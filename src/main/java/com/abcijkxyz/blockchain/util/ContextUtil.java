package com.abcijkxyz.blockchain.util;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.abcijkxyz.blockchain.data.SpentInfo;
import com.alibaba.fastjson.JSONObject;

public class ContextUtil {
	public static ThreadLocal<Context> context = new ThreadLocal<Context>();

	public static ConcurrentMap<String, Vector<SpentInfo>> outputsMap = new ConcurrentHashMap<String, Vector<SpentInfo>>();

	public static Context getContext() {
		return context.get();
	}

	public static void setContext(Context context) {
		ContextUtil.context.set(context);
	}

	public static Vector<SpentInfo> getOutputs(String address) {
		Vector<SpentInfo> list = outputsMap.get(address);
		Vector<SpentInfo> list2 = new Vector<SpentInfo>();
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

	public static Vector<SpentInfo> getAllOutputs(String address) {
		Vector<SpentInfo> list = outputsMap.get(address);
		 
		return list;
	}
	public static void putOutputs(Vector<SpentInfo> outputs) {
		if (outputs != null) {
			for (SpentInfo txOutput : outputs) {
				Vector<SpentInfo> spentInfos = outputsMap.get(txOutput.getOutputAddress());
				if (spentInfos == null) {
					spentInfos = new Vector<SpentInfo>();
				}
				spentInfos.add(txOutput);
				putOutputs(txOutput.getOutputAddress(), spentInfos);
			}
		}
	}

	public static void putOutputs(String address, Vector<SpentInfo> outputs) {
		outputsMap.put(address, outputs);
	}

	public static void removeAllOutputs() {
		outputsMap.keySet().forEach(outputsMap::remove);
	}

	public static Vector<SpentInfo> getAllOutputs() {
		Vector<SpentInfo> spentInfos = new Vector<SpentInfo>();
		outputsMap.keySet().forEach(k -> {
			spentInfos.addAll(outputsMap.get(k));
		});

		return spentInfos;
	}

}
