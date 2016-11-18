package com.whyse.web.jms.activemq.topic.my;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.web.context.ContextLoaderListener;

/**
 * 继承这个类，自己实现topic
 * 重写onMessage
 * 
 * author:xumin 
 * 2016-9-28 下午4:53:26
 */
public abstract class AbsTopicMessageListener implements MessageListener{
	
	private static CachingConnectionFactory cachingConnectionFactory;
	
	//============================================

	/**
	 * @param args
	 * author:xumin 
	 * 2016-8-19 上午11:53:06
	 */
	public static void main(String[] args) {

	}
	
	public AbsTopicMessageListener(String topicName){
		init();
		if(cachingConnectionFactory==null){
			System.err.println("cachingConnectionFactory获取不到！！");
			return;
		}
		ActiveMQTopic notifyTopic = new ActiveMQTopic(topicName);//"xmTopic"
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(cachingConnectionFactory);
		container.setDestination(notifyTopic);//订阅的topic name
		container.setConcurrentConsumers(1);//这样会有三个订阅者,不同线程 消费同一个消息三次
		container.setMessageListener(this);
		
	}
	private void init() {
		if(cachingConnectionFactory==null){
			cachingConnectionFactory = (CachingConnectionFactory) 
					ContextLoaderListener.getCurrentWebApplicationContext().getBean("cachingConnectionFactory");
		}
	}
	//================================
	/**
	 * 这个是简单实现，能打印收到结果，具体需要各个实例自己覆盖重写
	 * @param message
	 * author:xumin 
	 * 2016-9-28 下午4:55:51
	 */
	@Override
	public void onMessage(Message message) {
		
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
