package org.zbus.mq.perf;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.zbus.mq.Broker;
import org.zbus.mq.BrokerConfig;
import org.zbus.mq.SingleBroker;
import org.zbus.net.http.Message;
import org.zbus.rpc.service.Caller;

class Task extends Thread{
	Broker broker;
	int loopCount = 10000;
	String service = "MyService";
	long startTime;
	AtomicLong counter;
	@Override
	public void run() { 
		Caller p = new Caller(broker, service);
		for(int i=0;i<loopCount;i++){ 
			try {
				Message msg = new Message();
				msg.setBody("hello");
				p.invokeSync(msg, 2500); 
				long count = counter.incrementAndGet();
				if(count%2000 == 0){
					long end = System.currentTimeMillis();
					System.out.format("QPS: %.2f\n", count*1000.0/(end-startTime));
				}
				
			} catch (IOException e) { 
				e.printStackTrace();
			}
			
		}
	}
}

public class ServicePerf {
	public static void main(String[] args) throws Exception { 
		BrokerConfig config = new BrokerConfig(); 
		//config.setBrokerAddress("10.19.1.32:15555");
		config.setMaxTotal(200);
		config.setMaxIdle(200);  
		
		final Broker broker = new SingleBroker(config);
		
		final int loopCount = 10000; 
		final int threadCount = 100;
		
		AtomicLong counter = new AtomicLong(0);
		final long start = System.currentTimeMillis();
		Task[] tasks = new Task[threadCount];
		for(int i=0;i<tasks.length;i++){
			tasks[i] = new Task();
			tasks[i].broker = broker;
			tasks[i].loopCount = loopCount;
			tasks[i].startTime = start;
			tasks[i].counter = counter;
		}
		
		for(Task task : tasks){
			task.start();
		} 
		for(Task task : tasks){
			task.join();
		}
		
		
		System.out.println("===done===");
		//broker.close(); 
	}
}
