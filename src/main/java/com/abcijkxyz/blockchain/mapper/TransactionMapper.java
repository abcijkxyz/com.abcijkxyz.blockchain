package com.abcijkxyz.blockchain.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.abcijkxyz.blockchain.data.Transaction;

@Mapper
public interface TransactionMapper {
//	INSERT INTO "public"."transaction"("hash", "time", "height", "address", "resultSQL", "ecosystem", "contract", "version", "data") VALUES ('1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

	@Insert("""

			INSERT INTO "transaction"("hash", "time", "height", "address", "resultSQL", "ecosystem", "contract", "version", "txData")
			VALUES(#{hash},#{time},#{height},#{address},#{resultSQL},#{ecosystem},#{contract},#{version},#{txData})

			""")
	int insert(Transaction transaction);

	@Delete("""
			
			DELETE FROM "transaction" 
			
			""")
	int deleteAll();

}
