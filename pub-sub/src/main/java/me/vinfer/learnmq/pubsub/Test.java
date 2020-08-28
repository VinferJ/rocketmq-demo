package me.vinfer.learnmq.pubsub;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vinfer
 * @date 2020-08-23  23:31
 **/
public class Test {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("starting..");
        Thread.sleep(1000);
        List<String> pubList = new ArrayList<String>();
        pubList.add("p1");
        new Thread(new MessageServer()).start();
        new Thread(new Producer("p1")).start();
        new Thread(new Producer("p2")).start();
        new Thread(new Consumer("c1", pubList)).start();
        new Thread(new Consumer("c2", pubList)).start();
        pubList.add("p2");
        new Thread(new Consumer("c3", pubList)).start();
        new Thread(new Consumer("c4", pubList)).start();
    }


}
