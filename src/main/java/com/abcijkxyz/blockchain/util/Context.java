package com.abcijkxyz.blockchain.util;

import java.util.List;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.data.Transaction;
import com.abcijkxyz.blockchain.data.TxInput;
import com.abcijkxyz.blockchain.data.TxOutput;

import lombok.Data;

@Data
public class Context {

	private Long height;
	private String from; // from account
	private String to; // to account
	private String contract;
	private TxData txData;

	private List<SpentInfo> txInputs;

	private List<SpentInfo> txOutputs;

	private List<Transaction> transactions;

	private Transaction transaction;
}