package com.abcijkxyz.blockchain.data;

import lombok.Data;

@Data
public class SpentInfo {
//	INSERT INTO "public"."spent_info"("inputTxHash", "inputIndex", "inputPublicKey", "outputTxHash", "outputIndex", "outputAddress", "outputValue", "scene", "ecosystem", "contract", "height", "asset") VALUES ('1', NULL, NULL, '1', 1, '1', NULL, NULL, NULL, NULL, 1, NULL);

	// -------------  输出余额 ，用户账户余额  ---------------------------------------
	private Long height;

	// 输出花费给指定的的用户
	private String outputAddress;

	// 输出的余额
	private Long outputValue;

	// 输出花费的交易
	private String outputTxHash;

	// 输出花费的顺序 ， 固定顺序计算hash
	private Integer outputIndex;

	// 使用场景
	private String scene;

	// TODO 生态，一生态一资产？
	private Long ecosystem;

	// TODO 资产符号？是否需要 ，BTC、ETH、IBAX
	private String asset;

	// 合约锁定使用场景, 只能由该生态的该合约释放
	private String contract;

	// 锁定时间
	// 锁定区块
	// 锁定场景
	// TODO 锁定的流程？

	
	// -------------  花费余额，已使用标志   ---------------------------------------
	
	// 产生该输入的交易哈希
	private String inputTxHash;

	// 输入交易的顺序
	private Integer inputIndex;

	// TODO 验证改用户？是否可去除，UTXO+账户模型，无法验证
	private String inputPublicKey;

}
