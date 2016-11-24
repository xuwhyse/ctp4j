package com.whyse.md;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bridj.BridJ;
import com.whyse.lib.md.CThostFtdcReqUserLoginField;
import com.whyse.lib.md.MdLibrary.THOST_TE_RESUME_TYPE;
import com.whyse.main.selfmodel.LoginMdBean;
import com.whyse.md.server.impl.MdMngServiceImpl;
import com.whyse.md.server.impl.MdServiceImpl;
import com.whyse.web.ctptest.MDTestController;

public class TestMd {

	/**
	 * @param args
	 * author:xumin 
	 * 2016-8-29 上午11:31:37
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		initNativeLibrary();
		
		LoginMdBean.setLocalFilePath("C:/ctpfile/md/");
		LoginMdBean loginBean = new LoginMdBean();
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("7090");
		//****** + 825020   ;;;  81002445 +108652
		req.UserID().setCString("81002445");
		req.Password().setCString("108652");//行情：825020  交易、资金 538308
		req.UserProductInfo().setCString("ftdv1");
		loginBean.setReq(req);
		
		loginBean.setFrontUrl("tcp://180.169.75.19:41213");//实时行情的接口41213
		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		
		List<String> list = new ArrayList<>(1);
//		list.add("IF1612");
		list.add("rb1701");
		list.add("p1701");
		list.add("JM1701");
//		list.add("RB1701");
		//==========================================================================
		MdServiceImpl mdServiceImpl = MdMngServiceImpl.newMDService(loginBean,list);
		
		mdServiceImpl = MdMngServiceImpl.getMDService("brokerId","userId");
	}

	public static void initNativeLibrary() {
		String os = System.getProperty("os.name").toLowerCase();//Windows 8.1
		String arch = System.getProperty("os.arch").toLowerCase();//amd64
		String sysTemPath = MDTestController.class.getResource("/").getPath();
		String path = null;
		if(os.contains("win"))
			path = sysTemPath+"lib/win64/thostmduserapi.dll";
		else
			path = sysTemPath+"lib/linux64/thostmduserapi.so";
		loadLibrary(path,"Md");
	}

	public static void loadLibrary(String path, String name) {
		File f = new File(path);
		System.err.println(path);
		if(f.exists()){
//			BridJ.setNativeLibraryFile(name,f);
			try {
				System.err.println("ssssssssssssssss");
				BridJ.getNativeLibrary(name, f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
