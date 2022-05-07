package com.abcijkxyz.blockchain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.abcijkxyz.blockchain.client.TxData;

@Mapper
public interface TxDataMapper {
//	INSERT INTO "public"."tx_data"("hash", "from", "to", "amount", "timestamp", "contract", "data") VALUES ('1', '1', '1', 1, 1, '1', NULL);

	@Insert("""

			INSERT INTO "tx_data" (hash,contract,"from","to",amount,timestamp) VALUES(#{hash},#{contract},#{from},#{to},#{amount},#{timestamp})
			ON CONFLICT ON CONSTRAINT t_tx_queue_pkey DO NOTHING

			""")
	void insert(TxData txQueue);

	@Delete("""

			DELETE FROM "tx_data"

			""")
	void deleteAll();

	@Select("""

			SELECT hash,contract,"from","to",amount,"timestamp" FROM "tx_data" ORDER BY "timestamp" asc LIMIT 10000

			""")
	List<TxData> getFirst100TxQueue();

	@Delete("""
			<script>
				DELETE FROM "tx_data"
				<where>
					<foreach collection="hashs" item="item" open="and hash in(" separator="," close=")">
						#{item}
					</foreach>
				</where>
			</script>
			""")
	void deleteByHashs(List<String> hashs);

	@Delete("""
				DELETE FROM "tx_data" WHERE hash = #{hash}
			""")
	void delete(String hash);

	@Select("""

			SELECT count(*) FROM "tx_data"

			""")
	int countAll();
}
