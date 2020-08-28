package me.vinfer.learnmq.observer;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Vinfer
 * @date 2020-08-23  17:23
 **/
public class Test {


    public static void main(String[] args) throws InterruptedException {
        String p1 = "p1";
        String p2 = "p2";
        new Thread(new Producer(p1,new ArrayBlockingQueue<Object>(20))).start();
        new Thread(new Producer(p2,new ArrayBlockingQueue<Object>(20))).start();
        List<String> pubList = new ArrayList<String>();
        pubList.add(p1);
        Thread.sleep(100);
        new Thread(new Consumer(pubList),"consumer1").start();
        new Thread(new Consumer(pubList),"consumer2").start();
        pubList.add(p2);
        new Thread(new Consumer(pubList),"consumer3").start();
        new Thread(new Consumer(pubList),"consumer4").start();
        System.out.println("start...");
    }

}
