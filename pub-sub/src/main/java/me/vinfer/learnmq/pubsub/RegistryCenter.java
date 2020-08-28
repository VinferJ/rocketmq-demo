package me.vinfer.learnmq.pubsub;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 此时的注册中心仍旧是静态的方法，如果使用一个线程对注册中心进行维护
 * 那么就可以支持实时的服务注册以及服务订阅了
 *
 * @author Vinfer
 * @date 2020-08-23  22:49
 **/
public class RegistryCenter {

    private static final ConcurrentHashMap<String,Producer> PRODUCER_TAB = new ConcurrentHashMap<String, Producer>();
    private static final ConcurrentHashMap<String,Consumer> CONSUMER_TAB = new ConcurrentHashMap<String, Consumer>();
    private static final ConcurrentHashMap<String, List<String>> PUBLISH_TAB = new ConcurrentHashMap<String, List<String>>();

    public static void pubReg(String publisherId,Producer publisher){
        PRODUCER_TAB.put(publisherId, publisher);
        PUBLISH_TAB.put(publisherId, new ArrayList<String>());
    }

    public static void subReg(String subscriberId,Consumer subscriber){
        CONSUMER_TAB.put(subscriberId, subscriber);
    }

    public static void subscribe(String subscriberId,List<String> publisherIds){
        for (String publisherId:publisherIds){
            PUBLISH_TAB.get(publisherId).add(subscriberId);
        }
    }

    public static ConcurrentHashMap<String,Producer>getProducerTab(){
        return PRODUCER_TAB;
    }

    public static ConcurrentHashMap<String,Consumer>getConsumerTab(){
        return CONSUMER_TAB;
    }

    public static ConcurrentHashMap<String, List<String>> getPublishTab(){
        return PUBLISH_TAB;
    }



}
