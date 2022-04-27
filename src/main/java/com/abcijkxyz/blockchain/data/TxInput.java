package com.abcijkxyz.blockchain.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxInput {

	// private String hash;


	// 交易的哈希
	private String inputTxHash;
	
	// 输入的顺序
	private Integer inputIndex;


	// TODO 输入用户的公钥，验证是该用户的输入？UTXO+账户模型，无法验证
	private String inputPublicKey;

	// TODO 输出交易的哈希？
	private String outputTxHash;

	// TODO  输出余额的顺序？
	private Integer outputIndex;

	// ------------- 查询限定条件，解锁条件，冗余字段 ---------------------------------------

	private String scene;

	private Long ecosystem;

	private String asset;

	private String contract;

}
