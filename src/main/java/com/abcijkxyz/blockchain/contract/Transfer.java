package com.abcijkxyz.blockchain.contract;

import com.abcijkxyz.blockchain.function.TransferHelper;
import com.abcijkxyz.blockchain.smart.SmartContract;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transfer implements SmartContract {

	private String outputAddress;
	private Long outputValue;

	@Override
	public Boolean conditions() {
		return true;
	}

	@Override
	public String action() {
//ctx
		Boolean bool = TransferHelper.singleTransfer(outputAddress, outputValue);
		if (bool) {
			return "Transfer OK";
		} else {
			
			return "Transfer ERROR";
		}

	}

}
