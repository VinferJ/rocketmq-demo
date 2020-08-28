package me.vinfer.learnmq.basic;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;


/**
 * 创建一个生产者，发送同步消息,消息发送的方式是单消息发送
 *      单消息发送：遍历消息进行发送，每次只发送一条
 *      批量消息发送：将消息打包成一个List<Message>，发送该消息集合
 *
 *      同步消息：客户端向broker发送完消息后，线程会阻塞，直到broker回传一个接收结果
 *              同步消息的可靠性很高，常用的应用场景有：短信通知，重要的消息通知
 * 发送同步消息的步骤：
 *      1. 创建一个生产者，设置组名
 *      2. 指定name-server地址
 *      3. 启动该生产者(producer.start())
 *      4. 创建消息对象，指定topic以及设置消息体，还有tag（可选）
 *      5.发送消息
 *      6.关闭生产者
 *
 * @author Vinfer
 * @date 2020-08-26  21:48
 **/
public class SyncProducer {

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

    public static DefaultMQProducer createProducer(){
        //创建一个默认的生产者对象，并且传入生产者组名
        DefaultMQProducer producer = new DefaultMQProducer(DEFAULT_GROUP_NAME);
        //指定name-server的地址
        producer.setNamesrvAddr(NAME_SRV_ADDR);
        return producer;
    }

    public static Message generateMsg(String messageBody){
        return new Message(DEFAULT_TOPIC, "basic-sync-message",messageBody.getBytes());
    }

    public static void sendSyncMsg(DefaultMQProducer producer) throws Exception {
        //发送10条消息
        for (int i = 1; i <= TOTAL_SEND; i++) {
            //创建消息对象
            Message msg = generateMsg("message-"+i);
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
