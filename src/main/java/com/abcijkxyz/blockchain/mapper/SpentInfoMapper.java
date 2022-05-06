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
	// INSERT INTO "public"."spent_info"("inputTxHash", "inputIndex", "inputPublicKey", "outputTxHash", "outputIndex", "outputAddress", "outputValue", "scene", "ecosystem", "contract", "height",
	// "asset") VALUES ('1', NULL, NULL, '1', 1, '1', NULL, NULL, NULL, NULL, 1, NULL);

	// 记录钱包地址的可用花费
	@Insert("""
			<script>

			INSERT INTO "spent_info"("outputTxHash", "outputAddress", "outputValue","outputIndex", "scene", "ecosystem", "contract", "height", "asset")
			VALUES
				<foreach collection="list" item="item" index="" separator=",">
					(#{outputTxHash},#{item.outputAddress},#{item.outputValue},#{item.outputIndex},#{item.scene},#{item.ecosystem},#{item.contract},#{height},#{item.asset})
				</foreach>
			ON CONFLICT ("outputTxHash","outputAddress","outputIndex")
			DO NOTHING

			</script>
			""")
	int insertTxOutputs(@Param("outputTxHash") String outputTxHash, @Param("height") Long height, @Param("list") List<SpentInfo> txOutputs);

	// 更新花费使用情况
	@Update("""
			<script>

			UPDATE spent_info set "inputTxHash"=t."inputTxHash","inputIndex"=t."inputIndex" from (VALUES
				<foreach collection="list" item="item" index="inputIndex" separator=",">
					(#{inputTxHash},#{inputIndex},#{item.outputTxHash},#{item.outputAddress},#{item.outputIndex})
				</foreach>
			) as t ("inputTxHash","inputIndex","outputTxHash","outputAddress","outputIndex")
			WHERE
				spent_info."outputTxHash" = t."outputTxHash"
			AND spent_info."outputAddress" = t."outputAddress"
			AND spent_info."outputIndex" = t."outputIndex"

			</script>
						""")
	int updateTxInputs(@Param("inputTxHash") String inputTxHash, @Param("list") List<SpentInfo> txInputs);

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
				 AND "inputTxHash" IS NULL AND "inputTxHash" IS NULL

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
			WHERE "outputAddress" = #{address} AND "inputTxHash" IS NULL

			""")
	List<SpentInfo> findTxOutput(String address);

	// 用来模拟交易，给其他人转账
	@Select("""

			SELECT "outputAddress",SUM ("outputValue") AS "outputValue" FROM spent_info
			
			WHERE "inputTxHash" IS NULL    GROUP BY "outputAddress"  HAVING SUM ("outputValue") >1000 ORDER BY  "outputValue"  DESC LIMIT 1

			""")
	 SpentInfo getMaxOneTxOutputs();

	// 用来模拟交易，接收转账的账号
	@Select("""

			SELECT "account".address AS "outputAddress",( CASE WHEN outputs."outputValue" > 0 THEN outputs."outputValue" ELSE 0 END ) "outputValue"

			FROM "account"

			LEFT JOIN ( SELECT "outputAddress", SUM ( "outputValue" ) AS "outputValue" FROM spent_info

			WHERE "inputTxHash" IS NULL GROUP BY "outputAddress" ) outputs ON outputs."outputAddress" = "account".address

			ORDER BY "outputValue" ASC LIMIT 1

			""")
	SpentInfo getMinOneTxOutputs();

	@Select("""

			SELECT "outputAddress",SUM ("outputValue") AS "outputValue" FROM spent_info
			
			WHERE "inputTxHash" IS NULL    GROUP BY "outputAddress"  HAVING SUM ("outputValue") >10000 ORDER BY  "outputValue"  DESC LIMIT 1000

			""")
	List<SpentInfo> getMaxManyTxOutputs();

	@Select("""

			SELECT "account".address AS "outputAddress",( CASE WHEN outputs."outputValue" > 0 THEN outputs."outputValue" ELSE 0 END ) "outputValue"

			FROM "account"

			LEFT JOIN ( SELECT "outputAddress", SUM ( "outputValue" ) AS "outputValue" FROM spent_info

			WHERE "inputTxHash" IS NULL GROUP BY "outputAddress" ) outputs ON outputs."outputAddress" = "account".address

			ORDER BY "outputValue" ASC LIMIT 1000

			""")
	List<SpentInfo> getMinManyTxOutputs();
}
