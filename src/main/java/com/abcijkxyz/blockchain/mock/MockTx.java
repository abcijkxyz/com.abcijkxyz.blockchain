package com.abcijkxyz.blockchain.mock;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.clients.admin.NewTopic;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.mapper.AccountMapper;
import com.abcijkxyz.blockchain.mapper.SpentInfoMapper;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class MockTx {
//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private SpentInfoMapper spentInfoMapper;

//	@Autowired
//	private BlockMapper blockMapper;

//	@Autowired
//	private TransactionMapper transactionMapper;

	private static final String TOPIC = "MockTransaction";
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendTx(String message) {
//		log.debug(message);
		this.kafkaTemplate.send(TOPIC, message);
	}

	@Bean
	public NewTopic createTopic() {
		return new NewTopic(TOPIC, 3, (short) 1);
	}

	/**
	 * Mock real-time transactions
	 */
	@Scheduled(fixedRate = 1000)
	public void mockTx() {
		log.debug("start mockTx");
		try {
			SpentInfo maxOutput = spentInfoMapper.getMaxTxOutputs();
			if (maxOutput != null) {
				String from = maxOutput.getOutputAddress();
//				Long amount = maxOutput.getOutputValue();

				int accountNum = accountMapper.countAll();
				String to = null;
				if (accountNum < 10) {
					to = UUID.randomUUID().toString();
				} else {
					SpentInfo minOutput = spentInfoMapper.getMinTxOutputs();
					to = minOutput.getOutputAddress();
				}

				Random random = new Random();
				MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
				packer.packString("Transfer")//
						.packString(from)//
						.packString(to)//
						.packLong(random.nextLong(1L))//
						.packLong(new Date().getTime());
				packer.close();
				String tx = Hex.encodeHexString(packer.toByteArray());
				sendTx(tx);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

//		log.debug("end mockTx");
	}

}
