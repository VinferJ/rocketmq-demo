package me.vinfer.learnmq.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 创建一个事务消息的生产者对象，生产事务消息
 *      普通消息：只要客户端成功将消息发送到broker中，该消息对消费者都是可见的，消费者可以立即消费或延时消费
 *      事务消息：客户端发送事务消息时，此时发送到broker的消息是个Half Msg（半消息）
 *              这种消息对消费者时不可见的，因此Half Msg提交后不会被消费者马上消费
 *              当Half Msg成功发送后，broker会去给客户单发送接收成功的回调，然后回查producer（客户端）传入的
 *              事务监听器，让客户端去调用该监听器中本地事务执行方法，
 *              该方法执行完后会有两种响应：
 *          1. 正常的事务消息提交：客户端成功处理完本地事务后，向broker返回LocalTransactionState.COMMIT_MESSAGE
 *                             此时事务消息成功提交，消息对消费者可见，消费者可以进行消费
 *          2. 事务消息回滚：客户端处理本地事务失败，向broker返回LocalTransactionState.ROLLBACK_MESSAGE
 *                        此时事务消息会被回滚，broker将删除掉当前的Half Msg
 *          3. 中间状态：当在向broker返回了LocalTransactionState.COMMIT_UNKNOWN，会回调事务监听器中对消息补偿的处理方法，
 *                     在该消息补偿的方法中，最终会应该向broker返回commit或者是rollback
 *
 * @author Vinfer
 * @date 2020-08-28  01:07
 **/
public class TransactionProducer {

    private static final String NAME_SRV_ADDR = "106.53.103.199:9876";

    private static final String DEFAULT_GROUP_NAME = "default-producer-group";

    private static final String DEFAULT_TOPIC = "transaction-message";

    private static final int TOTAL_SEND = 3;

    private static final String[] TAGS = {"tag-a","tag-b","tag-c"};

    static class TransactionMsgListener implements TransactionListener{

        /**
         * 该方法中执行本地事务
         * 当事务消息被提交到broker后，该方法会马上被server端调用并执行
         *
         * @param msg       已发送的事务消息
         * @param arg       设置监听器时传入的对象参数
         * @return          返回执行状态
         */
        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            if(StringUtils.equals(TAGS[0],msg.getTags() )){
                System.out.println("return commit");
                return LocalTransactionState.COMMIT_MESSAGE;
            }else if(StringUtils.equals(TAGS[1],msg.getTags())){
                System.out.println("return rollback");
                //返回rollback后，server端会将该消息直接删除
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }else {
                /*
                * 返回中间窗台，让server端去回调消息补偿的方法
                * */
                System.out.println("return unknown");
                return LocalTransactionState.UNKNOW;
            }
        }

        /**
         * 消息补偿的回调方法
         * 当在executeLocalTransaction方法中向server端返回了UNKNOW时，
         * server端会回调该方法，但是该回调不是在返回UNKNOW后马上发生
         * 而是存在一定的超时时间，当超过该超时时间后，才会发起回调，让客户端执行该方法
         * 因此producer不应该在发送完消息后立刻关闭，而应该等待消息补偿被执行完后再关闭（如果要关闭的话）
         * @param msg       发送的事务消息
         * @return          返回消息状态
         */
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            System.out.println("The msg tags is: "+msg.getTags());
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }

    public static void main(String[] args) {
        /*
        * 发送事务消息需要创建一个事务消息生成者
        * 不再是之前的默认消息生产者
        * */
        TransactionMQProducer producer = createdProducer();

        //设置事务监听器
        producer.setTransactionListener(new TransactionMsgListener());
        try {
            //启动生产者
            producer.start();
            //发送事务消息
            sendTransactionMsg(producer);

            //这里需要让server发起超时回调，因此不关闭producer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static TransactionMQProducer createdProducer(){
        TransactionMQProducer producer = new TransactionMQProducer(DEFAULT_GROUP_NAME);
        producer.setNamesrvAddr(NAME_SRV_ADDR);
        return producer;
    }

    static Message generateMsg(String msgBody,String tags){
        return new Message(DEFAULT_TOPIC, tags, msgBody.getBytes());
    }

    static void sendTransactionMsg(TransactionMQProducer producer) throws MQClientException {
        for (int i = 0; i < TOTAL_SEND; i++) {
            String body = "message-"+i;
            //发送事务消息要使用sendMessageTransaction方法
            TransactionSendResult sendResult = producer.sendMessageInTransaction(generateMsg(body, TAGS[i]), null);
            System.out.println(sendResult);
        }
    }

}
