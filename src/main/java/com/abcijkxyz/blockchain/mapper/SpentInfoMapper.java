package com.abcijkxyz.blockchain.mapper;

import java.util.List;
import java.util.Vector;

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

	@Insert("""
			<script>

			INSERT INTO "spent_info"("inputTxHash", "inputIndex", "inputPublicKey", "outputTxHash", "outputIndex", "outputAddress", "outputValue", "scene", "ecosystem", "contract", "height", "asset")
			VALUES
				<foreach collection="list" item="item" index="" separator=",">
					(#{item.inputTxHash}, #{item.inputIndex}, #{item.inputPublicKey}, #{item.outputTxHash}, #{item.outputIndex}, #{item.outputAddress}, #{item.outputValue}, #{item.scene}, #{item.ecosystem}, #{item.contract}, #{item.height}, #{item.asset})
				</foreach>
			ON CONFLICT ("outputTxHash","outputAddress","outputIndex")
			DO UPDATE SET "inputTxHash" = EXCLUDED."inputTxHash" , "inputIndex" = excluded."inputIndex" , "inputPublicKey" = excluded."inputPublicKey"
				WHERE spent_info."outputTxHash" = EXCLUDED."outputTxHash"
					AND spent_info."outputAddress" = EXCLUDED."outputAddress"
					AND spent_info."outputIndex" = EXCLUDED."outputIndex"

			</script>
			""")
	int insertOrUpdateTxOutputs(@Param("list") List<SpentInfo> txOutputs);

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
	@Deprecated
	int insertTxOutputs(@Param("outputTxHash") String outputTxHash, @Param("height") Long height, @Param("list") List<SpentInfo> txOutputs);

	// 更新花费使用情况
	@Update("""
			<script>

			UPDATE spent_info set "inputTxHash"=t."inputTxHash","inputIndex"=t."inputIndex" from (VALUES
				<foreach collection="list" item="item" index="" separator=",">
					(#{inputTxHash},#{item.inputIndex},#{item.outputTxHash},#{item.outputAddress},#{item.outputIndex})
				</foreach>
			) as t ("inputTxHash","inputIndex","outputTxHash","outputAddress","outputIndex")
			WHERE
				spent_info."outputTxHash" = t."outputTxHash"
			AND spent_info."outputAddress" = t."outputAddress"
			AND spent_info."outputIndex" = t."outputIndex"

			</script>
						""")
	@Deprecated
	int updateTxInputs(@Param("inputTxHash") String inputTxHash, @Param("list") List<SpentInfo> txInputs);

	// 可花费的输出
	@Select("""
			<script>
			SELECT
				si."inputTxHash",
					row_number() over (PARTITION BY si."outputAddress"  order by si.height ASC,	tr."time" ASC) AS "inputIndex",
				si."inputPublicKey",
				si."outputTxHash",
				si."outputIndex",
				si."outputAddress",
				si."outputValue",
				si.scene,
				si.ecosystem,
				si.contract,
				si.height,
				si.asset
			FROM
				spent_info si
				LEFT JOIN "transaction" AS tr ON si."outputTxHash" = tr.hash
			WHERE
				"outputAddress" IN
				<foreach collection="list" item="item" open="(" close=")" separator="," >
					#{item}
				</foreach>
				AND si."inputTxHash" IS NULL
			ORDER BY si."outputAddress",
				si.height ASC,
				tr."time" ASC
			</script>
			""")
	Vector<SpentInfo> findTxOutputs(List<String> list);

	// 可花费的输出
	@Select("""
			SELECT
				si."inputTxHash",
			ROW_NUMBER() OVER() -1 as "inputIndex",
				si."inputPublicKey",
				si."outputTxHash",
				si."outputIndex",
				si."outputAddress",
				si."outputValue",
				si.scene,
				si.ecosystem,
				si.contract,
				si.height,
				si.asset
			FROM
				spent_info AS si
				LEFT JOIN "transaction" AS tr ON si."outputTxHash" = tr.hash
			WHERE
				si."outputAddress" = #{address}
				AND si."inputTxHash" IS NULL
			ORDER BY
				si.height ASC,
				tr."time" ASC

			""")
	@Deprecated
	List<SpentInfo> findTxOutput2(String address);

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
			WHERE "inputTxHash" IS NULL    GROUP BY "outputAddress"  HAVING SUM ("outputValue") >1000
			ORDER BY  "outputValue"  DESC LIMIT 10000

			""")
	List<SpentInfo> getMaxManyTxOutputs();

	@Select("""

			SELECT "account".address AS "outputAddress",( CASE WHEN outputs."outputValue" > 0 THEN outputs."outputValue" ELSE 0 END ) "outputValue"
			FROM "account"
			LEFT JOIN ( SELECT "outputAddress", SUM ( "outputValue" ) AS "outputValue" FROM spent_info
			WHERE "inputTxHash" IS NULL GROUP BY "outputAddress" ) outputs ON outputs."outputAddress" = "account".address
			ORDER BY "outputValue" ASC LIMIT 10000

			""")
	List<SpentInfo> getMinManyTxOutputs();

	// 总余额
	@Select("""

			SELECT SUM("outputValue") FROM "spent_info" WHERE "inputTxHash" IS NULL

			""")
	Long getTotalTxOutputs();
}
