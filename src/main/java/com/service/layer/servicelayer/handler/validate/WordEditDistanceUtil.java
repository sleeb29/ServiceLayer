package com.service.layer.servicelayer.handler.validate;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class WordEditDistanceUtil {

    private final double WORD_MATCH_FACTOR = 0.2;
    Map<String, Map<String, Boolean>> calculatedMatchesMap;

    public WordEditDistanceUtil() {
        this.calculatedMatchesMap = new HashMap<>();
    }

    private void addEntryToDistanceMap(String word1, String word2, Boolean isMatch) {

        if (calculatedMatchesMap.containsKey(word1)) {

            calculatedMatchesMap.get(word1).put(word2, isMatch);

        } else {

            HashMap<String, Boolean> insideMap = new HashMap<>();
            insideMap.put(word2, isMatch);
            calculatedMatchesMap.put(word1, new HashMap<>(insideMap));

        }
    }

    public Boolean isFuzzyMatch(String word1, String word2) {

        int word1Length = word1.length();
        int word2Length = word2.length();
        double maxLength = Math.max(word1Length, word2Length);

        if (this.calculatedMatchesMap.containsKey(word1)) {

            Map<String, Boolean> insideMap = this.calculatedMatchesMap.get(word1);
            if (insideMap.containsKey(word2)) {
                return insideMap.get(word2);
            }

        } else if (this.calculatedMatchesMap.containsKey(word2)) {

            Map<String, Boolean> insideMap = this.calculatedMatchesMap.get(word2);
            if (insideMap.containsKey(word1)) {
                return insideMap.get(word1);
            }

        }

        double distance = StringUtils.getLevenshteinDistance(word1, word2);

        Boolean isMatch = distance/maxLength < WORD_MATCH_FACTOR;
        addEntryToDistanceMap(word1, word2, isMatch);
        return isMatch;

    }

}