package com.whyse.web.ctptest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.whyse.lib.md.CThostFtdcReqUserLoginField;
import com.whyse.lib.md.MdLibrary.THOST_TE_RESUME_TYPE;
import com.whyse.main.selfmodel.LoginMdBean;
import com.whyse.main.trader.BridjUtils;
import com.whyse.md.server.impl.MdMngServiceImpl;
import com.whyse.md.server.impl.MdServiceImpl;


@Controller
public class MDTestController {
	
	public static ExecutorService executorService = Executors.newFixedThreadPool(10);
	private MdServiceImpl mdService;
	private LoginMdBean loginMdBean;
	
	@PostConstruct
	public void init(){
		
		final String localFilePath = "C:/ctpfile/md/";//C:/ctpfile/md/
		LoginMdBean.setLocalFilePath(localFilePath);
		//==========================================================================
		loginMdBean = new LoginMdBean();
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("7090");
		req.UserID().setCString("******");
		req.Password().setCString("538308");
		req.UserProductInfo().setCString("ftdv1");
		loginMdBean.setReq(req);
		
		loginMdBean.setFrontUrl("tcp://180.169.75.19:41213");//实时行情的接口41213
		loginMdBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginMdBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		
		
		//==========================================================================
		
		System.err.println("============================================================");
	}
	@RequestMapping("/release.do")
	@ResponseBody
	public int release(String userId){
		mdService.release();
		return 0;
	}
	@RequestMapping("/start.do")
	@ResponseBody
	public int start(String userId){
		List<String> list = new ArrayList<>(1);
		list.add("IF1612");
		list.add("rb1701");
		mdService = MdMngServiceImpl.newMDService(loginMdBean,list);
		return 0;
	}
	@RequestMapping("/doLogout.do")
	@ResponseBody
	public Object doLogout(){
		mdService.ReqUserLogout();
		return BridjUtils.mapTar;
	}
	
}
