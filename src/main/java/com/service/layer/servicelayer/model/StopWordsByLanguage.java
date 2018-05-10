package com.service.layer.servicelayer.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.service.layer.servicelayer.handler.validate.StopWordsByLanguageDeserializer;

import java.util.Map;
import java.util.Set;

@JsonDeserialize(using = StopWordsByLanguageDeserializer.class)
public class StopWordsByLanguage {

    Map<String, Set<String>> languageToCommonWordsMap;

    public Map<String, Set<String>> getLanguageToCommonWordsMap() {
        return languageToCommonWordsMap;
    }

    public void setLanguageToCommonWordsMap(Map<String, Set<String>> languageToCommonWordsMap) {
        this.languageToCommonWordsMap = languageToCommonWordsMap;
    }

    public Set<String> getCommonWordsByLanguage(String language){

        if(this.languageToCommonWordsMap.containsKey(language)){
            return this.languageToCommonWordsMap.get(language);
        }

        return null;

    }
}
