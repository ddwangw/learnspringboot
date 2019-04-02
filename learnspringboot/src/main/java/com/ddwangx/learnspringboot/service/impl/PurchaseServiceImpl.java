package com.ddwangx.learnspringboot.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ddwangx.learnspringboot.dao.ProductDao;
import com.ddwangx.learnspringboot.dao.PurchaseRecordDao;
import com.ddwangx.learnspringboot.pojo.Product;
import com.ddwangx.learnspringboot.pojo.PurchaseRecord;
import com.ddwangx.learnspringboot.service.PurchaseService;

import redis.clients.jedis.Jedis;
@Service
public class PurchaseServiceImpl implements PurchaseService{
	//使用Redis Lua响应请求
	@Autowired
	StringRedisTemplate stringRedisTemplate = null;
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
	//	先将商品编号保存到集合中，抢购前，将数据缓存进redis
	//	hmset product 1 id 1 stock 30000 price 5 . 00
	String purchaseScript = " redis.call('sadd', KEYS[1], ARGV[2]) \n"
			//购买列表
		  + "local productPurchaseList = KEYS[2] \n"
		  	//用户编号
		  + "local userId = ARGV[1] \n"
		  	//产品键
		  + "local product = 'product_'..ARGV[2] \n"
		  	//购买数量
		  + "local quantity = tonumber(ARGV[3]) \n"
		  	//当前库存
		  + "local stock = tonumber(redis.call('hget', product, 'stock')) \n"
		  	//价格
		  + "local price = tonumber(redis.call('hget', product, 'price')) \n"
		  	//购买时间
		  + "local purchase_date = ARGV[4] \n"
		  	//库存不足返回0
		  + "if stock < quantity then return 0 end \n"
		  	//减库存
		  + "stock = stock -quantity \n"
		  + "redis.call('hset', product, 'stock', tostring(stock)) \n"
			//计算价格
		  + "local sum = price * quantity \n"
		 	//合并购买记录
		  + "local purchaseRecord = userId..','..quantity..','"
		  + "..sum..','..price..','..purchase_date \n"
			//将购买记录保存到list里
		  + "redis.call('rpush', productPurchaseList, purchaseRecord) \n"
		  	//返回成功
		  + "return 1 \n";
	//Redis购买记录集合前缀
	private static final String PURCHASE_PRODUCT_LIST = "purchase_list";
	//抢购商品集合
	private static final String PRODUCT_SCHEDULE_SET = "product_schedule_set";
	//32位SHA1编码，第一次执行的时候先让Redis进行缓存脚本返回
	private String sha1 = null;
	@Override
	public boolean purcheasRedis(Long userId, Long productId, int quantity) {
		//购买时间
		Long purchaseDate = System.currentTimeMillis();
		Jedis jedis = null;
		try {
			//获取原始链接
			jedis = (Jedis) stringRedisTemplate
					.getConnectionFactory()
					.getConnection()
					.getNativeConnection();
			if(sha1 == null) {
				//如果没有加载，则先将脚本加载到Redis服务器，让其返回sha1
				sha1 = jedis.scriptLoad(purchaseScript);
			}
			//执行脚本，返回结果
			Object res = jedis.evalsha(sha1, 2, PRODUCT_SCHEDULE_SET , 
					PURCHASE_PRODUCT_LIST , userId +"", productId+"" ,
					quantity+"", purchaseDate+"");
			Long result = (Long) res;
			return result == 1;
		}finally {
			//关闭jedis链接
			if(jedis != null && jedis.isConnected()) {
				jedis.close();
			}
		}
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean dealRedisPurchase(List<PurchaseRecord> prs) {
		//TODO 下面是几个事务，如果扣减出现异常，插入是否回滚？
		for(PurchaseRecord pr : prs) {
			purchaseRecordDao.insertPurchaseRecord(pr);
			productDao.decreaseProduct(pr.getProductId(), pr.getQuantity());
		}
		return true;
	}
}
