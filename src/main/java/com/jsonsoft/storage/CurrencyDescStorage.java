/**  
 * @Title:  CurrencyDesc.java   
 * @Package com.jsonsoft.storage   
 * @Description: 货币说明存储   
 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
 * @date:   2021年2月27日 下午9:37:22   
 * @version V1.0 
 * @Copyright: 2021 X先生.
 */
package com.jsonsoft.storage;

import java.util.Map;

/**   
 * @ClassName:  CurrencyDesc   
 * @Description:货币说明本地存储
 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a>  
 * @date:   2021年2月27日 下午9:37:22   
 *     
 * @Copyright: 2021 X先生.  
 */
public class CurrencyDescStorage {
	//返回汇率JSON
	private Map<String,String> currencyDesc;
	//过期时间
	private long expiresTime;
	
	/**
	 * @Title: getRateJSON   
	 * @Description: 获取货币说明 
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
	 * @return      
	 * String
	 */
	public Map<String,String> getCurrencyDesc() {
		return this.currencyDesc;
	}
	
	/**
	 * @Title: rateJSONIsExpired   
	 * @Description: 是否过期   
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
	 * @return      
	 * boolean
	 */
	public boolean isExpired() {
		return System.currentTimeMillis() > this.expiresTime;
	}
	
	/**
	 * @Title: updateRateJSON   
	 * @Description: 更新货币说明 每四小时一次
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
	 * @param rateJSON      
	 * void
	 */
	public synchronized void updateCurrencyDesc(Map<String,String> desc) {
		this.currencyDesc = desc;
		this.expiresTime = System.currentTimeMillis()+(( 60 * 60 * 4) * 1000L);
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}

	public void setCurrencyDesc(Map<String, String> currencyDesc) {
		this.currencyDesc = currencyDesc;
	}
	
}
