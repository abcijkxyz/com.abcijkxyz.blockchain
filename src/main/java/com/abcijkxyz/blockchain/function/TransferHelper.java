package com.abcijkxyz.blockchain.function;

import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.util.Context;
import com.abcijkxyz.blockchain.util.ContextUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class TransferHelper {

	public static Boolean singleTransfer(String outputAddress, Long outputValue) {
		return singleTransfer(new TransferMsg(outputAddress, outputValue));
	}

	public static Boolean singleTransfer(TransferMsg transferMsg) {
		List<TransferMsg> transferMsgs = new ArrayList<TransferMsg>();
		transferMsgs.add(transferMsg);
		return multipleTransfer(transferMsgs);
	}

	// 系统函数 构造输出 到上下文
	public static Boolean multipleTransfer(List<TransferMsg> transferMsgs) {
		Context context = ContextUtil.getContext();
		List<SpentInfo> txInput = context.getTxInputs();
		String from = context.getFrom();

		System.out.println("multipleTransfer 获得虚拟机上下文参数");
		List<SpentInfo> txOutputs = new ArrayList<SpentInfo>();

		Long totalAmount = 0L;
		Boolean flag = true;
		for (SpentInfo spentInfo : txInput) {
			totalAmount = totalAmount + spentInfo.getOutputValue();
		}

//		Integer outputIndex = 0;
		for (TransferMsg transferMsg : transferMsgs) {
			// 组装转给指定账号的输出
			if (totalAmount >= transferMsg.getOutputValue()) {
				SpentInfo output = new SpentInfo();
				output.setOutputAddress(transferMsg.getOutputAddress());
				output.setOutputValue(transferMsg.getOutputValue());
//				output.setOutputIndex(outputIndex++);
				totalAmount = totalAmount - transferMsg.getOutputValue();
				txOutputs.add(output);
			} else {
				flag = false;

				break;
			}

		}
		Assert.isTrue(flag, "转账余额不足");

		// 剩余余额的找零输出
		if (totalAmount > 0) {
			SpentInfo output = new SpentInfo();
			output.setOutputAddress(from);
			output.setOutputValue(totalAmount);
//			output.setOutputIndex(outputIndex++);
			txOutputs.add(output);

		}

		if (flag) {
			context.setTxOutputs(txOutputs);
		}

		// cache output
		// return output
		return flag;
	}
}
