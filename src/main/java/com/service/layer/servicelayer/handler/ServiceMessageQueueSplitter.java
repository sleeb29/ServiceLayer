package com.service.layer.servicelayer.handler;

import com.buzzilla.webhose.client.WebhosePost;
import com.service.layer.servicelayer.model.MessageQueueServiceData;
import com.service.layer.servicelayer.model.TopicServiceData;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;

import java.util.*;

public class ServiceMessageQueueSplitter extends AbstractMessageSplitter {

    @Override
    protected Set<MessageQueueServiceData> splitMessage(Message<?> message) {

        TopicServiceData payload = (TopicServiceData) message.getPayload();

        Set<MessageQueueServiceData> messageQueueServiceDataSet = new HashSet<>();

        for(Map.Entry<String, Set<String>> entry : payload.getServiceData().entrySet()){

            String service = entry.getKey();
            Set<String> userIds = entry.getValue();
            String topic = payload.getTopic();

            for(WebhosePost post : payload.getPosts()){

                MessageQueueServiceData messageQueueServiceData = new MessageQueueServiceData();
                messageQueueServiceData.setService(service);
                messageQueueServiceData.setTopic(topic);
                messageQueueServiceData.setUserIds(userIds);
                messageQueueServiceData.setPost(post);
                messageQueueServiceDataSet.add(messageQueueServiceData);

            }

        }

        return messageQueueServiceDataSet;

    }

}