package com.abcijkxyz.blockchain.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxOutput {

	// private String hash;

	private Long height;

	private String outputAddress;

	private Long outputValue;

	private String outputTxHash;

	private Integer outputIndex;

	// ------------- 冗余字段 ，锁定条件，所有所有权 ---------------------------------------

	private String scene;

	private Long ecosystem;

	private String asset;

	private String contract;

	public TxOutput(String outputTxHash, String outputAddress, Long outputValue, Integer outputIndex, Long height) {
		this.outputTxHash = outputTxHash;
		this.outputAddress = outputAddress;
		this.outputValue = outputValue;
		this.outputIndex = outputIndex;
		this.height = height;
	}

}
