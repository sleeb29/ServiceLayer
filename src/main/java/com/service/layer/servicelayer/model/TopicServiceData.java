package com.service.layer.servicelayer.model;

import com.buzzilla.webhose.client.WebhosePost;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TopicServiceData {

    String topic;
    List<WebhosePost> posts;

    int totalResults;
    int requestsLeft;

    HashMap<String,  HashMap<String, Set<String>>> serviceData;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<WebhosePost> getPosts() {
        return posts;
    }

    public void setPosts(List<WebhosePost> posts) {
        this.posts = posts;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getRequestsLeft() {
        return requestsLeft;
    }

    public void setRequestsLeft(int requestsLeft) {
        this.requestsLeft = requestsLeft;
    }

    public HashMap<String, HashMap<String, Set<String>>> getServiceData() {
        return serviceData;
    }

    public void setServiceData(HashMap<String, HashMap<String, Set<String>>> serviceData) {
        this.serviceData = serviceData;
    }

}
