/**  
 * @Title:  PropertiesConfig.java   
 * @Package com.jsonsoft   
 * @Description: EAC价格转换配置  
 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
 * @date:   2021年2月27日 下午11:06:51   
 * @version V1.0 
 * @Copyright: 2021 X先生.
 */
package com.jsonsoft;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**   
 * @ClassName:  PropertiesConfig   
 * @Description:读取配置文件信息
 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a>  
 * @date:   2021年2月27日 下午11:06:51   
 *     
 * @Copyright: 2021 X先生.  
 */
@Component
@ConfigurationProperties(prefix = "rate")
public class PropertiesConfig {
	//汇率接口获取密钥
	private String currencylayerToken;
	
	//系统转换方式
	private String convertType;

	public String getCurrencylayerToken() {
		return currencylayerToken;
	}

	public void setCurrencylayerToken(String currencylayerToken) {
		this.currencylayerToken = currencylayerToken;
	}

	public String getConvertType() {
		return convertType;
	}

	public void setConvertType(String convertType) {
		this.convertType = convertType;
	}
}
