package me.vinfer.learnmq.observer;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 引入注册中心的设计
 * 但是这里的设计模式仍旧不是订阅-发布模式，而是观察者模式
 * 因为在consumer中会有附带有List<Producer> 订阅对象集合
 * 这在一定程度上形成了耦合，并没有完全松耦
 *
 * @author Vinfer
 * @date 2020-08-23  18:58
 **/
public class RegistryCenter {

    private static final ConcurrentHashMap<String, Producer> PRODUCER_MAP = new ConcurrentHashMap<String, Producer>();

    public static void registry(String name,Producer producer){
        PRODUCER_MAP.put(name, producer);
    }

    public static ConcurrentHashMap<String,Producer> getRegistry(){
        return PRODUCER_MAP;
    }
}
