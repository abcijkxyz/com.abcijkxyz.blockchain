package com.abcijkxyz.blockchain.smart;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SmartContractHandler implements InvocationHandler {

	private SmartContract contract = null;

	private Before before = null;

	private After after = null;

	/**
	 * InvocationHandler 接口的实现方法，进行动态代理
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 看看接口中方法是否标注了需要 @Contract
		boolean b = method.isAnnotationPresent(Contract.class);
		if (!b) {
			// 没有标注的话，按原方法执行
			return method.invoke(contract, args);
		}
		// 有标注的话，进行方法的前置和后置增强
		if (before != null) {
			before.before();
		}

		Object obj = method.invoke(contract, args);

		if (after != null) {
			after.after();
		}
		return obj;
	}

	/**
	 * 将传入的 SmartContract 与 InvocationHandler 进行绑定，以获得代理类的实例
	 *
	 * @param  contract
	 * @return
	 */
	public SmartContract bind(SmartContract contract) {
		this.contract = contract;
		SmartContract smartContract = (SmartContract) Proxy.newProxyInstance(contract.getClass().getClassLoader(), contract.getClass().getInterfaces(), this);
		return smartContract;
	}

	public void setAfter(After after) {
		this.after = after;
	}

	public void setBefore(Before before) {
		this.before = before;
	}
}