package com.whyse.web.jms.activemq.queue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.whyse.main.trader.BridjUtils;
import com.whyse.web.jms.activemq.queue.server.impl.ConsumerServiceImpl;
import com.whyse.web.jms.activemq.queue.server.impl.ProducerServiceImpl;
import com.whyse.web.jms.activemq.topic.ProducerTopic;


@Controller
public class JmsQueueController {
	
	@Autowired
	ProducerServiceImpl producerServiceImpl;
	@Autowired
	ConsumerServiceImpl consumerServiceImpl;
	
	@Autowired
	ProducerTopic producerTopic;
//	public static ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	@PostConstruct
	public void init(){
	}
	@RequestMapping("/producer.do")
	@ResponseBody
	public Object producer(String msg){
		if(StringUtils.isBlank(msg))
			msg = "hello jms";
		int flag = producerServiceImpl.sendMsg(msg);
		return BridjUtils.mapTar;
	}
	@RequestMapping("/consumer.do")
	@ResponseBody
	public Object consumer(String msg){
		int flag = consumerServiceImpl.receive(null);
		return BridjUtils.mapTar;
	}
	//======================================
	@RequestMapping("/producerTopic.do")
	@ResponseBody
	public Object producerTopic(String msg){
		if(StringUtils.isBlank(msg))
			msg = "hello jms topic";
		int flag = producerTopic.sendMsg(msg);
		return BridjUtils.mapTar;
	}
}
