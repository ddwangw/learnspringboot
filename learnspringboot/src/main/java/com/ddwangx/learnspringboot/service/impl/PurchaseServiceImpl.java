package com.ddwangx.learnspringboot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddwangx.learnspringboot.dao.ProductDao;
import com.ddwangx.learnspringboot.dao.PurchaseRecordDao;
import com.ddwangx.learnspringboot.pojo.Product;
import com.ddwangx.learnspringboot.pojo.PurchaseRecord;
import com.ddwangx.learnspringboot.service.PurchaseService;
@Service
public class PurchaseServiceImpl implements PurchaseService{
	@Autowired
	private ProductDao productDao = null;
	@Autowired
	private PurchaseRecordDao purchaseRecordDao = null;
	@Override
	@Transactional
	public boolean purchase(Long userId, Long productId, int quantity) {
		//获取产品
		Product product = productDao.getProduct(productId);
		//比较库存和购买数量
		if(product.getStock()<quantity) {
			//库存不足
			return false;
		}
		//扣减库存
		productDao.decreaseProduct(productId, quantity);
		//添加购买记录
		PurchaseRecord pr = this.initPurchaseRecord(userId, product, quantity);
		//插入购买记录
		purchaseRecordDao.insertPurchaseRecord(pr);
		return true;
	}
	//初始化购买记录
	private PurchaseRecord initPurchaseRecord(Long userId, Product product, int quantity) {
		PurchaseRecord pr = new PurchaseRecord();
		pr.setNote("购买日志，时间："+System.currentTimeMillis());
		pr.setPrice(product.getPrice());
		pr.setProductId(product.getId());
		pr.setQuantity(quantity);
		double sum = product.getPrice()*quantity;
		pr.setSum(sum);
		pr.setUserId(userId);
		return pr;
	}
}
