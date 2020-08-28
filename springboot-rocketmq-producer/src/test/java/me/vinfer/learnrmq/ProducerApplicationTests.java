package me.vinfer.learnrmq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProducerApplication.class})
class ProducerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    RocketMQTemplate template;

    @Test
    public void produceTest(){
        template.convertAndSend("test-message", "hello springboot-rocketmq");
    }

}
