package com.abcijkxyz.blockchain.deamon;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.mapper.AccountMapper;
import com.abcijkxyz.blockchain.mapper.SpentInfoMapper;
import com.abcijkxyz.blockchain.mapper.TxDataMapper;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class MockTxSingle {
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
	@Scheduled(fixedRate = 2000)
	public void mockTx() {
//		log.debug("start mockTx");
		try {
			int queueNum = queueMapper.countAll();
			if (queueNum >= 4) {
				return;
			}
			SpentInfo maxOutput = spentInfoMapper.getMaxOneTxOutputs();
			if (maxOutput != null) {
				String from = maxOutput.getOutputAddress();
				Long amount = 1L;// maxOutput.getOutputValue();

				int accountNum = accountMapper.countAll();
				String to = null;
				if (accountNum < 1000) {
					to = UUID.randomUUID().toString();
					accountMapper.insertOnlyAddress(to);
				} else {
					SpentInfo minOutput = spentInfoMapper.getMinOneTxOutputs();
					to = minOutput.getOutputAddress();
				}

				queueMapper.insert(new TxData("Transfer", from, to, amount, new Date().getTime()));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

//		log.debug("end mockTx");
	}

}
