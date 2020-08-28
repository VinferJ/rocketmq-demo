package me.vinfer.learnmq.filter.sql;

import me.vinfer.learnmq.filter.PushConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;

/**
 * @author Vinfer
 * @date 2020-08-27  15:52
 **/
public class Consumer extends PushConsumer {


    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = createConsumer();
        try {
            /*
            * 通过在订阅消息时传入一个sql语法的消息选择器就以sql的方式过滤消息了
            * 需要注意的是，如果想要broker支持sql的过滤必须要在启动broker时
            * 在conf/broker.conf中加入以下配置：
            *       enablePropertyFilter=true
            * */
            consumer.subscribe("test-message",MessageSelector.bySql("i>3 AND i<8"));
            consumer.registerMessageListener(new PushConsumer.MessageListener());
            consumer.start();
            System.out.println("consumer started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
