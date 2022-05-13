package com.abcijkxyz.blockchain.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.abcijkxyz.blockchain.data.SpentInfo;
import com.alibaba.fastjson.JSONObject;

public class ContextUtil {
	public static ThreadLocal<Context> context = new ThreadLocal<Context>();

	public static ConcurrentMap<String, List<SpentInfo>> outputsMap = new ConcurrentHashMap<String, List<SpentInfo>>();

	public static Context getContext() {
		return context.get();
	}

	public static void setContext(Context context) {
		ContextUtil.context.set(context);
	}

	public static List<SpentInfo> getOutputs(String address) {
		List<SpentInfo> list = outputsMap.get(address);
		List<SpentInfo> list2 = new ArrayList<SpentInfo>();
		if (list != null) {
			int inputIndex = 0;
			for (int i = 0; i < list.size(); i++) {
				SpentInfo info = list.get(i);
				if (info.getInputTxHash() == null) {
					info.setInputIndex(inputIndex++);
					list2.add(info);
				}
			}
		}
		return list2;
	}

	public static List<SpentInfo> getAllOutputs(String address) {
		List<SpentInfo> list = outputsMap.get(address);
		 
		return list;
	}
	public static void putOutputs(List<SpentInfo> outputs) {
		if (outputs != null) {
			for (SpentInfo txOutput : outputs) {
				List<SpentInfo> spentInfos = outputsMap.get(txOutput.getOutputAddress());
				if (spentInfos == null) {
					spentInfos = new ArrayList<SpentInfo>();
				}
				spentInfos.add(txOutput);
				putOutputs(txOutput.getOutputAddress(), spentInfos);
			}
		}
	}

	public static void putOutputs(String address, List<SpentInfo> outputs) {
		outputsMap.put(address, outputs);
	}

	public static void removeAllOutputs() {
		outputsMap.keySet().forEach(outputsMap::remove);
	}

	public static List<SpentInfo> getAllOutputs() {
		List<SpentInfo> spentInfos = new ArrayList<SpentInfo>();
		outputsMap.keySet().forEach(k -> {
			spentInfos.addAll(outputsMap.get(k));
		});

		return spentInfos;
	}

}
