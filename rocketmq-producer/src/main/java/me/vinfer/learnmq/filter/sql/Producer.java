package me.vinfer.learnmq.filter.sql;

import me.vinfer.learnmq.basic.SyncProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;


/**
 * 发送带有UserProperties的消息
 * 让消费端可以通过sql过滤消费消息
 *
 * @author Vinfer
 * @date 2020-08-27  15:51
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

    static void sendMessage(DefaultMQProducer producer) throws Exception {
        int sendTimes = 10;
        for (int i = 0; i < sendTimes; i++) {
            String body = "message-"+i;
            Message message = generateMsg(body);
            /*
            * 通过添加UserProperties，有对应的key-value
            * 那么在消费端就可以sql对消息进行过滤
            * */
            message.putUserProperty("i", String.valueOf(i));
            SendResult sendResult = producer.send(message);
            System.out.println(sendResult);
            Thread.sleep(1);
        }
    }


}
