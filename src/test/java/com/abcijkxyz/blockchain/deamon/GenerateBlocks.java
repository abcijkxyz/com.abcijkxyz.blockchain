package com.abcijkxyz.blockchain.deamon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.abcijkxyz.blockchain.function.TransferHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.contract.Transfer;
import com.abcijkxyz.blockchain.data.Block;
import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.data.Transaction;
import com.abcijkxyz.blockchain.data.TxOutput;
import com.abcijkxyz.blockchain.mapper.AccountMapper;
import com.abcijkxyz.blockchain.mapper.BlockMapper;
import com.abcijkxyz.blockchain.mapper.SpentInfoMapper;
import com.abcijkxyz.blockchain.mapper.TransactionMapper;
import com.abcijkxyz.blockchain.mapper.TxDataMapper;
import com.abcijkxyz.blockchain.smart.SmartVM;
import com.abcijkxyz.blockchain.util.ByteUtils;
import com.abcijkxyz.blockchain.util.Context;
import com.abcijkxyz.blockchain.util.ContextUtil;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class GenerateBlocks {
	private final static String UNKNOWN_TABLE = "UNKNOWN_TABLE";

	@Autowired
	private TxDataMapper queueMapper;

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private SpentInfoMapper spentInfoMapper;

	@Autowired
	private BlockMapper blockMapper;

	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private SmartVM smartVM;

	@Scheduled(fixedRate = 4000)
	@Transactional
	public void generateBlocks2() {
		List<TxData> list = queueMapper.getFirst100TxQueue();

		packagingBlock(list);

	}

	private void packagingBlock(List<TxData> list) {

		// TODO 交易分组
		Map<Integer, List<TxData>> groupMap = groupTxs(list);

	//	System.out.println("groupMap:" + groupMap);

		Long height = 0L;
		String prevBlockHash = ByteUtils.ZERO_HASH;

		Block lastBlock = blockMapper.getLastBlock();
		if (lastBlock != null) {
			height = lastBlock.getHeight() + 1;
			prevBlockHash = lastBlock.getHash();

		}

		//
		// // TODO 执行交易里面的合约逻辑
		// // TODO 并行分组执行交易
		List<String> txHashs = execute(groupMap, height);

		// List<Transaction> transactions = new ArrayList<Transaction>();
		//
		// transactions.add(Transaction.newCoinbaseTX("1111-1111-1111-1111-1111", height));
		//
		// Block.newBlock(prevBlockHash, transactions, height);

		// Long height = 0L;
		// String parent = "0000000000000000000000000000000000000000000000000000000000000000";
		// Block lastBlock = blockMapper.getLastBlock();
		//
		// if (lastBlock != null) {
		// height = lastBlock.getHeight() + 1;
		// parent = lastBlock.getHash();
		// }
		// Long timestamp = new Date().getTime();
		// System.out.println("txhash\t" + txhash);
		// blockMapper.insert(new Block(parent, height, timestamp, txhash));
	}

	// @Scheduled(fixedRate = 4000)
	// @Transactional
	// public void generateBlocks() {
	// log.debug("generateBlocks");
	//
	// List<TxQueue> list = queueMapper.getFirst100TxQueue();
	//
	// List<String> txhash = new ArrayList<String>();
	// Integer height = 0;
	// String parent = "0000000000000000000000000000000000000000000000000000000000000000";
	// Block lastBlock = blockMapper.getLastBlock();
	//
	// if (lastBlock != null) {
	// height = lastBlock.getHeight() + 1;
	// parent = lastBlock.getHash();
	// }
	//
	// if (list != null) {
	// // mint
	// Transaction mint = NFTMinter(height);
	// txhash.add(mint.getHash());
	//
	// List<String> qHashs = new ArrayList<String>();
	// for (TxQueue txData : list) {
	// String contract = txData.getContract();
	// String from = txData.getFrom();
	// String to = txData.getTo();
	// Long amount = txData.getAmount();
	// Long timestamp = txData.getTimestamp();
	// qHashs.add(txData.getHash());
	// switch (contract) {
	// case "Transfer":
	// Transaction tx = Transfer(from, to, amount, timestamp, height);
	// if (tx != null) {
	// txhash.add(tx.getHash());
	// }
	// break;
	// default:
	// break;
	// }
	// }
	// queueMapper.deleteByHashs(qHashs);
	// }
	// if (txhash.size() > 0) {
	// Long timestamp = new Date().getTime();
	// System.out.println("txhash\t" + txhash);
	// blockMapper.insert(new Block(parent, height, timestamp, txhash));
	// }
	//
	// }
	// NFTMinter
	private Transaction Coinbase(Long height) {
		// Long timestamp = new Date().getTime();
		// String txhash = null;
		// String from = "0000-0000-0000-0000-0000";
		// String account = "1111-1111-1111-1111-1111";
		// Long amount = 50L;
		// String to = account;
		// String inputHash = "0000000000000000000000000000000000000000000000000000000000000000";
		//
		Transaction transaction = null;// new Transaction(from, to, amount, timestamp, inputHash, height);
		// transactionMapper.insert(transaction);
		// txhash = transaction.getHash();
		// Boolean targetable = true;
		// Output output = new Output(height, account, amount, timestamp, targetable, 0, txhash);
		// outputMapper.insert(output);
		//
		return transaction;
	}

	//
	private Transaction Transfer(String from, String to, Long amount, Long timestamp, Long height) {
		// log.debug("Transfer");
		//
		Transaction transaction = null;
		// List<TransactionInput> inputsFrom = transactionMapper.getTransactionInput(from);
		//
		//// List<Output> outputsFrom = outputMapper.getByAccount(from);
		//
		// Account accountTo = accountMapper.getByAccount(to);
		//
		// if(accountTo==null)
		// {
		// accountMapper.insert(to);
		// }
		// Long amountFrom = 0L;
		// List<String> inputTxHashs = new ArrayList<String>();
		// List<String> inputOHashs = new ArrayList<String>();if(inputsFrom!=null)
		// {
		// for (TransactionInput input : inputsFrom) {
		// amountFrom += input.getOamount();
		// inputTxHashs.add(input.getThash());
		// inputOHashs.add(input.getOhash());
		// }
		// }if(amountFrom>=amount)
		// {
		//
		// Boolean targetable = true;
		// transaction = new Transaction(from, to, amount, timestamp, Joiner.on(",").join(inputTxHashs), height);
		// String txhash = transaction.getHash();
		// transactionMapper.insert(transaction);
		//
		// timestamp = new Date().getTime();
		//
		// Output output0 = new Output(height, from, amountFrom - amount, timestamp, targetable, 0, txhash);
		// Output output1 = new Output(height, to, amount, timestamp, targetable, 1, txhash);
		//
		// outputMapper.deleteByHashs(inputOHashs);
		// outputMapper.insert(output0);
		// outputMapper.insert(output1);
		//
		// }else
		// {
		// log.warn("There is not enough balance");
		// }
		//
		return transaction;
	}

	/**
	 * concurrent execution
	 *
	 * @param  groupMap
	 * @return
	 */
	private List<String> execute(Map<Integer, List<TxData>> groupMap, Long height) {
		int cpus = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = new ThreadPoolExecutor(2, cpus, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardPolicy());
		LinkedBlockingQueue<String> collectData = new LinkedBlockingQueue<>();// Collect all executed data
		long start = System.currentTimeMillis();
		while (!groupMap.isEmpty()) {
			Iterator<Map.Entry<Integer, List<TxData>>> it = groupMap.entrySet().iterator();
			Map<Integer, List<TxData>> excuGroupMap = new HashMap<>();// Collect the current groupings that can be executed in parallel
			int size = 0;
			while (it.hasNext()) {
				Entry<Integer, List<TxData>> entry = it.next();
				// System.out.println("group = " + entry.getKey() + ", value = " + entry.getValue());
				boolean flag = false;
				if (excuGroupMap.size() > 0 && entry.getValue().get(0).getTables().contains(UNKNOWN_TABLE)) {
					flag = true;
					break;
				} else {
					size += entry.getValue().size();
					excuGroupMap.put(entry.getKey(), entry.getValue());
					if (excuGroupMap.size() > 0) {
						if (entry.getValue().get(0).getTables().contains(UNKNOWN_TABLE)) {
							flag = true;
							it.remove();
							break;
						}
					}
				}
				if (flag) {
					break;
				}
				it.remove();
			}
	//		System.out.println("--------------Execute transactions in parallel-----------------");

			CountDownLatch countDownLatch = new CountDownLatch(size);
			excuGroupMap.forEach((key, value) -> {
				threadPool.execute(() -> {
					for (TxData txData : value) {
						try {
							if ((System.currentTimeMillis() - start) <= 2000) {
								// TimeUnit.MILLISECONDS.sleep(10);
								// TODO execute transaction

								callContract(txData, height);
								// log.info("transaction group {}, transaction address: {}", key, tx);
							}
							countDownLatch.countDown();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			});
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (groupMap.isEmpty()) {
					threadPool.shutdown();
				}
			}
		}
		log.info("Total number of transactions executed: {}", collectData.size());
		return new ArrayList<String>(collectData);
	}

	// TODO 模拟虚拟机 执行 合约 Contract
	private Transaction callContract(TxData txData, Long height) {
		String contract = txData.getContract();
		String from = txData.getFrom();
		String to = txData.getTo();
		Long amount = txData.getAmount();
		Long timestamp = txData.getTimestamp();

		Transaction tx = null;// Transfer(from, to, amount, timestamp, 0);
		// if (tx != null) {
		// collectData.add(tx.getHash());
		// // collectData.add(tx);
		// }

		// TODO 输入 TxInput
		List<SpentInfo> txInputs = spentInfoMapper.findTxOutput2(from);
		if (txInputs != null) {
//			List<TxOutput> txoutput = null;
			switch (contract) {
			case "Coinbase":
//				Coinbase(txData, txInputs, height);
				break;
			case "Transfer":
//				Transfer(txData, txInputs, height);
				String result = smartVM.callContract(txData, new Transfer(to, amount));
				break;
			case "Example":
//				Example(txData, txInputs, height);
				break;
			default:
//				return null;
				break;
			} 

//			ctx
			// gas
			// output = SingleTransfer 》 ctx
			Context context = ContextUtil.getContext();
			List<SpentInfo> txInputs_ctx = context.getTxInputs();
			List<SpentInfo> txOutputs_ctx = context.getTxOutputs();

			Transaction transaction = new Transaction(height, contract, from, System.currentTimeMillis(), txData, txInputs_ctx, txOutputs_ctx);
			transactionMapper.insert(transaction);

			spentInfoMapper.updateTxInputs(transaction.getHash(), txInputs_ctx);

			spentInfoMapper.insertTxOutputs(transaction.getHash(),height, txOutputs_ctx);
			// sql= execSQL(output)

		}
		return tx;
	}

	// 合约挖矿
//	private Transaction Coinbase(TxData txData, List<SpentInfo> txInputs, Long height) {
//		TransferHelper.singleTransfer(txData, txInputs);

//		return null;

//	}

	// 合约 转账
//	private Transaction Transfer(TxData txData, List<SpentInfo> txInputs, Long height) {

	// TODO 处理 / 输出 TxOutput
	// 给一个用户转账
//		TransferHelper.singleTransfer(txData, txInputs, height);
	// 给多个用户转账
	// MultipleTransfer
	// ctx

//		return null;

//	}

	// 合约 样例
//	private Transaction Example(TxData txData, List<SpentInfo> txInputs, Long height) {

	// sql Example table

//		return null;

//	}

	/**
	 * Group by contract occupancy resources and associated accounts
	 *
	 * @param  txs
	 * @return
	 */
	private static Map<Integer, List<TxData>> groupTxs(List<TxData> txs) {
		int group = 0;
		HashMap<Integer, List<TxData>> groupMap = new HashMap<>();
		while (!txs.isEmpty()) {
			TxData tx0 = txs.get(0);
			Set<String> tableSet = new HashSet<>();
			tableSet.addAll(tx0.getTables());
			List<TxData> txadd = new ArrayList<>();
			Iterator<TxData> iterator = txs.iterator();
			while (iterator.hasNext()) {
				TxData tx = iterator.next();
				boolean flag = false;
				if (txadd.size() > 0 && tx.getTables().contains(UNKNOWN_TABLE)) {
					flag = true;
					group = extracted(group, groupMap, txadd);
					break;
				} else {
					if (tableSet.contains(UNKNOWN_TABLE)) {
						flag = true;
						txadd.add(tx);
						iterator.remove();
						groupMap.put(group, txadd);
						group++;
						break;
					} else {
						txadd.add(tx);
						iterator.remove();
						if (txs.isEmpty()) {
							group = extracted(group, groupMap, txadd);
						}
					}
				}
				if (flag) {
					break;
				}
			}
		}
		return groupMap;
	}

	private static int extracted(int group, HashMap<Integer, List<TxData>> groupMap, List<TxData> txadd) {
		while (!txadd.isEmpty()) {
			TxData vertex = txadd.get(0);
			Set<String> set = new HashSet<>();
			set.add(vertex.getFrom());
			set.add(vertex.getTo());
			set.addAll(vertex.getTables());
			List<TxData> tempTxadd = new ArrayList<>();
			Iterator<TxData> iterator1 = txadd.iterator();
			while (iterator1.hasNext()) {
				TxData tx1 = iterator1.next();
				if (set.contains(tx1.getFrom()) || set.contains(tx1.getTo())) {
					set.add(tx1.getFrom());
					set.add(tx1.getTo());
					set.addAll(tx1.getTables());
					tempTxadd.add(tx1);
					iterator1.remove();
				} else {
					for (String value : tx1.getTables()) {
						if (set.contains(value)) {
							set.add(tx1.getFrom());
							set.add(tx1.getTo());
							set.addAll(tx1.getTables());
							tempTxadd.add(tx1);
							iterator1.remove();
							break;
						}
					}
				}
			}
			groupMap.put(group, tempTxadd);
			group++;
		}
		return group;
	}
}
