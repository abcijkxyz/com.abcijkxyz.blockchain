package com.abcijkxyz.blockchain.data;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
//  INSERT INTO "public"."account"("address", "locked", "ecosystem", "memberName", "imageId", "memberInfo", "createDate") VALUES ('1', 't', 9223372036854775807, '1', 1, '1', '2022-04-06');

	private String address;
	private Boolean locked;
	private Long ecosystem;
	private String memberName;
	private Long imageId;
	private String memberInfo;
	private Date createDate;
	
	public Account(String address) {
		this.address = address;
	}

}
