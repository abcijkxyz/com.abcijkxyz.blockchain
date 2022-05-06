package com.abcijkxyz.blockchain.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.abcijkxyz.blockchain.util.ByteUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Block {
//	INSERT INTO "public"."block"("hash", "prevBlockHash", "height", "time", "data", "txNum", "merkleRoot", "version") VALUES ('1', NULL, 1, NULL, NULL, NULL, NULL, NULL);

	// 区块哈希
	private String hash;

	// 区块高度
	private Long height;

	// 上一区块哈希
	private String prevBlockHash;

	// TODO 区块数据 byte[] ,测试用 String json
	private String data;

	// 交易列表
	private List<Transaction> transactions;

	// 时间戳
	private Long time;

	// 交易数量
	private Integer txNum;

	// 默克尔根
	private String merkleRoot;

	// 版本
	private Long version;

	/**
	 * 创建创世区块
	 * 
	 * @param  coinbase
	 * @return
	 */
	public static Block newGenesisBlock(Transaction coinbase) {
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(coinbase);
		return Block.newBlock(ByteUtils.ZERO_HASH, transactions, 0);
	}

	/**
	 * 创建新的区块
	 *
	 * @param  previousHash 上一区块哈希
	 * @param  transactions 区块里面的交易
	 * @return
	 */
	public static Block newBlock(String prevBlockHash, List<Transaction> transactions, long height) {
//		"", prevBlockHash, transactions, Instant.now().getEpochSecond(), height, 0
		Block block = new Block();

		try {
			block.setPrevBlockHash(prevBlockHash);
			block.setHeight(height);
			block.setTransactions(transactions);
			block.setTxNum(transactions.size());
			block.setTime(Instant.now().getEpochSecond());
			block.setVersion(0L);
			byte[][] txHashs = new byte[transactions.size()][];

			for (int i = 0; i < transactions.size(); i++) {

				txHashs[i] = Hex.decodeHex(transactions.get(i).getHash());
			}

			// TODO 执行交易里面的合约逻辑
			// TODO 并行分组执行交易

			// TODO 执行完成合约输后出SQL日志

			byte[] merkleRootHash = new MerkleTree(txHashs).getRoot().getHash();
			block.setMerkleRoot(Hex.encodeHexString(merkleRootHash));
			byte[] headers = ByteUtils.merge(block.getPrevBlockHash().getBytes(), block.getMerkleRoot().getBytes(), ByteUtils.toBytes(block.getTime()));
			block.setHash(DigestUtils.sha256Hex(headers));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return block;
	}

}
