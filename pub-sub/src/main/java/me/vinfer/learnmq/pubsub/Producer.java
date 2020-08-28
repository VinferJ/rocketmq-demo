package me.vinfer.learnmq.pubsub;

/**
 * 发布订阅模式下的生产者不在需要有自己的存储数据的队列
 * 每当生产新的消息就向MessageServer进行发送
 *
 * @author Vinfer
 * @date 2020-08-23  22:44
 **/
public class Producer implements Runnable{

    /**
     * 生产者id，消息的发送以及消费者订阅都需要该id
     * 映射到中间件中就是producer-host的配置
     * */
    private final String producerId;

    public Producer(String producerId){
        this.producerId = producerId;
        RegistryCenter.pubReg(producerId, this);
    }

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            String msg = producerId+"-msg-"+i;
            //消息转发
            MessageServer.acceptProduce(producerId,msg);
            System.out.println(producerId+" produce msg: "+msg);
        }
    }


}
