package com.ddwangx.learnspringboot;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ddwangx.learnspringboot.service.PurchaseService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnspringbootApplicationTests {
//	@Autowired
//	PurchaseService purchaseService = null;
	@Test
	public void contextLoads() {
//		Assert.assertTrue(purchaseService.purchase(Long.parseLong("1"), Long.parseLong("1"), 1)); 
	}

}
