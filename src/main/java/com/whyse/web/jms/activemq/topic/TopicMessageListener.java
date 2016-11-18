package com.whyse.web.jms.activemq.topic;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 * 这个就能保证订阅信息，订阅关系在配置文件里面
 * 
 * author:xumin 
 * 2016-9-27 下午2:36:34
 */
public class TopicMessageListener implements MessageListener{

	/**
	 * @param args
	 * author:xumin 
	 * 2016-8-19 上午11:53:06
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
		System.err.println(Thread.currentThread().getId()+"-----------");
		if(message instanceof ObjectMessage){
			ObjectMessage oMsg = (ObjectMessage)message;
			System.err.println(message);
		}
		if(message instanceof TextMessage){
			TextMessage tMsg = (TextMessage)message;
			String json;
			try {
				json = tMsg.getText();
				System.err.println(json);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
