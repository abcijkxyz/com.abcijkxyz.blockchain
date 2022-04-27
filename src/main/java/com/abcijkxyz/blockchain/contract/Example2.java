package com.abcijkxyz.blockchain.contract;

import com.abcijkxyz.blockchain.smart.SmartContract;

public class Example2 implements SmartContract {

	public Boolean conditions() {

		return true;
	}

	public String action() {
		System.out.println("Example2 action ");
		return "Hello World !";
	}
}