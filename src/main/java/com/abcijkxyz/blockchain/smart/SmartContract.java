package com.abcijkxyz.blockchain.smart;

public interface SmartContract {

	public Boolean conditions();

	@Contract
	public String action();

}
