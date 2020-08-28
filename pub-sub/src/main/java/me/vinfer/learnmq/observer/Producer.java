package me.vinfer.learnmq.observer;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者线程，对应消息的发布者
 * @author Vinfer
 * @date 2020-08-23  17:08
 **/
public class Producer implements Runnable{

    private final AtomicInteger NUM = new AtomicInteger(0);

    private final BlockingQueue<Object> TASK_QUEUE;

    private final String producerId;

    public Producer(String producerId,BlockingQueue<Object> taskQueue){
        this.TASK_QUEUE = taskQueue;
        this.producerId = producerId;
        RegistryCenter.registry(producerId, this);
    }

    @Override
    public void run() {
        produce();
    }

    private void produce(){
        for (int i = 1; i <= 100; i++) {
            String msg = producerId+"-msg-"+i;//NUM.incrementAndGet();
            //MessageQueue.putMessage(msg);
            try {
                TASK_QUEUE.put(msg);
                System.out.println("produce msg: "+msg);
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getMessage(){
        return TASK_QUEUE.poll();
    }

}
