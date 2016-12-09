package com.whyse.myLiangHua.real;

import java.math.BigDecimal;

import com.whyse.main.TimeUtil;

/**
 * 每一个品种有不同的动态参数以及记录
 * author:xumin 
 * 2016-12-8 下午2:49:47
 */
public class TraderLHOptImportReal {
	/**
	 * 1:买开状态  0：平仓状态   -1：卖开状态
	 */
	volatile int byOrSell = 0;
	volatile double openP = 0;
	volatile double closeP = 0;
	MDLHClientImpl item;
	public TraderLHOptImportReal(MDLHClientImpl mdlhClientImpl) {
		item = mdlhClientImpl;
	}
	/**
	 * @param args
	 * author:xumin 
	 * 2016-11-15 下午5:00:57
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 行情更新了，均线已经重新计算完毕，看本次策略情况，是否做出动作
	 * author:xumin 
	 * 2016-11-17 下午5:01:40
	 * @param item 
	 */
	public void doOrNot() {
		
		double l5_10_0 = item.list5[0] - item.list10[0];
		double l5_10_1 = item.list5[1] - item.list10[1];
		double l5_10_2 = item.list5[2] - item.list10[2];
		double l5_10_3 = item.list5[3] - item.list10[3];
		
		double l10_20_0 = item.list10[0] - item.list20[0];
		double l10_20_1 = item.list10[1] - item.list20[1];
		double l10_20_2 = item.list10[2] - item.list20[2];
		double l10_20_3 = item.list10[3] - item.list20[3];
		
		double l20_30_0 = item.list20[0] - item.list30[0];
		double l20_30_1 = item.list20[1] - item.list30[1];
		double l20_30_2 = item.list20[2] - item.list30[2];
		double l20_30_3 = item.list20[3] - item.list30[3];
		
		//--------------------------------------------------------------
		Double AskPrice1 = (Double) item.LastMdMap.get("AskPrice1");//申卖价一
		Double BidPrice1 = (Double) item.LastMdMap.get("BidPrice1");// 申买价一
		Double ma20CK = item.list20[0]/250 ;
		Double ma21CK = ma20CK*1.2;
		
		
		String time = TimeUtil.getTodayDayTime();
		
		if(byOrSell==0 && l20_30_0>0 && l20_30_1>0 && l10_20_3<0 && l10_20_2<0 && l10_20_1<0
				&& (l10_20_0>0 || Math.abs(l10_20_0)<ma20CK)  && (l5_10_0>0 || Math.abs(l5_10_0)<ma21CK) ){
			//20报价均线已经上穿30； 10报价均线即将上穿20；5报价均线已经上穿或者即将上穿10  ->判断买入
			byOrSell = 1;
			openP = item.LastPrice;
			doAction(2,openP);
			System.out.println(item.mainSym+" - "+time+"+++++买开价钱:"+openP);//AskPrice1
			printLastPrice();
			return;
		}
		
		if(byOrSell==1 && l5_10_2>0 && l5_10_1>0 && (l5_10_0<0)){
			// 5均价即将下穿10，警告。多头行情运行中时，这是卖平信号
			closeP = item.LastPrice;
			doAction(1,closeP);
			System.err.println(item.mainSym+" - "+time+"####卖平价钱:"+closeP);//BidPrice1
			
			
			if(openP<closeP){
				System.err.println("赚钱:"+(closeP-openP));
			}else{
				System.err.println("亏损:"+(closeP-openP));
			}
			
			//-------------
			byOrSell = 0;
			openP = 0;
			closeP = 0;
			printLastPrice();
			return;
		}
		
		//==============下面是空头===================
		if(byOrSell ==0 && l20_30_0<0 && l20_30_1<0 && l10_20_3>0 && l10_20_2>0 && l10_20_1>0
				&& (l10_20_0<0 || Math.abs(l10_20_0)<ma20CK) && (l5_10_0<0 || Math.abs(l5_10_0)<ma21CK)){
			//30均线已经在上面；10均线即将下穿20 ;5均价已经下穿  -->判断卖空
			byOrSell = -1;
			openP = item.LastPrice;
			doAction(-2,openP);
			System.out.println(item.mainSym+" - "+time+"---卖开价钱:"+openP);//BidPrice1
			printLastPrice();
			return;
		}
		
		if(byOrSell==-1 && l5_10_2<0 && l5_10_1<0 && (l5_10_0>0)){
			// 
			closeP = item.LastPrice;
			doAction(-1,closeP);
			System.err.println(item.mainSym+" - "+time+"####买平价钱:"+closeP);//AskPrice1
			if(openP>closeP){
				System.err.println("赚钱:"+(openP-closeP));
			}else{
				System.err.println("亏损:"+(openP-closeP));
			}
			
			openP = 0;
			closeP = 0;
			byOrSell = 0;
			printLastPrice();
			return;
		}
		
	}
	
	//============================================================
	/**
	 * 
	 * @param flag 2买开  1卖平  -1 买平   -2卖开
	 * author:xumin 
	 * 2016-11-18 下午4:41:01
	 */
	private void doAction(int flag,double price) {
//		TraderHelper.enterOrder(flag,price,item);
	}
	private void printLastPrice() {
		Double[] array = new Double[5];
		array = item.que5.toArray(array);
		System.out.print("最近几次报价");
		for(int i=0;i<4;i++){
			System.out.print(array[i]+" ");
		}
		System.out.println("=================================");
	}
	//=======================================================
	/**
	 * 本次执行半分钟线：
	 * 策略1分钟均值  2分钟均值  4分钟均值（先后突破），  然而20分钟内振幅小， 40小时内振幅小
1,2,4逆势的时候，平仓
	 * author:xumin 
	 * 2016-11-30 下午5:16:43
	 */
	public void optLeve2() {
		// TODO Auto-generated method stub
		double[]  array = item.listSelfHalfMin;
		int index = item.index;
		double m1V = getPJZ(array,index,index-2);
		double m2V = getPJZ(array,index,index-4);
		double m4V = getPJZ(array,index,index-8);
		double m40V = getPJZ(array,index-8,index-88);//除头前
		//================================
		int willQS = willQS(m1V,m2V,m4V);//获取是否形成趋势
		boolean willTP = willTPYD(m1V,m2V,m4V,m40V);//获取是否突破
		boolean isPZ = isPZ(array,m40V,index-8,index-88);//获取是否盘整
	}
	/**
	 * 前几分跟之前样本平均值做对比，判定是否突破
	 * @param m1v
	 * @param m2v
	 * @param m4v
	 * @param m40v
	 * @return
	 * author:xumin 
	 * 2016-12-8 下午2:24:38
	 */
	private boolean willTPYD(double m1v, double m2v, double m4v, double m40v) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * 40分钟内80个点，最大涨幅不超过当值的%1+1，绝大多数落点在0.3%（80%），少于5%
的抽样点落在0.7-1之间，就可判定是横盘
	 * 样本内下标，是否判定为盘整
	 * @param array
	 * @param m40v
	 * @param lastIndex
	 * @param startIndex
	 * @return
	 * author:xumin 
	 * 2016-12-8 下午2:23:57
	 */
	private boolean isPZ(double[] array, double m40v, int lastIndex, int startIndex) {
//		int ssMax = (int) (m40v/100)+1;//少数的最大限度
		double ssMin = m40v*0.007;//少数最小限度
		double dsLD = m40v*0.003;//多数落点的最大值
		int dsCount = 0;
		int ssCount = 0;
		for(int i=startIndex;i<lastIndex;i++){
			double item = array[i];
			if(Math.abs(item-m40v)<dsLD)
				dsCount++;
			if(Math.abs(item-m40v)>ssMin)
				ssCount++;
		}
		int allCount = lastIndex-startIndex;
		if(dsCount*1.0/allCount>=0.8 && ssCount*1.0/allCount<=0.05){
			return true;
		}
		return false;
	}
	/**
	 * 形成趋势，主键上升或者逐渐下降
	 * @param m1v
	 * @param m2v
	 * @param m4v
	 * @return
	 * author:xumin 1上升 0混乱  -1下降
	 * 2016-12-8 上午10:39:23
	 */
	private int willQS(double m1v, double m2v, double m4v) {
		if(m1v>m2v && m2v>m4v)
			return 1;
		if(m1v<m2v && m2v<m4v)
			return -1;
		return 0;
	}
	/**
	 * 计算从[startIndex,lastIndex)的平均值,保留3为小数
	 * @param array
	 * @param lastIndex
	 * @param startIndex
	 * @return
	 * author:xumin 
	 * 2016-11-30 下午5:52:58
	 */
	private double getPJZ(double[] array, int lastIndex, int startIndex) {
		double tar = 0;
		for(int i=startIndex;i<lastIndex;i++)
			tar+=array[i];
		tar = tar/(lastIndex-startIndex);
		BigDecimal bd = new BigDecimal(tar);  
		return bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}


}
