package com.whyse.myLiangHua.real;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.alibaba.fastjson.JSON;
import com.whyse.myLiangHua.util.FileUtils;

public class MDLHClientImpl {

	public double[] listSelfHalfMin = new double[1000];
	/**
	 * listSelfHalfMin 的size,下标所对应可以赋值
	 */
	public volatile int index = 0;
	//---------------------------------
	static final String qihuoMinLinePath = "G:/lianghua/qihuo/";
	volatile int count = 0;
	public volatile double[] list5 = new double[4];
	public volatile double[] list10 = new double[4];
	public volatile double[] list20 = new double[4];
	public volatile double[] list30 = new double[4];
	public volatile Queue<Double> queMin34 = new LinkedBlockingQueue<>(34);
	private int sizeP = 3;
	/**
	 * 用来保存最近不同的报价数据,不同的报价才能被列为参数.
	 * 调整这个容量参数，可以达到扩容样本的目的
	 */
	volatile Queue<Double> que5 = new LinkedBlockingQueue<>(sizeP);//5显然太大
	volatile Map<Double, Boolean>  mapQue = new HashMap<Double, Boolean>(10);
	public  LinkedBlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(10);// 应该是600，看看会不会溢出任务
	/**
	 * 获取到的最新报价
	 */
	volatile double LastPrice;
	volatile double LastPriceOlder=0;
	public volatile Map<String, Object> LastMdMap;
	TraderLHOptImportReal  traderLHOptImportReal;
	/**
	 * 量化多空，只做一个品种
	 */
	public String mainSym = "";//棕榈
	
//	public static String mainSym = "JM1701";//焦煤
//	public static String mainSym = "RB1701";//螺纹
	public MDLHClientImpl(String sym) {
		mainSym = sym;
		traderLHOptImportReal = new TraderLHOptImportReal(this);
		init();
	}

	//================================================================
	/**
	 * @param args
	 * author:xumin 
	 * 2016-11-15 下午5:01:09
	 */
	public static void main(String[] args) {
//		long beg = System.currentTimeMillis();
//		for(int i=1;i<66;i++)
//			lastPriceInQueue(que34, i*1.0);
//		Double[]  temp = new Double[34];
//		temp = que34.toArray(temp);
//		updateJS(temp);
//		System.err.println(System.currentTimeMillis()-beg);
	}

	public  void init() {
		//==========================================================================
		tryReadQueMin();
		count = queMin34.size();
		newConsumerEventJS("计算均线"+mainSym);
	}

	@SuppressWarnings("unchecked")
	private void tryReadQueMin() {
		try{
			String path = getPathSym();
			String str = FileUtils.readFileAll(path);
			List<Double> listT = new ArrayList<Double>(40);
			listT = JSON.parseObject(str, listT.getClass());//这个是读取用的
			for(int i=0;i<listT.size();i++){
				lastPriceInQueue(queMin34,listT.get(i));
			}
		}catch(Exception e){
			
		}
	}

	private String getPathSym() {
		return qihuoMinLinePath+mainSym+"_minLine";
	}

	//=====================================================================
	public void newConsumerEventJS(String name) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						int flag = blockingQueue.take();
						if(flag==1){
							//------这边开始计算均线是否OK-------------
							if(count>=34){
								Double[]  temp = new Double[34];
								temp = queMin34.toArray(temp);
								updateJS(temp);
								//------交易策略制定，等或者行动-------
								traderLHOptImportReal.doOrNot();
							}
						}
						if(flag==2){
							//---抽样点累加计算---------
							if(index>=88)
								traderLHOptImportReal.optLeve2();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread td = new Thread(run);
		td.setName(name);
		td.start();
	}
	//=================================================
	/**
	 * 因为乘法跟加法和一样，还是每次累加吧，计算速度基本一样
	 * 很快！！！,就这个就行
	 * author:xumin 
	 * 2016-11-17 下午4:09:45
	 * @param temp 
	 */
	protected void updateJS(Double[] temp) {
		for(int i=33,j=0;i>29;i--,j++)
			list5[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4])/5;
		
		for(int i=33,j=0;i>29;i--,j++)
			list10[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4]+temp[i-5]+temp[i-6]+temp[i-7]+temp[i-8]+temp[i-9])/10;
		
		for(int i=33,j=0;i>29;i--,j++)
			list20[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4]+temp[i-5]+temp[i-6]+temp[i-7]+temp[i-8]+temp[i-9]
					+temp[i-10]+temp[i-11]+temp[i-12]+temp[i-13]+temp[i-14]+temp[i-15]+temp[i-16]+temp[i-17]+temp[i-18]+temp[i-19])/20;
		
		for(int i=33,j=0;i>29;i--,j++)
			list30[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4]+temp[i-5]+temp[i-6]+temp[i-7]+temp[i-8]+temp[i-9]
					+temp[i-10]+temp[i-11]+temp[i-12]+temp[i-13]+temp[i-14]+temp[i-15]+temp[i-16]+temp[i-17]+temp[i-18]+temp[i-19]
					+temp[i-20]+temp[i-21]+temp[i-22]+temp[i-23]+temp[i-24]+temp[i-25]+temp[i-26]+temp[i-27]+temp[i-28]+temp[i-29])/30;
	}

	/**
	 * 只管存最新的5挡行情
	 * @param mapData
	 * author:xumin 
	 * 2016-11-16 下午4:20:55
	 */
	public void optData(Map<String, Object> mapData) {
		LastMdMap = mapData;
		LastPrice = (Double) LastMdMap.get("LastPrice");
		lastPriceInQueue(que5,LastPrice);//最新数据入队列
	}
	/**
	 * 每分钟被调用一次，记录过去一分钟的收盘价格
	 * author:xumin 
	 * 2016-11-22 下午7:59:59
	 */
	public void miniteWriteEvent() {
		Double pjPrice = getPJPrice(que5,sizeP );
		lastPriceInQueue(queMin34,pjPrice);//每分钟的最后均价
		count++;
		//==========行情更新不能阻塞=====================
		blockingQueue.add(1);//发送行情更新的消息----一分钟执行一次--
	}
	/**
	 * 自定义时间记录的
	 * author:xumin 
	 * 2016-11-30 上午11:45:55
	 */
	public void initSelfTimeWorkerEvent() {
		listSelfHalfMin[index] = LastPrice;
		++index;
		//==========行情更新不能阻塞=====================
		blockingQueue.add(2);//发送行情更新的消息----一分钟执行一次--
	}

	private static Double getPJPrice(Queue<Double> que, int size) {
		Double[]  temp = new Double[size];
		temp = que.toArray(temp);
		int s = temp.length;
		double total = 0;
		for(double dd : temp)
			total+=dd;
		double target = total/s;
		BigDecimal bd = new BigDecimal(target);  
		return bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 讲最新数据插入队列，如果队列满，则消费首位之后再入队列
	 * @param price 
	 * @param que 
	 */
	public static void lastPriceInQueue(Queue<Double> que, double price) {
		if(!que.offer(price)){
			//如果队列满，则取出队首再插入
			que.poll();//取出并丢弃
			que.add(price);
		}
	}

	public void saveToFile() {
		String str = JSON.toJSONString(queMin34);
		String path = getPathSym();
		FileUtils.writeToFileAll(str,path);
	}

	
	

}
