package com.abcijkxyz.blockchain.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import com.abcijkxyz.blockchain.util.Hash256Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TxData {
//	INSERT INTO "public"."tx_queue"("hash", "from", "to", "amount", "timestamp", "contract", "data") VALUES ('1', '1', '1', 1, 1, '1', NULL);

	private String hash;
//  byte[] 
	private String data;
	// data extra
	private String contract;
	private String from; // from account
	private String to; // to account
	private Long amount;
	private Long timestamp;

	private List<String> tables = new ArrayList<>();

	public TxData(String contract, String from, String to, Long amount, Long timestamp) {
		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packString(contract)//
					.packString(from)//
					.packString(to)//
					.packLong(amount)//
					.packLong(timestamp);
			packer.close();
			byte[] data = packer.toByteArray();
			this.data = Hex.encodeHexString(data);
			this.hash = Hash256Util.hash256(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.contract = contract;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public TxData(String hash, String contract, String from, String to, Long amount, Long timestamp) {
		this.hash = hash;
		this.contract = contract;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.timestamp = timestamp;
	}

}
