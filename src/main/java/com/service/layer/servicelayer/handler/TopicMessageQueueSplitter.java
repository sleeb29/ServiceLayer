package com.service.layer.servicelayer.handler;

import com.service.layer.servicelayer.model.MessageQueueServiceData;
import com.service.layer.servicelayer.model.TopicServiceData;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import sun.jvm.hotspot.utilities.MessageQueue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TopicMessageQueueSplitter extends AbstractMessageSplitter {

    @Override
    protected HashMap<String, MessageQueueServiceData> splitMessage(Message<?> message) {

        TopicServiceData payload = (TopicServiceData) message.getPayload();

        HashMap<String, MessageQueueServiceData> topicMessageQueueMap = new HashMap<>();

        for(Map.Entry<String, Set<String>> entry : payload.getServiceData().entrySet()){

            String service = entry.getKey();
            Set<String> userIds = entry.getValue();

            MessageQueueServiceData messageQueueServiceData = new MessageQueueServiceData();
            messageQueueServiceData.setTopic(payload.getTopic());
            messageQueueServiceData.setPosts(payload.getPosts());
            messageQueueServiceData.setUserIds(userIds);

            topicMessageQueueMap.put(service, messageQueueServiceData);

        }

        return topicMessageQueueMap;

    }

}