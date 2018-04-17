package com.service.layer.servicelayer.model;

import com.buzzilla.webhose.client.WebhosePost;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MessageQueueServiceData {

    String service;
    String topic;
    Set<String> userIds;
    WebhosePost post;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<String> userIds) {
        this.userIds = userIds;
    }

    public WebhosePost getPost() {
        return post;
    }

    public void setPost(WebhosePost post) {
        this.post = post;
    }
}