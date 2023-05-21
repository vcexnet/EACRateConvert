/**  
 * @Title:  SpringBootStartApplication.java   
 * @Package com.jsonsoft   
 * @Description: 从交易所获取EAC的USDT价格，然后转换为各种货币对应的价格   
 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a> 
 * @date:   2021年2月28日 下午2:06:31   
 * @version V1.0 
 * @Copyright: 2021 X先生.
 */
package com.jsonsoft;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**   
 * @ClassName:  SpringBootStartApplication   
 * @Description:打包war所需
 *@author: <a href="mailto:tonghuafuwu@qq.com">X先生</a>  
 * @date:   2021年2月28日 下午2:06:31   
 *     
 * @Copyright: 2021 吴元森.  
 */
public class SpringBootStartApplication extends SpringBootServletInitializer{

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ExchangeRateConvertApplication.class);
    }
	
}
