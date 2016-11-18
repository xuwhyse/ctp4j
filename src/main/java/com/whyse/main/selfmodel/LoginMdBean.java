package com.whyse.main.selfmodel;

import com.whyse.lib.md.CThostFtdcReqUserLoginField;
import com.whyse.lib.md.MdLibrary.THOST_TE_RESUME_TYPE;

public class LoginMdBean {
	/**
	 * 我们的客户端id用户名密码等
	 */
	private CThostFtdcReqUserLoginField req;
	/**
	 * 不同的远程主机地址
	 */
	/**
	 * author:xumin 
	 * 2016-4-21 下午7:41:08
	 */
	private String frontUrl;
	/**
	 * 用户session文件
	 */
	private static String localFilePath;
	private THOST_TE_RESUME_TYPE privateTopic =THOST_TE_RESUME_TYPE.THOST_TERT_QUICK;
	private THOST_TE_RESUME_TYPE publicTopic =THOST_TE_RESUME_TYPE.THOST_TERT_QUICK;
	
	public THOST_TE_RESUME_TYPE getPrivateTopic() {
		return privateTopic;
	}

	public void setPrivateTopic(THOST_TE_RESUME_TYPE privateTopic) {
		this.privateTopic = privateTopic;
	}

	public THOST_TE_RESUME_TYPE getPublicTopic() {
		return publicTopic;
	}

	public void setPublicTopic(THOST_TE_RESUME_TYPE publicTopic) {
		this.publicTopic = publicTopic;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public  CThostFtdcReqUserLoginField getReq() {
		return req;
	}

	public  void setReq(CThostFtdcReqUserLoginField req) {
		this.req = req;
	}

	public static String getLocalFilePath() {
		return localFilePath;
	}

	public static void setLocalFilePath(String localFilePath) {
		LoginMdBean.localFilePath = localFilePath;
	}

}
