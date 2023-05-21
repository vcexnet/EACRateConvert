/**  
 * @Title:  SyncQueryDataTest.java   
 * @Package exchangeRateConvert   
 * @Description: (用一句话描述该文件做什么)   
 * @author: <a href="mailto:15909910367@163.com">吴先生</a> 
 * @date:   2021年7月6日 下午3:59:05   
 * @version V1.0 
 * @Copyright: 2021 吴先生.
 */
package exchangeRateConvert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.hutool.json.JSONUtil;

/**   
 * @ClassName:  SyncQueryDataTest   
 * @Description:本例实现后端同步执行代码使用
 * @author: <a href="mailto:15909910367@163.com">吴先生</a>  
 * @date:   2021年7月6日 下午3:59:05   
 *     
 * @Copyright: 2021 吴元森.  
 */
public class SyncQueryDataTest {
	
	/**
	 * @Title: main   
	 * @Description: 同步执行代码
	 * @author: <a href="mailto:15909910367@163.com">吴先生</a>
	 * @param args      
	 * void
	 */
	public static void main(String[] args) {
		//例如我先构建出来一千个人 假设这里是你的查询 这里的List<Map> 根据实际情况修改成List<实体类>
		List<Map<String,Object>> allUser = new ArrayList<Map<String,Object>>();
		for(int i=0;i<7000;i++) {
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("name", "jason_"+i);
			allUser.add(tempMap);
		}
		//并发执行同步执行
		int queueCount = 1000;//比如这里设置成100人一个批次
		int batchCount = allUser.size()/queueCount;//批次数，可以理解为将数据打包成多少份
		CountDownLatch latch = new CountDownLatch(batchCount);//将总数和批次数进行分配
		ExecutorService threadPool = Executors.newFixedThreadPool(batchCount);//构造线程池
		List<Future<List<Map<String,Object>>>> futureTaskList = new ArrayList<Future<List<Map<String,Object>>>>(batchCount);//Future机制解决耗时问题
		long batchBegin = System.currentTimeMillis();
		List<List<Map<String,Object>>> cuttingData = averageAssign(allUser, batchCount);
		for(int k=0;k<cuttingData.size();k++) { //构建批次执行数量
			final List<Map<String,Object>> temp = cuttingData.get(k);
			futureTaskList.add(threadPool.submit(new Callable<List<Map<String,Object>>>() {
				@Override
				public List<Map<String,Object>> call() throws Exception {
					latch.countDown(); 
					for(int l =0;l<temp.size(); l++) {//模拟子查询
						temp.get(l).put("count", 1);//重新赋值可以理解为查询这个人的考勤天数
					}
					return temp;
				}
			}));
		}
		List<Map<String,Object>> resultArray = new ArrayList<Map<String,Object>>();
		try {
			latch.await();//调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}finally{
            threadPool.shutdown();
        }
		long batchEnd = System.currentTimeMillis();
		//取得查询结果
		for(int k = 0;k<futureTaskList.size();k++) {
			try {			
				Future<List<Map<String,Object>>> future = futureTaskList.get(k);
				resultArray.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		System.out.println("耗时：" + (batchEnd - batchBegin) + "\r\n"/* +JSONUtil.toJsonStr(resultArray) */);
		//传统执行 for循环1000次
		long begin = System.currentTimeMillis();
		for(Map<String,Object> temp : allUser) {
			//子查询，查询考情
			temp.put("count", 2);
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end - begin) + "\r\n"/* +JSONUtil.toJsonStr(resultArray) */);
	}
	
	/**
	 * @Title: averageAssign   
	 * @Description: 将List分块
	 * @author: <a href="mailto:15909910367@163.com">吴先生</a>
	 * @param <T>
	 * @param source
	 * @param n
	 * @return      
	 * List<List<T>>
	 */
	public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;//(先计算出余数)
        int number = source.size() / n;//获得商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }
}
