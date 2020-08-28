package me.vinfer.learnmq.delay;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 创建一个消息生产者对象，发送延时消息
 *      延时消息：这里的延时指的是消费的延时，并不是延时发送
 *              客户端要发送的消息在设置延时级别之后仍会将消息立刻发送
 *              而在消费端是在该延时时间之后才会进行消费（pushConsumer则是broker在延时时间之后才进行推送）
 *              rocketmq对延时的时间只支持固定的几个级别，不允许自定义
 *      延时消息的应用场景：
 *              在电商中，提交一个订单时就可以提交一个延时消息，比如30分钟后去查看订单状态，
 *              如果仍旧未付款，那么就可以取消该订单
 *
 * @author Vinfer
 * @date 2020-08-27  11:03
 **/
public class DelayProducer {


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

    static Message generateMsg(String messageBody){
        return new Message(DEFAULT_TOPIC, "delay-sync-message",messageBody.getBytes());
    }

    static void sendSyncMsg(DefaultMQProducer producer) throws Exception {
        //发送10条消息
        for (int i = 1; i <= TOTAL_SEND; i++) {
            //创建消息对象
            Message msg = generateMsg("message-"+i);

            /*
            * 实现延时发送：对需要发送的消息对象设置延时级别
            * 消费端如何拿到延时的时间：
            *        System.currentTimeMillis() - msg.getStoreTimestamp()
            * */
            msg.setDelayTimeLevel(2);

            //发送消息，由于发送的是同步消息，因此可以接收broker的返回结果
            SendResult sendResult = producer.send(msg);
            System.out.println(sendResult);
            /*
             * 稍微让发送有一个间隔，进行短暂的sleep
             * */
            Thread.sleep(1);
        }
    }


}
