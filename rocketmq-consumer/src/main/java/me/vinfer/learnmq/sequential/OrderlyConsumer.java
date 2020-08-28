package me.vinfer.learnmq.sequential;

import io.netty.util.CharsetUtil;
import me.vinfer.learnmq.normal.PushConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * 顺序消费者
 *
 * 实现思路：
 *      由于并发消费时，消费者对一个消息队列是使用多线程的方式进行消费的
 *      因此要想保证顺序消费，必须要对单个消息队列只使用单线程去消费
 *      而rocketmq对此提供了MessageListenerOrderly接口，
 *      传入该接口实现的registerListener方法所注册的监听器
 *      就会对消息进行顺序消费
 *
 * @author Vinfer
 * @date 2020-08-27  10:26
 **/
public class OrderlyConsumer {

    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-consumer-group";

    private static final String DEFAULT_TOPIC = "test-message";

    static class MessageListener implements MessageListenerOrderly {
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            try {
                /*
                * 对每个消息队列都是用单线程去消费
                * 最终保证局部顺序消费
                * 消费的结果可能会是：
                *       1039-create >> 1065-create >> 1039-pay >> ...
                *       中间穿插了其他订单的消费，但是对于同一个订单的消费顺序一定和其入队顺序一致
                *       也就是不可能会出现以下情况：
                *           1039-pay >> ... >> 1039-create >> ...
                *
                * */
                for (MessageExt msg : msgs) {
                    System.out.println("consuming msg: [" +
                            //打印一下线程名称，方便看清是单线程消费的
                            "consuming thread: "+Thread.currentThread().getName()+
                            ", topic: "+msg.getTopic()+
                            ", queueId: "+msg.getQueueId()+
                            ", keys: "+msg.getKeys()+
                            ", tags: "+msg.getTags()+
                            ", body: "+new String(msg.getBody(), CharsetUtil.UTF_8)+"]");
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }catch (Exception e){
                e.printStackTrace();
                return ConsumeOrderlyStatus.valueOf("FAIL");
            }
        }
    }

    public static void main(String[] args) {
        DefaultMQPushConsumer pushConsumer = createConsumer();
        try {
            //订阅消息主题以及tag
            pushConsumer.subscribe(DEFAULT_TOPIC, "*");
            //设置消息的监听器，在监听器对消息进行消费以及回调处理
            pushConsumer.setMessageListener(new MessageListener());
            //启动消费者
            pushConsumer.start();
            System.out.println("push-consumer started...");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    static DefaultMQPushConsumer createConsumer(){
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(DEFAULT_GROUP_NAME);
        pushConsumer.setNamesrvAddr(NAME_SRV_ADDR);
        return pushConsumer;
    }


}
