package me.vinfer.learnmq.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建一个消息生产者，发送批量消息
 *      发送批量消息：将消息打包成一个List<Message>，发送该消息集合
 *                  rocketmq中有限定，该集合的大小不可以超过4M
 *
 * @author Vinfer
 * @date 2020-08-27  11:28
 **/
public class BatchProducer {


    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-producer-group";

    private static final String DEFAULT_TOPIC = "test-message";

    private static final int TOTAL_SEND = 10;


    public static void main(String[] args) {
        //创建一个消息生产者对象
        DefaultMQProducer producer = createProducer();
        try {
            //启动生产者
            producer.start();
            System.out.println("producer started...");
            //发送同步消息
            sendSyncMsg(producer);
            //消息发送完成后，关闭生产者对象
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

    static List<Message> generateMsgList(){
        List<Message> msgList = new ArrayList<>();
        for (int i = 0; i < TOTAL_SEND; i++) {
            String body = "message"+i;
            msgList.add(new Message(DEFAULT_TOPIC, "delay-sync-message",body.getBytes()));
        }
        return msgList;
    }

    static void sendSyncMsg(DefaultMQProducer producer) throws Exception {
        //创建消息集合，发送批量消息
        List<Message> batchMessages = generateMsgList();

        //进行同步发送
        SendResult sendResult = producer.send(batchMessages);
        System.out.println(sendResult);
    }


}
