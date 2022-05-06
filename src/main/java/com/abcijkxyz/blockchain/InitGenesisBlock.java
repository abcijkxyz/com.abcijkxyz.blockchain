package com.abcijkxyz.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.contract.Coinbase;
import com.abcijkxyz.blockchain.contract.Example1;
import com.abcijkxyz.blockchain.contract.Example2;
import com.abcijkxyz.blockchain.contract.Transfer;
import com.abcijkxyz.blockchain.mapper.AccountMapper;
import com.abcijkxyz.blockchain.mapper.BlockMapper;
import com.abcijkxyz.blockchain.mapper.SpentInfoMapper;
import com.abcijkxyz.blockchain.mapper.TransactionMapper;
import com.abcijkxyz.blockchain.smart.SmartVM;

@Component
public class InitGenesisBlock implements CommandLineRunner {

	@Autowired
	private SmartVM smartVM;

	@Autowired
	private BlockMapper blockMapper;

	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private SpentInfoMapper spentInfoMapper;

	@Override
	public void run(String... args) throws Exception {

		// System.out.println("初始化第一个区块 newGenesisBlock");

	}

//	@Bean
	ApplicationRunner runner1(SmartVM smartVM) {
		return args -> {

//			 String from = "0000-0000-0000-0000-0000";
//			 String account = "1111-1111-1111-1111-1111";
			try {
				// Example1
				String contract = "Example1";
				String from = "1111-1111-1111-1111-1111";
				String to = "";
				Long amount = 0L;
				Long timestamp = System.currentTimeMillis();

				TxData txData = new TxData(contract, from, to, amount, timestamp);

//				String result = smartVM.callContract(txData, new Example1());
				String result = smartVM.callContract(txData, new Example1("test comment"));
		//		System.out.println("Example1:" + result);
			} catch (Exception e) {
				e.printStackTrace();
			}

		//	System.out.println("--------------");
			try {
				// Example2

				String contract = "Example2";
				String from = "1111-1111-1111-1111-1111";
				String to = "";
				Long amount = 0L;
				Long timestamp = System.currentTimeMillis();

				TxData txData = new TxData(contract, from, to, amount, timestamp);

				String result = smartVM.callContract(txData, new Example2());
		//		System.out.println("Example2:" + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		//	System.out.println("--------------");

			try {
				// Coinbase

				String contract = "Coinbase";
				String from = "0000-0000-0000-0000-0000";
				String to = "1111-1111-1111-1111-1111";
				Long amount = 0L;
				Long timestamp = System.currentTimeMillis();

				TxData txData = new TxData(contract, from, to, amount, timestamp);

				String result = smartVM.callContract(txData, new Coinbase());
	//			System.out.println("Coinbase:" + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		//	System.out.println("--------------");

			try {
				// Transfer

				String contract = "Transfer";
				String from = "1111-1111-1111-1111-1111";
				String to = "2222-2222-2222-2222-2222";
				Long amount = 1L;
				Long timestamp = System.currentTimeMillis();
				TxData txData = new TxData(contract, from, to, amount, timestamp);
				String result = smartVM.callContract(txData, new Transfer(from, amount));
	//			System.out.println("Transfer:" + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		//	System.out.println("--------------");

		};
	}

//	@Bean
	ApplicationRunner GenesisBlock(AccountMapper accountMapper, //
			SpentInfoMapper spentInfoMapper, //
			BlockMapper blockMapper, //
			TransactionMapper transactionMapper) {
		return args -> {

//			String account = UUID.randomUUID().toString();
//			String account = "0"; 
//			account="234e1345-2b92-454e-ac08-9ab95215d3cf";
			String account = "1111-1111-1111-1111-1111";

			{
				accountMapper.deleteAll();
				// Account
		//		System.out.println("account\t" + account);
				accountMapper.insertOnlyAddress(account);
			}

			Integer height = 0;
			Long amount = 100_000_000_000_000L;

			String txhash = null;
			Long timestamp = new Date().getTime();
			{

				// Transaction
				transactionMapper.deleteAll();

				String from = "0000-0000-0000-0000-0000";
				String to = account;
				String inputHash = "0000000000000000000000000000000000000000000000000000000000000000";

//				Transaction transaction = new Transaction(from, to, amount, timestamp, inputHash, height);
//				transactionMapper.insert(transaction);
//				txhash = transaction.getHash();
//
//				// Output
//				outputMapper.deleteAll();
//				Boolean targetable = true;
//				Output output = new Output(height, account, amount, timestamp, targetable, 0, txhash);
//				outputMapper.insert(output);
			}
			{
				blockMapper.deleteAll();
				// Block
				String parent = "0000000000000000000000000000000000000000000000000000000000000000";
				List<String> txhash2 = new ArrayList<String>();
				txhash2.add(txhash);
//				blockMapper.insert(new Block(parent, height, timestamp, txhash2));
			}

		};
	}

//	@Override
//	public void run1(ApplicationArguments args) throws Exception {
//	@Bean
	ApplicationRunner runner(SmartVM smartVM) {
		return args -> {
			try {
				// Transfer
				String contract = "Transfer";
				String from = "1111-1111-1111-1111-1111";
				String to = "2222-2222-2222-2222-2222";
				Long amount = 3L;
				Long timestamp = System.currentTimeMillis();
				TxData txData = new TxData(contract, from, to, amount, timestamp);
				String result = smartVM.callContract(txData, new Transfer(to, amount));
	//			System.out.println("Transfer:" + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
	//		System.out.println("--------------");
			try {
				// Example1
				String contract = "Example1";
				String from = "1111-1111-1111-1111-1111";
				String to = "";
				Long amount = 0L;
				Long timestamp = System.currentTimeMillis();
				TxData txData = new TxData(contract, from, to, amount, timestamp);
//			String result = smartVM.callContract(txData, new Example1());
				String result = smartVM.callContract(txData, new Example1("test comment"));
		//		System.out.println("Example1:" + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		//	System.out.println("--------------");
		};
	}
}