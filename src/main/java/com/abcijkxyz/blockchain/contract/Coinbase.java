package com.abcijkxyz.blockchain.contract;

import com.abcijkxyz.blockchain.smart.SmartContract;

public class Coinbase implements SmartContract {

	@Override
	public Boolean conditions() {
		return true;
	}

	@Override
	public String action() {
		return null;

	}

}
