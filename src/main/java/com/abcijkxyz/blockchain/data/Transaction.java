package com.abcijkxyz.blockchain.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.util.ByteUtils;
import com.abcijkxyz.blockchain.util.Hash256Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
//	INSERT INTO "public"."transaction"("hash", "time", "height", "address", "resultSQL", "ecosystem", "contract", "version", "data") VALUES ('1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

	// 区块奖励
	private static final long SUBSIDY = 100;

	// 交易哈希
	private String hash;

	private Long time;

	private Long height;

	// 版本
	private Long version;

	private List<SpentInfo> txInputs;

	private List<SpentInfo> txOutputs;

	// TODO 交易数据？ byte[] ,测试用 String json
	private TxData txData;
	private String data;

	// 调用的合约名称
	private String contract;

	// 生态ID
	private Long ecosystem;

	// 发起交易的用户地址
	private String address;

	// TODO 合约执行完成之后，输出的SQL日志？ byte[] ,测试用 String json
	private String resultSQL;

	
	public Transaction(Long height, String contract, String address, Long time, TxData txData) {
		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packString(contract)//
//					.packString(txData)//
					.packString(address)//
					.packLong(height)//
					.packLong(time)//
					.packString(txData.toString());
			
			packer.close();
			byte[] data = packer.toByteArray();
//			this.data = Hex.encodeHexString(data) ;
			this.hash = Hash256Util.hash256(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.height = height;
		this.contract = contract;
		this.address = address;
		this.time = time;
		this.txData = txData;
		this.data = txData.toString();
	}

	public Transaction(Long height, String contract, String address, Long time, TxData txData, List<SpentInfo> txInputs, List<SpentInfo> txOutputs) {
		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packString(contract)//
//					.packString(txData)//
					.packString(address)//
					.packLong(height)//
					.packLong(time)//
					.packString(txData.toString());
			packer.close();
			byte[] data = packer.toByteArray();
//			this.data = Hex.encodeHexString(data) ;
			this.hash = Hash256Util.hash256(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.txInputs = txInputs;
		this.txOutputs = txOutputs;

		this.height = height;
		this.contract = contract;
		this.address = address;
		this.time = time;
		this.txData = txData;
		this.data = txData.toString();
	}

	/**
	 * 创建CoinBase交易
	 *
	 * @param  to   收账的钱包地址
	 * @param  data 解锁脚本数据
	 * @return
	 */
	public static Transaction newCoinbaseTX(String to, Long height) {
//		if (StringUtils.isBlank(data)) {
//			data = String.format("Reward to '%s'", to);
//		}
		// 创建交易输入
//        TXInput txInput = new TXInput(new byte[]{}, -1, data);
//		TxInput txInput = new TxInput();

		// 创建交易
		Transaction tx = new Transaction(height, "Coinbase", to, System.currentTimeMillis(), null);
		// 设置交易ID
//		tx.setTxId();

		// 创建交易输出
//      TXOutput txOutput = new TXOutput(SUBSIDY, to);
		TxOutput txOutput = new TxOutput(ByteUtils.ZERO_HASH, to, SUBSIDY, 0, height);
		List<SpentInfo> txOutputs = new ArrayList<SpentInfo>();
//		txOutputs.add(txOutput);
		tx.setTxOutputs(txOutputs);
		return tx;
	}

	public Transaction(TxData txData) {
		this.txData = txData;
	}

}
