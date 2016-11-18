package com.whyse.web.jms.activemq.queue.server.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class ProducerServiceImpl {

	@Autowired
	JmsTemplate jmsTemplate;//这个实例化了两个，根据不同id竟然可以区分
	Destination destination = new ActiveMQQueue("xmTest");
	/**
	 * @param args
	 * author:xumin 
	 * 2016-8-11 下午6:05:25
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	public int sendMsg(final String msg) {
		MessageCreator messageCreator = new MessageCreator() {
			
			@Override
			public Message createMessage(Session arg0) throws JMSException {
				return arg0.createTextMessage(msg);
			}
		};
		jmsTemplate.send(messageCreator);
		jmsTemplate.send(destination, messageCreator);
		return 0;
	}

}
