package com.ddwangx.learnspringboot.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ddwangx.learnspringboot.pojo.PurchaseRecord;

@Mapper
public interface PurchaseRecordDao {
	//插入购买记录
	public int insertPurchaseRecord(PurchaseRecord pr);
}
