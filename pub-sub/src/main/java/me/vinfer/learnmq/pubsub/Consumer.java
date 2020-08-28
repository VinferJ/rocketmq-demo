package me.vinfer.learnmq.pubsub;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Vinfer
 * @date 2020-08-23  22:44
 **/
public class Consumer implements Runnable{

    private final BlockingQueue<Object> msgQueue = new ArrayBlockingQueue<Object>(100);

    /**
     * 订阅者id，进行服务订阅时需要该信息
     * 映射到中间件中就是consumer-host的配置
     * */
    private final String subscriberId;

    public Consumer(String subscriberId, List<String> publisherIds){
        this.subscriberId = subscriberId;
        RegistryCenter.subReg(subscriberId, this);
        RegistryCenter.subscribe(subscriberId, publisherIds);
    }

    @Override
    public void run() {
        while (true){
            if(!msgQueue.isEmpty()){
                Object msg = msgQueue.poll();
                System.out.println(subscriberId+" consume: "+msg);
            }
        }
    }

    protected void receiveMsg(Object msg){
        try {
            msgQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
