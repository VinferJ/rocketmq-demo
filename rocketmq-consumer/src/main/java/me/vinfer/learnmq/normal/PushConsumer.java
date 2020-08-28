package me.vinfer.learnmq.normal;

import io.netty.util.CharsetUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * 创建一个消费者对象对消息进行被动消费（broker主动向consumer进行消息推送：pushConsumer）
 * pushConsumer使用的是短连接，对服务端压力会大些，需要服务端不断地主动推送
 * pullConsumer会使用长连接，对服务器的压力会小一些，但需要客户端主动拉取（客户端压力大些）
 *
 * 消息消费的步骤：
 *      1. 创建一个消费者对象，并且设置消费者组名
 *      2. 指定name-server的地址
 *      3. 订阅主题topic和tag（tag设置为“*”时会订阅当前topic的全部内容）
 *      4. 设置回调函数，处理消息
 *      5. 启动消费者进行持续监听
 *
 * @author Vinfer
 * @date 2020-08-26  23:40
 **/
public class PushConsumer {

    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-consumer-group";

    private static final String DEFAULT_TOPIC = "test-message";

    static class MessageListener implements MessageListenerConcurrently{

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            try {
                msgs.forEach(msg ->{
                    System.out.println("consuming msg: [" +
                            "topic: "+msg.getTopic()+
                            ", tags: "+msg.getTags()+
                            ", body: "+new String(msg.getBody(), CharsetUtil.UTF_8)+"]");
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }catch (Exception e){
                System.out.println("consuming fail");
                //在并发消费的模式下，不建议抛异常，而是返回RECONSUME_LATER
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
    }

    public static void main(String[] args) {
        DefaultMQPushConsumer pushConsumer = createConsumer();
        try {
            //订阅消息主题以及tag
            pushConsumer.subscribe("test-message", "*");
            //设置消息的监听器，在监听器对消息进行消费以及回调处理
            pushConsumer.setMessageListener(new MessageListener());

            /*
            * 消息的消费模式：
            *   一共有两种模式：
            *       负载均衡/集群消费（MessageModel.CLUSTERING）：所有消费者一起消费所有消息；
            *       广播模式（MessageModel.BROADCASTING）：所有消息都会被所有消费者消费一遍
            *   消费模式是针对在同一个组内的消费者，在同一个group下，默认是负载均衡模式
            * */
            //设置为广播模式消费
            //pushConsumer.setMessageModel(MessageModel.BROADCASTING);

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
