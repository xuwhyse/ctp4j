package com.whyse.web.jms.activemq.queue.server;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueMessageListener implements MessageListener{

	static Logger logger = LoggerFactory.getLogger(QueueMessageListener.class);
	/**
	 * @param args
	 * author:xumin 
	 * 2016-8-16 上午11:16:24
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 接受到了消息
	 * @param message
	 * author:xumin 
	 * 2016-8-16 上午11:17:24
	 */
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
