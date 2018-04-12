package com.service.layer.servicelayer.handler;

import com.buzzilla.webhose.client.WebhoseClient;
import com.buzzilla.webhose.client.WebhosePost;
import com.buzzilla.webhose.client.WebhoseResponse;
import com.service.layer.servicelayer.model.TopicServiceData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class TopicRequestSender {

    @Value("${webhose.api_key}")
    private String webhoseApiKey;

    @ServiceActivator
    public TopicServiceData handleTopic(String topic) throws IOException {

        WebhoseClient webhoseClient = new WebhoseClient(webhoseApiKey);
        WebhoseResponse webhoseResponse = webhoseClient.search(topic);

        TopicServiceData topicServiceData = new TopicServiceData();
        topicServiceData.setTopic(topic);
        topicServiceData.setPosts(webhoseResponse.posts);
        topicServiceData.setTotalResults(webhoseResponse.totalResults);
        topicServiceData.setRequestsLeft(webhoseResponse.requestsLeft);

        return topicServiceData;

    }

}
