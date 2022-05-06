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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.contract.Transfer;
import com.abcijkxyz.blockchain.data.Block;
import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.data.Transaction;
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

@Component
@Slf4j
public class GenerateBlocks2 {
	private final static String UNKNOWN_TABLE = "UNKNOWN_TABLE";

	@Autowired
	private TxDataMapper txDataMapper;

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

	@Scheduled(fixedDelay =    4000)
//	@Scheduled(fixedRate = 4000) // 不包含运行时间
	@Transactional
	public void generateBlocks2() {
		List<TxData> list = txDataMapper.getFirst100TxQueue();

		if (list != null && list.size() > 0) {
			packagingBlock(list);
		}

	}

	private void packagingBlock(List<TxData> list) {

		// TODO 交易分组
		Map<Integer, List<TxData>> groupMap = groupTxs(list);

		log.info("Total number of groupMap     executed: {}", groupMap.size());
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
		List<Transaction> txList = execute(groupMap, height);
		if (txList.size() > 0) {
			List<String> qHashs = new ArrayList<String>();
			for (Transaction transaction : txList) {
				qHashs.add(transaction.getTxData().getHash());
			}

			txDataMapper.deleteByHashs(qHashs);

			Block block = Block.newBlock(prevBlockHash, txList, height);

			blockMapper.insert(block);
		}
	}

	/**
	 * concurrent execution
	 *
	 * @param  groupMap
	 * @return
	 */
	private ArrayList<Transaction> execute(Map<Integer, List<TxData>> groupMap, Long height) {
		int cpus = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = new ThreadPoolExecutor(2, cpus, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardPolicy());
		LinkedBlockingQueue<Transaction> collectData = new LinkedBlockingQueue<>();// Collect all executed data
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
			// System.out.println("--------------Execute transactions in parallel-----------------");

			CountDownLatch countDownLatch = new CountDownLatch(size);
			excuGroupMap.forEach((key, value) -> {
				threadPool.execute(() -> {
					for (TxData txData : value) {
						try {
							if ((System.currentTimeMillis() - start) <= 2000) {
								// TimeUnit.MILLISECONDS.sleep(10);
								// TODO execute transaction

								Transaction tx = callContract(txData, height);
								if (tx != null) {
									collectData.add(tx);
								}
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
		return new ArrayList<Transaction>(collectData);
	}

	// TODO 模拟虚拟机 执行 合约 Contract
	private Transaction callContract(TxData txData, Long height) {
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
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(" callContract error ", e);
			txDataMapper.delete(txData.getHash());
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
