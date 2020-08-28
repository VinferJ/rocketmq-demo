package me.vinfer.learnmq.basic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Vinfer
 * @date 2020-08-23  17:14
 **/
public class MessageQueue {

    private static final BlockingQueue<Object> MESSAGE_QUEUE = new LinkedBlockingQueue<Object>();

    public static void putMessage(Object msg){
        try {
            MESSAGE_QUEUE.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Object getMessage(){
        return MESSAGE_QUEUE.poll();
        /*try {
            return MESSAGE_QUEUE.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }*/
    }

    public static boolean isEmpty(){
        return MESSAGE_QUEUE.isEmpty();
    }
}
