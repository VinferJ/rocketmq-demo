package me.vinfer.learnmq.basic;

/**
 * @author Vinfer
 * @date 2020-08-23  22:34
 **/
public class Test {

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Producer()).start();
        Thread.sleep(1000);
        new Thread(new Consumer(),"consumer1").start();
        new Thread(new Consumer(),"consumer2").start();
        new Thread(new Consumer(),"consumer3").start();
        new Thread(new Consumer(),"consumer4").start();
        System.out.println("start...");
    }

}
