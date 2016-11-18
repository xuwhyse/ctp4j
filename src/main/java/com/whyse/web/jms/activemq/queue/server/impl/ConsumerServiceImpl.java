package com.whyse.web.jms.activemq.queue.server.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConsumerServiceImpl {

	@Autowired
	JmsTemplate jmsTemplate;
	/**
	 * @param args
	 * author:xumin 
	 * 2016-8-15 下午4:50:59
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 一次接受到一条信息
	 * @param destination
	 * @return
	 * author:xumin 
	 * 2016-8-16 上午9:56:07
	 */
	public int receive(Destination destination) {
		if(destination==null)
			destination = jmsTemplate.getDefaultDestination();
		TextMessage msg = (TextMessage) jmsTemplate.receive(destination);
		if(msg==null){
			System.err.println("已经没有数据");
			return 0;
		}
		try {
			System.err.println("从队列" + destination.toString() + "收到了消息：\t"
					+ msg.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
