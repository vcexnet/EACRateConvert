package com.jsonsoft.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jsonsoft.PropertiesConfig;
import com.jsonsoft.storage.CurrencyDescStorage;
import com.jsonsoft.storage.RateStorage;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.annotation.Resource;

@Controller
@RequestMapping("/api/v1")
public class IndexCtrl {

	@Resource
	private PropertiesConfig config;
	
	private RateStorage rateStorage = new RateStorage();

	CurrencyDescStorage currencyDesc = new CurrencyDescStorage();
	
	@GetMapping("rates")
	@ResponseBody
	public synchronized Object convert() {
		List<Map<String,Object>> resultArray = new ArrayList<Map<String,Object>>();
		Map<String,Object> result = new HashMap<String, Object>();
		//1 获取指定交易所价格 
//		Map<String,Object> exchangeParams = new HashMap<String,Object>();
//		exchangeParams.put("mk_type", StringUtils.isEmpty(mk_type)?"cnc":mk_type);
//		exchangeParams.put("mk_type", StringUtils.isEmpty(mk_type)?"USDT":mk_type);
//		exchangeParams.put("coinname", StringUtils.isEmpty(coinname)?"eac":coinname);
		//从bitxonex交易所获取eac的USDT价格
		String exchangeResult = HttpUtil.get("https://www.bitxonex.com/api/v2/trade/coinmarketcap/trades/eac_usdt");
		//从xeggex交易所获取eac的USDT价格
//      String exchangeResult = HttpUtil.get("https://xeggex.com/api/v2/ticker/EAC_USDT");
		System.out.println(exchangeResult);

//		JSONArray exchange = JSONUtil.parseArray(exchangeResult);
//		System.out.println(exchangeResult.split(",{")[0]);
//		JSONObject exchange = JSONUtil.parseObj(exchangeResult);
//				System.out.println(exchange);

		if(exchangeResult!=null) {
			//2获取货币转换 默认美元
			if(rateStorage.getRate() == null) {
				getCurrency();
			}else if(rateStorage.rateIsExpired()){ //过期了
				getCurrency();
			}
//			//获取货币说明
			if(currencyDesc.getCurrencyDesc() == null) {
				getCurrencyDesc();
			}else if(currencyDesc.isExpired()){
				getCurrencyDesc();
			}
			//开始输出结果
			//{code:"AED",n:647.21933,name:"United Arab Emirates Dirham",price:"AED647.21933"}
			//拿到基础值
//			Double buy = exchange.getJSONObject("data").getJSONObject("ticker").getDouble("buy");
			Double buy = new Double(exchangeResult.split("\"price\":")[1].split(",\"base_volume\"")[0]);
//          Double buy = new Double(exchangeResult.split("\"last_price\":")[1].split(",\"base_volume\"")[0]);xeggex交易所使用
			System.out.println("AAA:"+buy);
//          System.out.println("AAA:"+bid); xeggex交易所使用
			System.out.println(rateStorage.getRate());
			//货币兑换 - 人民币转换美元
			double CNYTOUSD =  0.00;
			if(config.getConvertType().equals("CNYTOUSD")) {
				CNYTOUSD = 1.0/Double.valueOf(rateStorage.getRate().get("USDCNY").toString()) * buy;
			}else if(config.getConvertType().equals("USDTOCNY")){//美元转换人名币
				CNYTOUSD = buy / Double.valueOf(rateStorage.getRate().get("USDCNY").toString());
			}else if(config.getConvertType().equals("USDT")){
				CNYTOUSD = buy;
			}else {
				//接口错误
//				result.put("eno", exchange.getInt("eno"));
				result.put("emsg", "取值失败");
				return result;
			}
			System.out.println(JSONUtil.toJsonStr( rateStorage.getRate().entrySet()));
			for(Map.Entry<String, Object> rate : rateStorage.getRate().entrySet()) {
				String key = rate.getKey();
				if(!key.equals("USDUSD")) {
					key = key.replace("USD", "");
				}else {
					key = "USD";
				}
				//缺少人民币换算美元
				BigDecimal overValue = BigDecimal.valueOf(Double.valueOf(rate.getValue().toString())).multiply(BigDecimal.valueOf(CNYTOUSD));
				Map<String,Object> temp = new HashMap<String,Object>();
				temp.put("code", key.isEmpty()?"USD":key);
				temp.put("n", overValue);
				if(currencyDesc.getCurrencyDesc() == null) {
					temp.put("name", "currencydesc is empty");
				}else {
					temp.put("name", currencyDesc.getCurrencyDesc().get(key));
				}
				temp.put("price", key + overValue);
				resultArray.add(temp);
			}
			return resultArray;
		}else {
			//接口错误
//			result.put("eno", exchange.getInt("eno"));
			result.put("emsg", "取值失败");
			return result;
		}
//		return null;
	}
	
	/**
	 * @Title: getCurrency   
	 * @Description: 汇率 
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a>       
	 * void
	 */
	public void getCurrency() {
		Map<String,Object> currencyParams = new HashMap<String, Object>();
		currencyParams.put("access_key", config.getCurrencylayerToken());
		String currencyResult = HttpUtil.get("http://api.currencylayer.com/live", currencyParams);
		JSONObject currencyObj = JSONUtil.parseObj(currencyResult);
		if(currencyObj.getBool("success")) {
			@SuppressWarnings("unchecked")
			Map<String,Object> currencyMap = currencyObj.getJSONObject("quotes").toBean(Map.class, false);
			rateStorage.updateRate(currencyMap);
		}
	}
	
	/**
	 * @Title: getCurrencyDesc   
	 * @Description: 货币说明
	 * @author: <a href="mailto:tonghuafuwu@qq.com">X先生</a>      
	 * void
	 */
	public void getCurrencyDesc() {
		Map<String,Object> currencyParams = new HashMap<String, Object>();
		currencyParams.put("access_key", config.getCurrencylayerToken());
		String currencyResult = HttpUtil.get("http://api.currencylayer.com/list", currencyParams);	
		JSONObject currencyObj = JSONUtil.parseObj(currencyResult);
		if(currencyObj.getBool("success")) {
			@SuppressWarnings("unchecked")
			Map<String,String> currencyMap = currencyObj.getJSONObject("currencies").toBean(Map.class, false);
			currencyDesc.updateCurrencyDesc(currencyMap);
		}
	}
	
}