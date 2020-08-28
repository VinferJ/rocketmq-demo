package me.vinfer.learnrmqconsumer.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 消息监听器，topic和消费者组名是必须的，具体的配置可以查看接口api
 * 常用的配置有：
 *      - 消息过滤方式：selectorType：选择器类型，默认为TAG，还可以是SQL92
 *      - 过滤公式/标签：selectorExpression：如果选择了TAG，就配置对应的tag，如果是SQL92，就配置对应的sql语句
 *      - 消费模式：consumeMode：默认是并发消费[CONCURRENTLY]，还有顺序消费[ORDERLY]，
 *                这就是手创消费者对象时，对消息监听器的配置（MessageListener）
 *      - 消息模式：messageModel：默认是集群模式(负载均衡)[CLUSTERING],还可以配置广播模式[BROADCASTING]
 *
 * @author Vinfer
 * @date 2020-08-25  02:59
 **/
@Service
@RocketMQMessageListener(topic = "test-message",consumerGroup = "${rocketmq.consumer.group}")
public class ConsumerListener implements RocketMQListener<String> {


    @Override
    public void onMessage(String s) {
        System.out.println("consuming msg from broker: "+s);
    }
}
