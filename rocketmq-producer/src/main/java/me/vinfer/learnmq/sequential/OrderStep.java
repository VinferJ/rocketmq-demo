package me.vinfer.learnmq.sequential;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟一个订单顺序/步骤
 *
 * @author Vinfer
 * @date 2020-08-27  09:26
 **/
public class OrderStep {

    private long orderId;
    private String desc;

    @Override
    public String toString() {
        return "OrderStep{" +
                "orderId=" + orderId +
                ", desc='" + desc + '\'' +
                '}';
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static List<OrderStep> buildOrder(){
        List<OrderStep> orderList = new ArrayList<OrderStep>();

        /*
        * 生成3订单：
        *   1039L：创建 >> 付款 >> 推送 >> 完成
        *   1065L：创建 >> 付款 >> 完成
        *   7235L：创建 >> 付款 >> 完成
        * 整体的顺序：
        *   1039L-创建 >> 1065L-创建 >> 1039L-付款 >> 7235L-创建 >> 1065L-付款 >>
        *   7235L-付款 >> 1065L-完成 >> 1039L-推送 >> 7235L-完成 >> 1039L-完成
        *
        * */
        OrderStep orderDemo = new OrderStep();
        orderDemo.setOrderId(1039L);
        orderDemo.setDesc("create");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(1065L);
        orderDemo.setDesc("create");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(1039L);
        orderDemo.setDesc("pay");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(7235L);
        orderDemo.setDesc("create");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(1065L);
        orderDemo.setDesc("pay");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(7235L);
        orderDemo.setDesc("pay");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(1065L);
        orderDemo.setDesc("finish");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(1039L);
        orderDemo.setDesc("push");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(7235L);
        orderDemo.setDesc("finish");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(1039L);
        orderDemo.setDesc("finish");
        orderList.add(orderDemo);

        return orderList;
    }

}
