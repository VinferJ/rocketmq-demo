package me.vinfer.learnmq.normal;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;

/**
 * 创建一个消费对象对消息进行主动消费（主动向broker拉取消息进行消费，pullConsumer）
 * 主动消费的逻辑/流程比较复杂，与push消费完全不同
 *
 * @author Vinfer
 * @date 2020-08-27  00:20
 **/
public class PullConsumer {


    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-consumer-group";

    private static final String DEFAULT_TOPIC = "test-message";

    public static void main(String[] args) {
        DefaultMQPullConsumer pullConsumer = createConsumer();
        try {

            //TODO  消费流程

            //启动消费者
            pullConsumer.start();
            System.out.println("push-consumer started...");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    static DefaultMQPullConsumer createConsumer(){
        DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer(DEFAULT_GROUP_NAME);
        pullConsumer.setNamesrvAddr(NAME_SRV_ADDR);
        return pullConsumer;
    }


}
