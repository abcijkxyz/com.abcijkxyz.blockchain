package com.abcijkxyz.blockchain.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.abcijkxyz.blockchain.data.SpentInfo;

public class OutputLedger {
	public static Map<String, List<SpentInfo>> map = new ConcurrentHashMap<String, List<SpentInfo>>();
//	public static Map<String, List<SpentInfo>> map2=Collections.synchronizedMap(new HashMap<String, List<SpentInfo>>());
}
