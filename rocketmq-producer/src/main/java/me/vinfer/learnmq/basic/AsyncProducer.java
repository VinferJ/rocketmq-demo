package me.vinfer.learnmq.basic;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建一个生产者对象，发送异步消息（单发）
 *      异步消息：当客户端向broker发送完消息后，线程不会进行阻塞等待结果返回
 *              而是会继续往下走，异步消息的可靠性不如同步消息高，但响应更快
 *              因此异步消息适用于对响应时间敏感的业务场景，
 *              即发送端不能容忍长时间地等待broker的响应
 *              异步消息同样可以接收broker的回调结果，需要指定一个回调函数（SendCallback）
 *
 * 发送异步的步骤与发送同步消息的步骤大体一致，对回调的处理不同
 *
 * @author Vinfer
 * @date 2020-08-26  22:34
 **/
public class AsyncProducer {

    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-group";

    private static final String DEFAULT_TOPIC = "test-message";

    private static final AtomicInteger SEND_COUNT = new AtomicInteger(0);

    //private static final CountDownLatch LATCH = new CountDownLatch(10);

    private static final int TOTAL_SEND = 10;

    static class Callback implements SendCallback{

        public void onSuccess(SendResult sendResult) {
            System.out.println("send success: "+sendResult);
            SEND_COUNT.incrementAndGet();
            //LATCH.countDown();
        }

        public void onException(Throwable e) {
            System.out.println("send fail");
            e.printStackTrace();
            SEND_COUNT.incrementAndGet();
            //LATCH.countDown();
        }
    }

    public static void main(String[] args) {
        DefaultMQProducer producer = createProducer();
        try {
            producer.start();
            System.out.println("producer started...");
            //发送异步消息
            sendAsyncMsg(producer);
            /*
            * 由于发送异步消息，线程不会阻塞，所以如果不进行判断在关闭producer
            * 会引起发送异常（没发完消息就已经关闭了）
            * 而因为发送会完成后会有回调函数进行处理，有几种解决方案：
            *   1. 设置一个AtomicInteger来进行计数，在每次回调处理完都进行自增1
            *      因为回调处理在其他线程中执行，所以需要用到原子变量
            *      对该atomic-int进行监听，当达到任务发送数量时，关闭producer，并结束监听
            *   2. 使用countDownLatch，latch的初值设置为任务发送数
            *      在调用发送异步消息的方法后，进行latch.await，await的后面写producer.shutdown
            *      在每次回调处理完成后都进行一次latch.countDown
            *   3. 不手动关闭producer
            *
            * */
            while (true){
                //对该原子整型进行监听，当达到任务发送数后进行producer的关闭，并结束自旋
                if(SEND_COUNT.get() == TOTAL_SEND){
                    producer.shutdown();
                    break;
                }
            }
            /*LATCH.await();*/
            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }


    static DefaultMQProducer createProducer(){
        //创建一个默认的生产者对象，并且传入生产者组名
        DefaultMQProducer producer = new DefaultMQProducer(DEFAULT_GROUP_NAME);
        //指定name-server的地址
        producer.setNamesrvAddr(NAME_SRV_ADDR);
        return producer;
    }

    static Message generateMsg(String messageBody){
        return new Message(DEFAULT_TOPIC, "basic-async-message",messageBody.getBytes());
    }

    static void sendAsyncMsg(DefaultMQProducer producer) throws Exception {
        //发送10条消息
        for (int i = 1; i <= TOTAL_SEND; i++) {
            //创建消息对象
            Message msg = generateMsg("message-"+i);
            /*
            * 发送消息，由于发送的是异步消息，broker的结果回调将会在传入的callback对象中处理，
            * 此时的send方法不再进行结果接收，并且由于发送的是异步消息，
            * 也不再需要进行线程休眠
            * 由于异步发送消息，所以发送的次序也不一定是顺序的
            * 同时需要注意的是，因为异步消息不会阻塞，所以producer.shutdown一定要在全部消息发送完成后才能关闭
            * 否则会因为消息还没发送出去就直接关闭通道，而报异常
            * */
            producer.send(msg, new Callback());
        }
    }


}
