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
        ValidatePostHelper validatePostHelper = new ValidatePostHelper();

        for(Map.Entry<String,  HashMap<String, Set<String>>> entry : payload.getServiceData().entrySet()){

            String service = entry.getKey();
            HashMap<String, Set<String>> languageToUserMap = entry.getValue();
            String topic = payload.getTopic();

            for(WebhosePost post : payload.getPosts()){

                String postLanguage = post.language;

                if(!languageToUserMap.containsKey(postLanguage)){
                    continue;
                }

                if(!validatePostHelper.addPost(post)){
                    continue;
                }

                MessageQueueServiceData messageQueueServiceData = new MessageQueueServiceData();
                messageQueueServiceData.setService(service);
                messageQueueServiceData.setTopic(topic);
                messageQueueServiceData.setUserIds(languageToUserMap.get(postLanguage));
                messageQueueServiceData.setPost(post);

                messageQueueServiceDataSet.add(messageQueueServiceData);

            }

        }

        return messageQueueServiceDataSet;

    }

}