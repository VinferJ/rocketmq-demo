package me.vinfer.learnrmq.service;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;


/**
 * @author Vinfer
 * @date 2020-08-24  22:01
 **/
@Service
public class ProducingService {

    @Autowired
    RocketMQTemplate rocketMqTemplate;

    public void produce() throws Exception {

        /*
        * convertAndSend是springboot对rocketmq消息发送提供的额外支持
        * 该方法传入一个对象作为发送的消息体，在方法内部会将该对象封装成一个
        * Message对象然后再发送出去
        * 而convertAndSend中发送的同步消息，该方法不提供SendResult作为返回值
        * sendResult会在log中进行打印
        * */
        rocketMqTemplate.convertAndSend("test-message", UUID.randomUUID().toString());
        /*
        * 由于rocketMqTemplate是springboot对rocketmq的上层封装，因此使用该模板直接发送消息
        * 是以rocketmq的Message对象作为消息直接发送的，必须要使用springboot自己提供的Message对象
        * 当然其实也不需要这样，因为springboot提供了对象封装的消息的形式发送消息（直接传入一个java对象），更方便
        *
        * 要想直接使用rocketmq提供的消息发送api也很简单，通过该模板拿到producer对象即可
        * */
        Message msg = new Message("test-message", "TAGS-A", "message-a".getBytes());
        DefaultMQProducer producer = rocketMqTemplate.getProducer();
        SendResult send = producer.send(msg);
        System.out.println(send);
        /*
        * 如果将rocketmq的Message对象作为发送对象使用模板进行发送
        * 那么该对象会被转换成一个json对象并且被封装到转换后的rocketmq的Message对象的body中
        * 所有传入的JavaPO都会被换成json对象然后被封装到body中
        * */
        rocketMqTemplate.convertAndSend("test-message", msg);
        /*
        * 需要发送事务消息，也可以使用模板进行发送，相比较rocketmq中发送事务消息的api
        * 模板提供的方法还需要传入生产者组名，因为该方法内部会为事务消息的发送而创建一个事务消息生产者对象
        * 但是第一次调用模板提供的方法来发送事务消息时，要先调用模板的createAndStartTransactionMQProducer方法
        * 来创建并启动一个事务消息生成者对象，否则直接调用会对象空报异常；，该对象创建后会被保存到一个concurrentHashMap中进行维护
        * 当重复调用该创建方法时，只要传入的生成者组名一样，spring并不会为我们创建重复的同一个组的生成者对象，
        * 而是会根据组名在保存事务消息生成者对象的map中判断是否存在相同组名的对象，如果后就会直接返回false，不会进行再创建
        *
        * 方法参数：
        * txProducerGroup：生产者组名
        * TransactionListener：rocketmq的事务监听器
        * executorService：需要传入一个用于执行该事务消息生产者对象生产任务的执行器对象，即需要传入一个线程组或者是线程池
        * RPCHook：远程调用的钩子对象，用于处理远程用发起执行的业务逻辑（doBeforeRequest）以及远程调用响应完成
        *          后（doAfterResponse）的业务处理，而rocketmq提供了改接口的实现类：AclClientRPCHook
        *          （默认提供的RPCHook接口实现类只有这一个）
        *
        * 当然也可以通过手动创建事务消息生产者对象来发送事务消息，但是手动创建的缺点就是
        * 手动创建的对象无法交给spring管理，对该对象的维护不方便
        * （因为不可能每次发完消息就关闭生产者，每次需要发送有再次创建并启动，这样会耗费很大的性能）
        * */
        //rocketMqTemplate.sendMessageInTransaction(null,null,null,null);
    }



}
