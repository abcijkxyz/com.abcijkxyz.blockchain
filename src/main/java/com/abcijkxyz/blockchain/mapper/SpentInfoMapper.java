package com.abcijkxyz.blockchain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.abcijkxyz.blockchain.data.SpentInfo;
//import com.abcijkxyz.blockchain.data.TxInput;
//import com.abcijkxyz.blockchain.data.TxOutput;

@Mapper
public interface SpentInfoMapper {
//	INSERT INTO "public"."spent_info"("inputTxHash", "inputIndex", "inputPublicKey", "outputTxHash", "outputIndex", "outputAddress", "outputValue", "scene", "ecosystem", "contract", "height", "asset") VALUES ('1', NULL, NULL, '1', 1, '1', NULL, NULL, NULL, NULL, 1, NULL);

	// 记录钱包地址的可用花费
	@Insert("""

			INSERT INTO "spent_info"("outputTxHash", "outputIndex", "outputAddress", "outputValue", "scene", "ecosystem", "contract", "height", "asset")
			VALUES
				<foreach collection="list" item="item" index="" separator=",">
					(#{outputTxHash},#{item.outputIndex},#{item.outputAddress},#{item.outputValue},#{item.scene},#{item.ecosystem},#{item.contract},#{item.height},#{item.asset})
				</foreach>
			ON CONFLICT ("outputTxHash", "outputIndex", "outputAddress")
			DO NOTHING
			""")
	int insertTxOutputs(  @Param("outputTxHash")String  outputTxHash, @Param("list") List<SpentInfo> txOutputs);

	
	
	
	// 更新花费使用情况
	@Update("""
			<script>

			UPDATE spent_info set inputTxHash=t.inputTxHash,inputIndex=t.inputIndex from (VALUES
				<foreach collection="list" item="item" index="" separator=",">
					(#{inputTxHash},#{item.inputIndex},#{item.outputTxHash},#{item.outputIndex},#{item.outputAddress})
				</foreach>
			) as t (inputTxHash,inputIndex,outputTxHash,outputIndex,outputAddress)
			WHERE
				spent_info.outputTxHash = t.outputTxHash
			AND spent_info.outputIndex = t.outputIndex
			AND spent_info.outputAddress = t.outputAddress

			</script>
						""")
	int updateTxInputs(@Param("inputTxHash")String  inputTxHash,@Param("list") List<SpentInfo> txInputs);

	// 可花费的输出
	@Select("""

			SELECT
				"inputTxHash",
				"inputIndex",
				"inputPublicKey",
				"outputTxHash",
				"outputIndex",
				"outputAddress",
				"outputValue",
				scene,
				ecosystem,
				contract,
				height,
				asset
			FROM
				spent_info
			WHERE "outputAddress" IN
				<foreach collection="list" item="item" index="index" open="(" close=")" separator=",">
				  #{item}
				</foreach>
				 AND "inputTxHash" IS NULL

			""")
	List<SpentInfo> findTxOutputs(@Param("list") List<String> address);

	
	

	// 可花费的输出
	@Select("""

			SELECT
				"inputTxHash",
				"inputIndex",
				"inputPublicKey",
				"outputTxHash",
				"outputIndex",
				"outputAddress",
				"outputValue",
				scene,
				ecosystem,
				contract,
				height,
				asset
			FROM
				spent_info
			WHERE "outputAddress" = #{address}

			""")
	List<SpentInfo> findTxOutput(String address);

	
	// 用来模拟交易，给其他人转账
	@Select("""

			SELECT "outputAddress",SUM ("outputValue") AS "outputValue" FROM spent_info
			WHERE "inputTxHash" IS NULL GROUP BY "outputAddress" ORDER BY  "outputValue"  DESC LIMIT 1

			""")
	SpentInfo getMaxTxOutputs();
//	List<SpentInfo> getMaxTxOutputs();

	// 用来模拟交易，接收转账的账号
	@Select("""

			SELECT "outputAddress",SUM ("outputValue") AS "outputValue" FROM spent_info
			WHERE "inputTxHash" IS NULL GROUP BY "outputAddress" ORDER BY  "outputValue"  ASC LIMIT 1

			""")
	SpentInfo getMinTxOutputs();
//	List<SpentInfo> getMinTxOutputs();
}
