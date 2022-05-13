package com.abcijkxyz.blockchain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.data.Transaction;

@Mapper
public interface TransactionMapper {
//	INSERT INTO "public"."transaction"("hash", "time", "height", "address", "resultSQL", "ecosystem", "contract", "version", "data") VALUES ('1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

	@Insert("""

			INSERT INTO "transaction"("hash", "time", "height", "address", "resultSQL", "ecosystem", "contract", "version", "data")
			VALUES(#{hash},#{time},#{height},#{address},#{resultSQL},#{ecosystem},#{contract},#{version},#{data})

			""")
	int insert(Transaction transaction);

	@Delete("""

			DELETE FROM "transaction"

			""")
	int deleteAll();

	
	@Insert("""
			<script>

			INSERT INTO "transaction"("hash", "time", "height", "address", "resultSQL", "ecosystem", "contract", "version", "data")
			VALUES
				<foreach collection="list" item="item" index="" separator=",">
					(#{item.hash},#{item.time},#{item.height},#{item.address},#{item.resultSQL},#{item.ecosystem},#{item.contract},#{item.version},#{item.data})
				</foreach>
			ON CONFLICT ("hash")
			DO NOTHING

			</script>
			""")
	int insertTransactions(@Param("list") List<Transaction> list); 

}
