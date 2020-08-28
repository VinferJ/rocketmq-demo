package me.vinfer.learnmq.basic;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者线程，对应消息的发布者
 * @author Vinfer
 * @date 2020-08-23  17:08
 **/
public class Producer implements Runnable{

    private final AtomicInteger NUM = new AtomicInteger(0);

    @Override
    public void run() {
        produce();
    }

    private void produce(){
        for (int i = 1; i <= 100; i++) {
            String msg = "msg-"+NUM.incrementAndGet();
            MessageQueue.putMessage(msg);
            try {
                System.out.println("produce msg: "+msg);
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
