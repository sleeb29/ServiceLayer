package com.service.layer.servicelayer.handler;

import com.buzzilla.webhose.client.WebhoseClient;
import com.buzzilla.webhose.client.WebhosePost;
import com.buzzilla.webhose.client.WebhoseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TopicRequestSender {

    @Value("${webhose.api_key}")
    private String webhoseApiKey;

    @ServiceActivator
    public WebhoseResponse handleTopic(String topic) throws IOException {

        WebhoseClient webhoseClient = new WebhoseClient(webhoseApiKey);
        WebhoseResponse webhoseResponse = webhoseClient.search(topic);

        return webhoseResponse;

    }

}
