package com.ddwangx.learnspringboot.task;

public interface TaskService {
	/**
	 * 购买定时任务
	 * 将redis数据刷新到数据库中
	 */
	public void purchaseTask();
}
