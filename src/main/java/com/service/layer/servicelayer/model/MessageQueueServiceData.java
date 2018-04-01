package com.service.layer.servicelayer.model;

import com.buzzilla.webhose.client.WebhosePost;

import java.util.List;
import java.util.Set;

public class MessageQueueServiceData {

    String topic;
    Set<String> userIds;
    List<WebhosePost> posts;

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

    public List<WebhosePost> getPosts() {
        return posts;
    }

    public void setPosts(List<WebhosePost> posts) {
        this.posts = posts;
    }
}
