package com.abcijkxyz.blockchain.smart;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.abcijkxyz.blockchain.client.TxData;
import com.abcijkxyz.blockchain.data.SpentInfo;
import com.abcijkxyz.blockchain.mapper.SpentInfoMapper;
import com.abcijkxyz.blockchain.util.Context;
import com.abcijkxyz.blockchain.util.ContextUtil;

@Component
public class SmartVM {

	@Autowired
	private SpentInfoMapper spentInfoMapper;

	// 设置虚拟机上下文参数
	public String callContract(TxData txData, SmartContract contract) {
		Before before = new Before() {
			public void before() {

				// Boolean bool = contract.conditions();
				// if (bool != null && bool) {
				// System.out.println("SmartContract...before...参数验证 conditions OK");
				// } else {
				// System.out.println("SmartContract...before...参数验证 conditions ERROR");
				// }
//				 Assert.isTrue(bool, contract.getClass().getSimpleName() + " 参数验证方法 conditions() 返回值必须为true");

				// System.out.println("SmartContract...before...准备输入 TxInput");

				String from = txData.getFrom();
				List<SpentInfo> txInputs = spentInfoMapper.findTxOutput(from);

				// String pretty = JSON.toJSONString(txInputs, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);

				// System.out.println("TxInput:\n" + pretty);
				// TODO 运行失败惩罚机制
				Assert.isTrue(txInputs != null && txInputs.size() > 0, "txInputs 不为空: " + from + "\t" + txInputs);

				if (txInputs != null && txInputs.size() > 0) {
					Context context = new Context();
					context.setFrom(from);
					context.setTxInputs(txInputs);
					context.setTxData(txData);

					// System.out.println("SmartContract...before...设置虚拟机上下文参数");
					ContextUtil.setContext(context);
				}

			}
		};
		After after = new After() {
			public void after() {
				// System.out.println("SmartContract...after...处理输出 TxOutput");

				Context context = ContextUtil.getContext();
				// TODO 运行失败惩罚机制
				if (context != null) {
					List<SpentInfo> txOutputs_ctx = context.getTxOutputs();
					List<SpentInfo> txInputs_ctx = context.getTxInputs();
					String from = context.getFrom();
					List<SpentInfo> txOutputs = new ArrayList<SpentInfo>();
					Long totalAmount = 0L;

					// 余额是否充足标识
					Boolean flag = true;

					// 手续费
					Long gasOutputValue = 1L;
					// 收手续费账号
					String gasOutputAddress = "9999-9999-9999-9999-9999";
//					String gasOutputAddress = "1111-1111-1111-1111-0000";

					// 有输出时候 在from的输出上减去手续费
					Integer outputIndex = 0;
					if (txOutputs_ctx != null && txOutputs_ctx.size() > 0) {

						for (SpentInfo spentInfo : txOutputs_ctx) {
							if (spentInfo.getOutputAddress().equals(from)) {
								totalAmount = totalAmount + spentInfo.getOutputValue();
							} else {
								spentInfo.setOutputIndex(outputIndex++);
								txOutputs.add(spentInfo);
							}
						}

					} else {
						if (txInputs_ctx != null && txInputs_ctx.size() > 0) {
							for (SpentInfo spentInfo : txInputs_ctx) {
								totalAmount = totalAmount + spentInfo.getOutputValue();
							}
						}
					}

					// 减去手续费，找零

					if (totalAmount >= gasOutputValue) {
						SpentInfo output = new SpentInfo();
						output.setOutputAddress(gasOutputAddress);
						output.setOutputValue(gasOutputValue);
						output.setOutputIndex(outputIndex++);
						totalAmount = totalAmount - gasOutputValue;
						txOutputs.add(output);
					} else {
						flag = false;
					}
					// TODO 运行失败惩罚机制
					Assert.isTrue(flag, "扣减手续费余额不足:" + from);

					// 剩余余额的找零输出
					if (totalAmount > 0) {
						SpentInfo output = new SpentInfo();
						output.setOutputAddress(from);
						output.setOutputValue(totalAmount);
						output.setOutputIndex(outputIndex++);
						txOutputs.add(output);

					}

					// String pretty = JSON.toJSONString(txOutputs, SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat);

					// System.out.println("TxOutput:\n" + pretty);
					if (flag) {
						context.setTxOutputs(txOutputs);
					}
					// ContextUtil.setContext(null);
					// Long height=null;

				}
			}
		};

		return callContract(before, contract, after);
	}

	// 设置虚拟机上下文参数
	private static String callContract(Before before, SmartContract contract, After after) {

		SmartContractHandler handler = new SmartContractHandler();
		if (before != null) {
			handler.setBefore(before);
		}
		if (after != null) {
			handler.setAfter(after);
		}
		return handler.bind(contract).action();
	}

}

interface After {
	public void after();
}

interface Before {
	public void before();
}

// interface TryCatch {
// public void tryCatch();
// }
