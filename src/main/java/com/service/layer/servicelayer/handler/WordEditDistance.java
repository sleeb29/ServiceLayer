package com.service.layer.servicelayer.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class WordEditDistance {

    Map<String, Map<String, Integer>> calculatedDistancesMap;

    public WordEditDistance() {
        this.calculatedDistancesMap = new HashMap<>();
    }

    private void addEntryToDistanceMap(String word1, String word2, int distance) {

        if (calculatedDistancesMap.containsKey(word1)) {
            //should not reach this case as we should have already memoized the result
            if (calculatedDistancesMap.get(word1).containsKey(word2)) {
                return;
            } else {
                calculatedDistancesMap.get(word1).put(word2, distance);
            }
        } else {
            HashMap<String, Integer> insideMap = new HashMap<>();
            insideMap.put(word2, distance);
            calculatedDistancesMap.put(word1, new HashMap<>(insideMap));
        }
    }

    public Boolean isFuzzyMatch(String word1, String word2) {

        int word1Length = word1.length();
        int word2Length = word2.length();
        int maxLength = Math.max(word1Length, word2Length);
        int distanceThresholdExclusive = maxLength / 2 + (maxLength % 2);

        if (this.calculatedDistancesMap.containsKey(word1)) {
            Map<String, Integer> insideMap = this.calculatedDistancesMap.get(word1);
            if (insideMap.containsKey(word2)) {
                return insideMap.get(word2) == distanceThresholdExclusive;
            }
        } else if (this.calculatedDistancesMap.containsKey(word2)) {
            Map<String, Integer> insideMap = this.calculatedDistancesMap.get(word2);
            if (insideMap.containsKey(word1)) {
                return insideMap.get(word1) == distanceThresholdExclusive;
            }
        }

        int distance = StringUtils.getLevenshteinDistance(word1, word2);
        addEntryToDistanceMap(word1, word2, distance);
        return distance < distanceThresholdExclusive;

    }

}