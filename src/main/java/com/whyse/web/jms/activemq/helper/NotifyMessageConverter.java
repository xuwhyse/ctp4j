package com.whyse.web.jms.activemq.helper;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class NotifyMessageConverter implements MessageConverter{

	static Logger logger = LoggerFactory.getLogger(NotifyMessageConverter.class);
	/**
	 * @param args
	 * author:xumin 
	 * 2016-8-16 上午10:33:29
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
		   logger.debug("Receive JMS message :"+message);
		 }
		if(message instanceof ObjectMessage){
			ObjectMessage oMsg = (ObjectMessage)message;
			System.err.println(message);
		}
		if(message instanceof TextMessage){
			TextMessage tMsg = (TextMessage)message;
			String json = tMsg.getText();
			System.err.println(json);
		}
		return null;
	}
	@Override
	public Message toMessage(Object arg0, Session arg1) throws JMSException,
			MessageConversionException {
		// TODO Auto-generated method stub
		return null;
	}

}
