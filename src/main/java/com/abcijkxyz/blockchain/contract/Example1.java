package com.abcijkxyz.blockchain.contract;

import org.apache.commons.lang3.StringUtils;

import com.abcijkxyz.blockchain.smart.SmartContract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Example1 implements SmartContract {

	public String comment;

	public Boolean conditions() {

		if (StringUtils.length(this.comment) == 0) {

			return false;
		}

		return true;
	}

	public String action() {
//		System.out.println("Example1 action param comment:" + this.comment);

		if (StringUtils.length(this.comment) == 0) {
			return "Empty comment";
		}

		return "Hello " + comment;

	}
}