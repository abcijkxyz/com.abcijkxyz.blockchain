package com.abcijkxyz.blockchain.mapper;

import java.util.Collection;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.abcijkxyz.blockchain.data.Account;

@Mapper
public interface AccountMapper {
//  INSERT INTO "public"."account"("address", "locked", "ecosystem", "memberName", "imageId", "memberInfo", "createDate") VALUES ('1', 't', 9223372036854775807, '1', 1, '1', '2022-04-06');

	@Insert("""

			INSERT INTO "account" ("address", "locked", "ecosystem", "memberName", "imageId", "memberInfo", "createDate") 
			VALUES(#{address},#{locked},#{ecosystem},#{memberName},#{imageId},#{memberInfo},#{createDate})

			""")
	void insertAccount(Account account);

	@Insert("""

			INSERT INTO "account" (address) VALUES(#{address})

			""")
	void insertOnlyAddress(String address);

	@Delete("""

			DELETE FROM account

			""")
	void deleteAll();

	@Select("""

			SELECT "address", "locked", "ecosystem", "memberName", "imageId", "memberInfo", "createDate" FROM "account"

			""")
	Collection<Account> findAll();

	@Select("""

			SELECT count(*) FROM account

			""")
	int countAll();

	@Select("""

			SELECT "address", "locked", "ecosystem", "memberName", "imageId", "memberInfo", "createDate" FROM "account" WHERE address = #{address}

			""")
	Account getByAccount(String address);
}
