package com.abcijkxyz.blockchain.deamon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.mapper.AccountMapper;
import com.abcijkxyz.blockchain.mapper.SpentInfoMapper;
import com.abcijkxyz.blockchain.mapper.TxDataMapper;
import com.abcijkxyz.blockchain.util.ByteUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MockTxMultiple2 {
//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private SpentInfoMapper spentInfoMapper;

	@Autowired
	private TxDataMapper queueMapper;

	/**
	 * Mock real-time transactions
	 */
	@Scheduled(fixedRate = 500)
	public void mockTx() {
//		log.debug("start mockTx");
		try {
			int queueNum = queueMapper.countAll();
			// 保持一定量的交易，不让账号转超过账上的钱
			if (queueNum >= 50000) {
				return;
			}
			List<SpentInfo> maxOutputs = spentInfoMapper.getMaxManyTxOutputs();
			List<SpentInfo> minOutputs = spentInfoMapper.getMinManyTxOutputs();
			int accountNum = accountMapper.countAll();
			// 初始化一批账户
			if (accountNum < 10000) {
				for (int i = accountNum; i <= 10000; i++) {
					String address = "1111-1111-1111-1111-" + String.format("%04d", i);
					accountMapper.insertOnlyAddress(address);
					List<SpentInfo> outputs = new ArrayList<SpentInfo>();
					SpentInfo output = new SpentInfo();
					output.setOutputAddress(address);
					output.setOutputValue(10000L);
					output.setOutputIndex(0);
					outputs.add(output);
					spentInfoMapper.insertTxOutputs(ByteUtils.ZERO_HASH, 0L, outputs);
				}
			}
//			String gasOutputAddress = "9999-9999-9999-9999-9999";
			if (maxOutputs != null && minOutputs != null) {
				for (int i = 0; i < maxOutputs.size(); i++) {
					SpentInfo maxOutput = maxOutputs.get(i);
					String from = maxOutput.getOutputAddress();
					Long amount = 1L;// maxOutput.getOutputValue();
					String to = null;
//					if (gasOutputAddress.equals(from)) {
//						continue;
//					}

//					if (accountNum < 1000 && maxOutput.getOutputValue() > 10000L) {
//						amount = 1000L;
//					}

					if (accountNum < 30000) {

						to = UUID.randomUUID().toString();
						accountMapper.insertOnlyAddress(to);
						accountNum++;
					} else {
						SpentInfo minOutput = minOutputs.get(i);
						to = minOutput.getOutputAddress();
					}
					Random random = new Random();

					queueMapper.insert(new TxData("Transfer", from, to, amount, new Date().getTime()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		log.debug("end mockTx");
	}

}
