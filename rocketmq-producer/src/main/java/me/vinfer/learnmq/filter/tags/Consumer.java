package me.vinfer.learnmq.filter.tags;

import io.netty.util.CharsetUtil;
import me.vinfer.learnmq.filter.PushConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * 消费者通过订阅不同的tag，实现消息的过滤
 *
 * @author Vinfer
 * @date 2020-08-27  15:52
 **/
public class Consumer extends PushConsumer {

    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = createConsumer();
        try {
            //通过订阅想要的tag，过滤掉不需要消费的消息
            consumer.subscribe("test-message", "tag1 || tag2");
            consumer.registerMessageListener(new PushConsumer.MessageListener());
            consumer.setMessageModel(MessageModel.BROADCASTING);
            consumer.start();
            System.out.println("consumer started...");
        } catch (MQClientException e) {
            e.printStackTrace();
        }


    }

}
