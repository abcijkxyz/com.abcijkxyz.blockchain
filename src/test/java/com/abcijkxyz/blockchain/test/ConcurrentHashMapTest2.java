package com.abcijkxyz.blockchain.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class StudentScore {
	private Integer orderNum;
	private String subjectName;
	private Integer score;

}

public class ConcurrentHashMapTest2 {
	public static Map<Integer, Integer> sum1(List<StudentScore> list) {

		Map<Integer, Integer> map = new HashMap<>();
		for (StudentScore studentScore : list) {
			if (map.containsKey(studentScore.getOrderNum())) {
				map.put(studentScore.getOrderNum(), map.get(studentScore.getOrderNum()) + studentScore.getScore());
			} else {
				map.put(studentScore.getOrderNum(), studentScore.getScore());
			}
		}
		return map;
	}

	public static Map<Integer, Integer> sum2(List<StudentScore> list) {
		Map<Integer, Integer> map = new HashMap<>();
		list.stream().forEach(studentScore -> map.merge(studentScore.getOrderNum(), studentScore.getScore(), Integer::sum));
		return map;
	}

	public static void main(String args[]) {
		List<StudentScore> list = new ArrayList<>();
		list.add(new StudentScore(1, "chinese", 110));
		list.add(new StudentScore(1, "english", 120));
		list.add(new StudentScore(1, "math", 135));

		list.add(new StudentScore(2, "chinese", 99));
		list.add(new StudentScore(2, "english", 100));
		list.add(new StudentScore(2, "math", 133));

		System.out.println(sum1(list));
		System.out.println(sum2(list));

	}
}
