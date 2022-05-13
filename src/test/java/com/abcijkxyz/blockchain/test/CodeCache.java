package com.abcijkxyz.blockchain.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CodeCache {

//	public static Map<String, TownNameDto> codeMap = new HashMap<String, TownNameDto>();
//
//	public static Map<String, CompanyModel> companyMap = new HashMap<String, CompanyModel>();
//
//	@Autowired
//	private CityDao cityDao;
//
//	@Autowired
//	private CompanyDao companyDao;
//
//	@PostConstruct
//	public void init() {
//		// 系统启动中。。。加载codeMap
//		List<TownNameDto> codeList = cityDao.selectCityNameAndCodeALL();
//		for (TownNameDto code : codeList) {
//			codeMap.put(code.getTownCode() + code.getValue(), code);
//		}
//		List<CompanyModel> companyModels = companyDao.selectCompanies();
//
//		for (CompanyModel company : companyModels) {
//			companyMap.put(company.getCode(), company);
//		}
//	}
//
//	@PreDestroy
//	public void destroy() {
//		// 系统运行结束
//
//	}
//
//	@Scheduled(cron = "0 0 0/2 * * ?")
//	public void testOne() {
//		// 每2小时执行一次缓存
//		init();
//	}
//
//	public static void main(String[] args) {
//
//		// 使用:
//		// 从缓存中获取城市;
//		Map<String, TownNameDto> cityModelMap = CodeCache.codeMap;
//		Map<String, CompanyModel> companyModelMap = CodeCache.companyMap;
//
//	}
}