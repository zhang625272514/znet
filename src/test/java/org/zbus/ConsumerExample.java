package org.zbus;

import java.io.IOException;

import org.zbus.mq.Broker;
import org.zbus.mq.BrokerConfig;
import org.zbus.mq.Consumer;
import org.zbus.mq.MqConfig;
import org.zbus.mq.SingleBroker;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.MessageHandler;

public class ConsumerExample {
	public static void main(String[] args) throws Exception{  
		//创建Broker代表
		BrokerConfig brokerConfig = new BrokerConfig();
		brokerConfig.setBrokerAddress("127.0.0.1:15555");
		Broker broker = new SingleBroker(brokerConfig);
		
		MqConfig config = new MqConfig(); 
		config.setBroker(broker);
		config.setMq("MyMQ");
		
		//创建消费者
		@SuppressWarnings("resource")
		Consumer c = new Consumer(config);  
		
		c.onMessage(new MessageHandler() { 
			@Override
			public void handle(Message msg, Session sess) throws IOException {
				System.out.println(msg);
			}
		});

		//启动消费线程
		c.start();   
		
	} 
}
