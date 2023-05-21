/**  
 * @Title:  RateStorage.java   
 * @Package com.jsonsoft.storage   
 * @Description: EAC价格转换存储  
 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
 * @date:   2021年2月27日 下午8:59:30   
 * @version V1.0 
 * @Copyright: 2021 X先生.
 */
package com.jsonsoft.storage;

import java.util.Map;

/**   
 * @ClassName:  RateStorage   
 * @Description: 汇率存储
 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a>  
 * @date:   2021年2月27日 下午8:59:30   
 *     
 * @Copyright: 2021 X先生.  
 */
public class RateStorage {
	//返回汇率JSON
	private Map<String,Object> rateJSON;
	//过期时间
	private long expiresTime;
	
	/**
	 * @Title: getRateJSON   
	 * @Description: 获取汇率JSON   
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
	 * @return      
	 * String
	 */
	public Map<String,Object> getRate() {
		return this.rateJSON;
	}
	
	/**
	 * @Title: rateJSONIsExpired   
	 * @Description: 是否过期   
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
	 * @return      
	 * boolean
	 */
	public boolean rateIsExpired() {
		return System.currentTimeMillis() > this.expiresTime;
	}
	
	/**
	 * @Title: updateRateJSON   
	 * @Description: 更新汇率JSON 每四小时一次
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
	 * @param rateJSON      
	 * void
	 */
	public void updateRate(Map<String,Object> rate) {
		this.rateJSON = rate;
		this.expiresTime = System.currentTimeMillis()+(( 60 * 60 * 4) * 1000L);
	}

	public Map<String, Object> getRateJSON() {
		return rateJSON;
	}

	public void setRateJSON(Map<String, Object> rateJSON) {
		this.rateJSON = rateJSON;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}
}
