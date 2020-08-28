package me.vinfer.learnmq.sequential;

import io.netty.util.CharsetUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import java.util.List;


/**
 * 发送顺序消息
 * 消息的顺序包括两种模式：全局顺序，局部顺序，顺序消息更多使用的是局部顺序
 * 由于一个broker中，每个topic包含多个MessageQueue（默认是4个），在客户端向broker发送
 * 消息时，会以轮询的方式（默认）向这4个队列存放消息，而消费者消费消息时，对每个MessageQueue的消费
 * 是以多线程的方式进行的，因此最终不能保证消费的顺序与发送的顺序一致
 *
 * 发送局部顺序消息的思路：
 *      1. 因为队列的特性是FIFO，生产局部顺序的消息，只要把需要顺序被消费的消息放到同一个MessageQueue中
 *      2. 向producer.send方法中传入一个MessageSelector，
 *         用一个业务id对一个消息指定固定的MessageQueue，保证同一个业务的消息发送到同一个队列中
 *         让消费者按照该id去选择消费，并做对应的业务处理
 *
 * @author Vinfer
 * @date 2020-08-27  09:16
 **/
public class SequentialProducer {

    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-producer-group";

    private static final String DEFAULT_TOPIC = "test-message";


    public static void main(String[] args) {
        //创建一个消息生产者对象
        DefaultMQProducer producer = createProducer();
        try {
            //启动生产者
            producer.start();
            System.out.println("producer started...");
            //构建订单集合
            List<OrderStep> orderSteps = OrderStep.buildOrder();
            //发送同步消息(订单消费需要高可靠性)
            sendSyncMsg(producer, orderSteps);
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

    static Message generateMsg(String messageBody,String key){
        //局部顺序消息，需要给每个消息再指定一个key
        return new Message(DEFAULT_TOPIC, "sequential-message",key,messageBody.getBytes(CharsetUtil.UTF_8));
    }

    static void sendSyncMsg(DefaultMQProducer producer, List<OrderStep> orderStepList) throws Exception {
        int count = 0;
        for (OrderStep order : orderStepList) {
            String messageBody = order.toString();
            /*
            * 该send方法的三个参数：
            *   1.msg：需要发送的消息
            *   2.MessageQueueSelector，消息队列选择器，通过该选择器，根据业务id选择指定的队列
            *   3.业务id（如订单id）
            * */
            SendResult sendResult = producer.send(generateMsg(messageBody, "KEYS" + (count++)), new MessageQueueSelector() {
                /**
                 * 对消息队列进行选择
                 * @param mqs   topic中消息队列的集合（一个topic中所有的消息队列）
                 * @param msg   发送的消息
                 * @param arg   send方法所传入的业务id
                 * @return 返回指定的消息队列
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    //将arg进行强转，拿到订单id
                    long orderId = (long) arg;
                    /*
                     * 用orderId对mqs的size进行取余，获得指定的消息队列
                     * 由于每个订单的orderId保证是相同的，因此就可以保证相同订单id的订单一定
                     * 会进入到相同的MessageQueue中去
                     * 而且topic中消息队列的数量在生产环境都是预先指定的，一般不会频繁变动
                     * 所以用取余的方法是可行的，如果想要更可靠可以使用哈希方法的队列选择器(SelectMessageQueueByHash())
                     *
                     * 出现取余结果相同的情况：
                     *      这种情况下，对应id的订单消息仍旧是按顺序进入了同一个队列，
                     *      只是中间可能会被其他订单的消息穿插隔开，但是对于消费者来说，对应id的订单
                     *      仍旧是按照其入队的顺序被顺序消费，中间即使还消费了其他订单，但是只要是同一个id的订单
                     *      一定会被顺序消费的
                     *      也就是说不通订单进入到相同的队列没关系，但是这个订单的完整顺序的消息一定要在同一个一个队列中
                     * */
                    long targetIndex = orderId % mqs.size();
                    //将指定的MessageQueue返回
                    return mqs.get((int) targetIndex);
                }
            }, order.getOrderId());
            System.out.println(sendResult);
            Thread.sleep(10000);
        }
    }



}
