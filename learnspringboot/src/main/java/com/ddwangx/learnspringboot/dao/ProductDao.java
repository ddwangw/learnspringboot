package com.ddwangx.learnspringboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ddwangx.learnspringboot.pojo.Product;

@Mapper
public interface ProductDao {
	//获取产品
	public Product getProduct(Long id);
	//减库存
	public int decreaseProduct(@Param("id")Long id,
			@Param("quantity") int quantity) ;
}
