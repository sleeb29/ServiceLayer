package com.service.layer.servicelayer.model;

import java.util.HashMap;
import java.util.Set;

public class ResponsePayloadData {

    HashMap<String, Set<String>> serviceData;

    public HashMap<String, Set<String>> getServiceData() {
        return serviceData;
    }

    public void setServiceData(HashMap<String, Set<String>> serviceData) {
        this.serviceData = serviceData;
    }
}
