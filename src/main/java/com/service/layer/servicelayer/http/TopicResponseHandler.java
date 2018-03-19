package com.service.layer.servicelayer.http;

import com.buzzilla.webhose.client.WebhoseClient;
import com.service.layer.servicelayer.model.Topic;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

public class TopicResponseHandler {

    @ServiceActivator
    public String handle(Message<Topic[]> message) {
        Topic[] topics = message.getPayload();

        StringBuilder response = new StringBuilder(System.lineSeparator());
        if (topics.length > 0) {
            response.append("Returned topics:" + System.lineSeparator());
        }
        else {
            response.append("No topics returned" + System.lineSeparator());
        }

        for (Topic topic : topics) {
            response.append(topic.getName()).append(System.lineSeparator());
        }

        return response.toString();
    }

}
