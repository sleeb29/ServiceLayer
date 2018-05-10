package com.service.layer.servicelayer.handler.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.service.layer.servicelayer.model.MessageQueueServiceData;
import com.service.layer.servicelayer.model.StopWordsByLanguage;

import java.io.IOException;
import java.util.*;

public class StopWordsByLanguageDeserializer extends StdDeserializer<StopWordsByLanguage> {

    public StopWordsByLanguageDeserializer() {
        this(null);
    }
    public StopWordsByLanguageDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public StopWordsByLanguage deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {

        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        StopWordsByLanguage stopWordsByLanguage = new StopWordsByLanguage();

        Map<String, Set<String>> languageToCommonWordsMap = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> rootFields = root.fields();
        while(rootFields.hasNext()){

            Map.Entry<String, JsonNode> parent = rootFields.next();
            String language = parent.getKey();
            JsonNode commonWordsNode = parent.getValue();
            Set<String> commonWords = new HashSet<>();

            int commonWordsSize = commonWordsNode.size();
            int i = 0;
            while(i < commonWordsSize){
                commonWords.add(formatTreeNodeToString(commonWordsNode.get(i)));
                i++;
            }

            languageToCommonWordsMap.put(language, commonWords);

        }

        stopWordsByLanguage.setLanguageToCommonWordsMap(languageToCommonWordsMap);

        return stopWordsByLanguage;

    }

    private String formatTreeNodeToString(TreeNode sourceNode) {
        return sourceNode.toString().replace("\"", "");
    }

}
