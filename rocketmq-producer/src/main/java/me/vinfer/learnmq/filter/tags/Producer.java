package me.vinfer.learnmq.filter.tags;

import me.vinfer.learnmq.basic.SyncProducer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;


/**
 * 生产者发送不同tag的消息
 *
 *
 * @author Vinfer
 * @date 2020-08-27  15:52
 **/
public class Producer extends SyncProducer {

    public static void main(String[] args) {
        DefaultMQProducer producer = createProducer();
        try {
            producer.start();
            sendMessage(producer);
            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Message generateMsg(String messageBody,String tags){
        Message message = SyncProducer.generateMsg(messageBody);
        message.setTags(tags);
        return message;
    }

    public static void sendMessage(DefaultMQProducer producer) throws Exception {
        /*
        * tag1，tag2，tag3的消息各发3条
        * */
        int sendTimes = 3;
        for (int i = 0; i < sendTimes; i++) {
            String body = "message-"+i;
            SendResult sendResult = producer.send(generateMsg(body, "tag1"));
            System.out.println(sendResult);
        }
        for (int i = 0; i < sendTimes; i++) {
            String body = "message-"+i;
            SendResult sendResult = producer.send(generateMsg(body, "tag2"));
            System.out.println(sendResult);
        }
        for (int i = 0; i < sendTimes; i++) {
            String body = "message-"+i;
            SendResult sendResult = producer.send(generateMsg(body, "tag3"));
            System.out.println(sendResult);
        }
    }


}
