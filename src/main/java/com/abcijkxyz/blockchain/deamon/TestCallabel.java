package com.abcijkxyz.blockchain.deamon;

import java.util.List;
import java.util.concurrent.Callable;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.contract.Transfer;
import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.data.Transaction;
import com.abcijkxyz.blockchain.mapper.AccountMapper;
import com.abcijkxyz.blockchain.mapper.BlockMapper;
import com.abcijkxyz.blockchain.mapper.SpentInfoMapper;
import com.abcijkxyz.blockchain.mapper.TransactionMapper;
import com.abcijkxyz.blockchain.mapper.TxDataMapper;
import com.abcijkxyz.blockchain.smart.SmartVM;
import com.abcijkxyz.blockchain.util.Context;
import com.abcijkxyz.blockchain.util.ContextUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class TestCallabel implements Callable<Transaction> {

	private TxDataMapper txDataMapper;

	private AccountMapper accountMapper;

	private SpentInfoMapper spentInfoMapper;

	private BlockMapper blockMapper;

	private TransactionMapper transactionMapper;

	private SmartVM smartVM;

	private TxData txData;
	private Long height;

//    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	@Override
	public Transaction call() throws Exception {
		// readWriteLock.readLock().lock();
		Transaction transaction = null;

		try {
			String contract = txData.getContract();
			String from = txData.getFrom();
			String to = txData.getTo();
			Long amount = txData.getAmount();
			Long timestamp = txData.getTimestamp();
			// TODO 输入 TxInput
			List<SpentInfo> txInputs = spentInfoMapper.findTxOutput(from);
			if (txInputs != null) {
				// List<TxOutput> txoutput = null;
				switch (contract) {
				case "Coinbase":
					break;
				case "Transfer":
					String result = smartVM.callContract(txData, new Transfer(to, amount));
					break;
				case "Example":
					break;
				default:
					break;
				}

				// ctx
				// gas
				// output = SingleTransfer 》 ctx
				Context context = ContextUtil.getContext();
				// TODO 运行失败惩罚机制
				if (context != null) {
					List<SpentInfo> txInputs_ctx = context.getTxInputs();
					List<SpentInfo> txOutputs_ctx = context.getTxOutputs();

					if (txInputs_ctx != null && txInputs_ctx.size() > 0 && txOutputs_ctx != null && txOutputs_ctx.size() > 0) {
						transaction = new Transaction(height, contract, from, System.currentTimeMillis(), txData, txInputs_ctx, txOutputs_ctx);
						transactionMapper.insert(transaction);

						// 超过了最大可传参数 32767
						// spentInfoMapper.updateTxInputs(transaction.getHash(), txInputs_ctx);
						updateTxInputs(transaction.getHash(), txInputs_ctx);

						spentInfoMapper.insertTxOutputs(transaction.getHash(), height, txOutputs_ctx);
					} else {
						txDataMapper.delete(txData.getHash());
					}
					// sql= execSQL(output)

					// TODO
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(" callContract error ", e);
			txDataMapper.delete(txData.getHash());
		} finally {
			// readWriteLock.readLock().unlock();
		}
		return transaction;
	}

	public void updateTxInputs(String inputTxHash, List<SpentInfo> txInputs) {
		int numberBatch = 32767 / 5; // 每一次插入的最大行数
		double number = txInputs.size() * 1.0 / numberBatch;
		int n = ((Double) Math.ceil(number)).intValue(); // 向上取整
		for (int i = 0; i < n; i++) {
			int end = numberBatch * (i + 1);
			if (end > txInputs.size()) {
				end = txInputs.size(); // 如果end不能超过最大索引值
			}
			spentInfoMapper.updateTxInputs(inputTxHash, txInputs.subList(numberBatch * i, end)); // 插入数据库
		}
	}
}
