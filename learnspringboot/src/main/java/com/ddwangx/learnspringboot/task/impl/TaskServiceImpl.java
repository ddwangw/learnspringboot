package com.ddwangx.learnspringboot.task.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ddwangx.learnspringboot.pojo.PurchaseRecord;
import com.ddwangx.learnspringboot.service.PurchaseService;
import com.ddwangx.learnspringboot.task.TaskService;
@Service
public class TaskServiceImpl implements TaskService {
	@Autowired
	private StringRedisTemplate stringRedisTemplate = null;
	@Autowired
	private PurchaseService purchaseService = null;
	//Redis购买记录集合前缀
	private static final String PURCHASE_PRODUCT_LIST = "purchase_list";
	//抢购商品集合
	private static final String PRODUCT_SCHEDULE_SET = "product_schedule_set";
	//每次取出1000条，避免一次取出消耗太多内存
	private static final int ONE_TIME_SIZE = 1000;
	@Override
	@Scheduled(fixedRate = 1000*60)
	public void purchaseTask() {
		System.out.println("定时任务开始");
		Set<String> productIdList = 
				stringRedisTemplate.opsForSet().members(PRODUCT_SCHEDULE_SET);
		List<PurchaseRecord> prlist = new ArrayList<PurchaseRecord>();
		for(String productIdStr :productIdList) {
			Long productIdr = Long.parseLong(productIdStr);
			String purchaseKey = PURCHASE_PRODUCT_LIST;
			BoundListOperations<String,String> ops = 
					stringRedisTemplate.boundListOps(purchaseKey);
			//计算记录数
			long size = stringRedisTemplate.opsForList().size(purchaseKey);
			Long times = size % ONE_TIME_SIZE == 0 ? size /ONE_TIME_SIZE : size / ONE_TIME_SIZE +1;
			for(int i = 0 ;i < times ; i++) {
				//获取至多TIME_SIZE个抢红包信息
				List<String> prListStr = null;
				if(i == 0) {
					prListStr = ops.range(i*ONE_TIME_SIZE , (i+1)*ONE_TIME_SIZE);
				}else {
					prListStr= ops.range(i*ONE_TIME_SIZE + 1, (i+1)*ONE_TIME_SIZE);
				}
				for(String prStr : prListStr) {
					PurchaseRecord pr =  this.createPurchaseRecord(productIdr,prStr);
					prlist.add(pr);
				}
				try {
					purchaseService.dealRedisPurchase(prlist);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
				//清除列表为空，等待重新写入数据
				prlist.clear();
			}
			/*try {
				System.out.println("开始睡眠");
				Thread.sleep(3000*20);
				System.out.println("睡眠结束，着手删除记录");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//删除购买列表：如果查询出来正在进行处理，此时又有销售记录进来了，就会出现购买记录丢失
			stringRedisTemplate.delete(purchaseKey);
			//从商品集合中删除商品
			stringRedisTemplate.opsForSet().remove(PRODUCT_SCHEDULE_SET, productIdStr);
		}
		System.out.println("定时任务结束");
	}
	
	private PurchaseRecord createPurchaseRecord(Long productId,String prStr) {
		String[] arr = prStr.split(",");
		Long userId = Long.parseLong(arr[0]);
		int quantity = Integer.parseInt(arr[1]);
		double sum = Double.valueOf(arr[2]);
		double price = Double.valueOf(arr[3]);
		Long time = Long.parseLong(arr[4]);
		Timestamp purchaseTime = new Timestamp(time);
		PurchaseRecord pr = new PurchaseRecord();
		pr.setProductId(productId);
		pr.setPurchaseTime(purchaseTime);
		pr.setPrice(price);
		pr.setQuantity(quantity);
		pr.setSum(sum);
		pr.setUserId(userId);
		pr.setNote("购买日志，时间："+purchaseTime.getTime());
		return pr;
	}
}
