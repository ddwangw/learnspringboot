package com.ddwangx.learnspringboot.service;

import java.util.List;

import com.ddwangx.learnspringboot.pojo.PurchaseRecord;

public interface PurchaseService {
	/**
	 * 处理购买业务
	 * @param userId 用户编号
	 * @param productId 产品编号
	 * @param quantity 购买数量
	 * @return 成功或者失败
	 */
	public boolean purchase(Long userId,Long productId,int quantity);
	/**
	 * 处理购买业务,通过Redis提高效率解决高并发商品超发问题
	 * @param userId 用户编号
	 * @param productId 产品编号
	 * @param quantity 购买数量
	 * @return 成功或者失败
	 */
	public boolean purcheasRedis(Long userId,Long productId,int quantity);
	/**
	 * 保存购买记录
	 * @param pcr
	 * @return
	 */
	public boolean dealRedisPurchase(List<PurchaseRecord> pr);
}
