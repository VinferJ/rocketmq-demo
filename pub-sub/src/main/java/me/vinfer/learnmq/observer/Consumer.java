package me.vinfer.learnmq.observer;

import me.vinfer.learnmq.basic.MessageQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者线程，对应消息的订阅者
 * @author Vinfer
 * @date 2020-08-23  17:10
 **/
public class Consumer implements Runnable{

    private final List<String> subPublisherIds;

    private final List<Producer> subPublishers;

    public Consumer(List<String> subPublisherIds){
        this.subPublisherIds = subPublisherIds;
        subPublishers = new ArrayList<Producer>();
        subscribe();
    }

    @Override
    public void run() {
        listenToConsume();
    }

    private void subscribe(){
        ConcurrentHashMap<String,Producer> map = RegistryCenter.getRegistry();
        for(String name :subPublisherIds){
            if(map.containsKey(name)){
                subPublishers.add(map.get(name));
            }
        }
    }

    public void listenToConsume(){
        while (true){
            /*
            * 由于getMessage方法所使用的是阻塞队列中的take方法
            * take方法让头元素出队时，如果队列是空的，那么会进入线程阻塞等待状态（RUNNABLE）
            * 只有当队列不为空，即头元素是available状态才会将元素取出并返回
            * 如果队列一致为空，那么线程就会一直处于等待状态，因此take是无限等待的获取
            * 因此不需要进行额外的队列查询是否为空
            * 如果getMessage使用的是超时获取poll或者非超时poll方法，那么就需要先查询是否为空了
            * 因为poll方法会在队列为空时进入超时等待，如果等待时间内，头元素为available状态
            * 那么可以返回，如果超出了等待时间，会返回null
            * 非超时poll在队空时会直接返回null
            *
            * 空消费问题：
            *   当getMessage使poll（超时及非超时）方法+空判断时，会出现某个消费消费到空消息(null)的现象
            *   原因是因为对队列的空判断的布尔值的获取时非线程安全的
            *   假设有A B C 3个线程
            *   当队列中只剩最后一个消息时，如果A B C同时进入了队列的空判断，那么就会进入到if里面
            *   由于阻塞队列是线程安全的，因此只能有一个线程先拿到锁，假设此时A线程已经拿到锁（阻塞队列的锁）可以消费
            *   当A消费完后释放锁让B C 继续消费，那么B C 消费到的可能都是null，因此最后一个消息已经被消费了
            *   如果队列还没来得及产生新的消息，那么消费到的就是null了
            * 解决方法：获取消息后加一个对消息的空判断，如果是null，那么进入自旋获取，直到获取到的不是null
            *
            * */
            /*if(canConsume()){
                Object message = MessageQueue.getMessage();
                if(message == null){
                    continue;
                }
                System.out.println(Thread.currentThread().getName()+" consume: "+message);
            }*/

            for(Producer producer:subPublishers){
                Object message = producer.getMessage();
                if(message==null){
                    continue;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+" consume: "+message);
            }
        }
    }

    public boolean canConsume(){
        return !MessageQueue.isEmpty();
    }

}
