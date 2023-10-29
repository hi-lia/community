package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //触发事件
    public void fireEvent(Event event){
        // 将事件发布到指定的topic, 把event转成JSON字符串，消费者会转成event得到所有的数据
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
