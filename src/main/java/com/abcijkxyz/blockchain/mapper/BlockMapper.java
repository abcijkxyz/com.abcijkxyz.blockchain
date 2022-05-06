package com.abcijkxyz.blockchain.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.abcijkxyz.blockchain.data.Block;

@Mapper
public interface BlockMapper {
//	INSERT INTO "public"."block"("hash", "prevBlockHash", "height", "time", "data", "txNum", "merkleRoot", "version") VALUES ('1', NULL, 1, NULL, NULL, NULL, NULL, NULL);


	@Insert("""

			INSERT INTO "block" ("height","txNum","hash","prevBlockHash","time","data","merkleRoot","version") 
			VALUES(#{height},#{txNum},#{hash},#{prevBlockHash},#{time},#{data},#{merkleRoot},#{version})

			""")
	int insert(Block block);

	@Delete("""

			DELETE FROM "block"

			""")
	int deleteAll();

	@Select("""

			SELECT count(*) FROM "block"

			""")
	int countAll();

	@Select("""

			SELECT "hash", "prevBlockHash", "height", "time", "data", "txNum", "merkleRoot", "version" FROM "block" ORDER BY height DESC LIMIT 1

			""")
	Block getLastBlock();

}
