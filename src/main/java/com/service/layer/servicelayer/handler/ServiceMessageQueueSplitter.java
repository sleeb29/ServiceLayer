package com.service.layer.servicelayer.handler;

import com.buzzilla.webhose.client.WebhosePost;
import com.service.layer.servicelayer.handler.validate.ValidatePostHelper;
import com.service.layer.servicelayer.model.MessageQueueServiceData;
import com.service.layer.servicelayer.model.TopicServiceData;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.*;

public class ServiceMessageQueueSplitter extends AbstractMessageSplitter {

    @Override
    protected Set<MessageQueueServiceData> splitMessage(Message<?> message) {

        TopicServiceData payload = (TopicServiceData) message.getPayload();
        Set<MessageQueueServiceData> messageQueueServiceDataSet = new HashSet<>();



        ValidatePostHelper validatePostHelper = null;
        try {
            validatePostHelper = new ValidatePostHelper();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Map.Entry<String,  HashMap<String, Set<String>>> entry : payload.getServiceData().entrySet()){

            String service = entry.getKey();
            HashMap<String, Set<String>> languageToUserMap = entry.getValue();
            String topic = payload.getTopic();

            for(WebhosePost post : payload.getPosts()){

                //each post will only be written in one language
                //if no user's have that as their preferred language then
                //don't send post.
                String postLanguage = post.language;
                if(!languageToUserMap.containsKey(postLanguage)){
                    continue;
                }

                if(!validatePostHelper.validPostToAdd(post.title, post.language)){
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