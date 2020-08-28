package me.vinfer.learnmq.basic;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 创建一个消息生成对象，发送单向消息（单发）
 *      单向消息：客户端在消息发送完成后立即返回，broker不会再有回调给客户端
 *              即客户端发完消息后不能拿到broker的回调
 *              单向消息适用于对发送结果不关心的业务场景，如日志发送
 * 单向消息的发送与同步消息的发送大体相同，没有回调处理
 *
 * @author Vinfer
 * @date 2020-08-26  23:28
 **/
public class OnewayProducer {


    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-group";

    private static final String DEFAULT_TOPIC = "test-message";

    private static final int TOTAL_SEND = 10;


    public static void main(String[] args) {
        //创建一个消息生产者对象
        DefaultMQProducer producer = createProducer();
        try {
            //启动生产者
            producer.start();
            System.out.println("oneway-producer started...");
            //发送单向消息
            sendOneWayMsg(producer);
            //单向消息的发送也是同步调用，因此消息发送方法完成后可以立即关闭生产者对象
            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static DefaultMQProducer createProducer(){
        //创建一个默认的生产者对象，并且传入生产者组名
        DefaultMQProducer producer = new DefaultMQProducer(DEFAULT_GROUP_NAME);
        //指定name-server的地址
        producer.setNamesrvAddr(NAME_SRV_ADDR);
        return producer;
    }

    static Message generateMsg(String messageBody){
        return new Message(DEFAULT_TOPIC, "basic-oneway-message",messageBody.getBytes());
    }

    static void sendOneWayMsg(DefaultMQProducer producer) throws Exception {
        //发送10条消息
        for (int i = 1; i <= TOTAL_SEND; i++) {
            //创建消息对象
            Message msg = generateMsg("message-"+i);
            //发送消息，由于发送的是单向消息，此时不再有回到结果
            producer.sendOneway(msg);
            Thread.sleep(1);
        }
    }


}
