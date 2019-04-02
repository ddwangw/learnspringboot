package com.ddwangx.learnspringboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.ddwangx.learnspringboot.common.Result;
import com.ddwangx.learnspringboot.service.PurchaseService;

//REST风格控制器
@RestController
public class PurchaseController {
	@Autowired
	PurchaseService purchaseService = null;
	
	//定义JSP视图
	@GetMapping("test")
	public ModelAndView testPage() {
		ModelAndView mv = new ModelAndView("test");
		return mv;
	}
	@PostMapping("purchase")
	public Result purchase(Long userId, Long productId, int quantity) {
//		boolean success = purchaseService.purchase(userId, productId, quantity);
		boolean success = purchaseService.purcheasRedis(userId, productId, quantity);
		String message = success?"抢购成功":"抢购失败";
		Result result = new Result(success,message);
		return result;
	}
}
