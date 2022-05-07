package com.abcijkxyz.blockchain.function;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferMsg {
	private String outputAddress;
	private Long outputValue;
}
