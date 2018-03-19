package com.service.layer.servicelayer.handler;

import com.service.layer.servicelayer.model.Topic;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public class TopicMessageSplitter extends AbstractMessageSplitter {

    @Override
    @ServiceActivator
    public List<String> splitMessage(Message<?> message){

        Topic[] topics = (Topic[])message.getPayload();
        List<String> topicNames = new ArrayList<>();
        for(Topic topic : topics){
            topicNames.add(topic.getName());
        }

        return topicNames;

    }

}
