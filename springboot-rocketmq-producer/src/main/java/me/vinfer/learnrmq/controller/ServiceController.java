package me.vinfer.learnrmq.controller;

import me.vinfer.learnrmq.service.ProducingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Vinfer
 * @date 2020-08-25  02:41
 **/
@RestController
@RequestMapping(value = "/rocketmq/demo")
public class ServiceController {

    private static final int MAX_CONCURRENCY_NUM = 100000;

    @Autowired
    ProducingService producingService;

    @GetMapping(value = "/produce")
    public String produceMsg(){
        try {
            producingService.produce();
            return "已成功发送一条消息到broker";
        }catch (Exception e){
            e.printStackTrace();
            return "消息发送异常！！";
        }
    }

    @GetMapping(value = "/produce/{num}")
    public String butchProduce(@PathVariable String num){
        try {
            int number = Integer.parseInt(num);
            if(number>MAX_CONCURRENCY_NUM){
                return "<h1>已超过系统最大并发数["+MAX_CONCURRENCY_NUM+"]，请将并发数设置在"+MAX_CONCURRENCY_NUM+"以内！</h1>";
            }
            for (int i = 0; i < number; i++) {
                producingService.produce();
            }
            return "已成功批量发送"+num+"条消息到broker";
        }catch (Exception e){
            e.printStackTrace();
            return "消息发送异常！！";
        }
    }

}
