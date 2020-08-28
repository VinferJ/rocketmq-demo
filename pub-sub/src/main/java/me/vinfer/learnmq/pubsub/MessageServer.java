package me.vinfer.learnmq.pubsub;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过对即时消费的消息进行转发的机制，可以简单实现订阅-发布模式
 * 此时consumer对象不再与producer对象耦合，只要设置其需要订阅的发布者的id即可
 * producer通过消息转发到messageServer，而messageServer在通过发布表来匹配producerId
 * 获取consumerId的集合从何进行消息转发，消息转发后会保存到consumer本地的queue中等待消费
 *
 * 还可以通过定义消息类型来决定，是否进行即刻转发还是延时发布
 *
 * @author Vinfer
 * @date 2020-08-23  22:45
 **/
public class MessageServer implements Runnable{

    static class Message{
        //String type;
        String producerId;
        Object data;
        public Message(String producerId,Object data){
            this.producerId = producerId;
            this.data = data;
        }
    }

    private static final BlockingQueue<Message> TASK_QUEUE = new ArrayBlockingQueue<Message>(100);

    @Override
    public void run() {
        /*
         * 对队列进行监听，当生存者发送了新的消息时，对消息进行消费
         * */
        while (true){
            if(!TASK_QUEUE.isEmpty()){
                Message msg = TASK_QUEUE.poll();
                publishMsg(msg.producerId, msg.data);
            }
        }
    }

    public static void acceptProduce(String producerId,Object msg){
        Message message = new Message(producerId, msg);
        try {
            TASK_QUEUE.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void publishMsg(String producerId, Object msg){
        ConcurrentHashMap<String, Consumer> consumerTab = RegistryCenter.getConsumerTab();
        ConcurrentHashMap<String, List<String>> publishTab = RegistryCenter.getPublishTab();
        //拿到订阅者的id集合
        List<String> subscriberIds = publishTab.get(producerId);
        if(!subscriberIds.isEmpty()){
            /*遍历订阅者，并且将消息转发出去*/
            for (String sub:subscriberIds){
                Consumer consumer = consumerTab.get(sub);
                consumer.receiveMsg(msg);
            }
        }

    }
}
