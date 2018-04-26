package com.service.layer.servicelayer.handler.validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ValidatePostHelper {

    private static Log logger = LogFactory.getLog(ValidatePostHelper.class);
    private final double MATCH_FACTOR = 0.5;
    private final String WORD_REGULAR_EXPRESSION = "\\P{javaLetterOrDigit}+";

    HashMap<String, Set<String>> languageToVisitedTitlesMap;
    HashMap<String, Set<ArrayList<String>>> languageToAddedTitlesMap;
    WordEditDistanceUtil wordEditDistanceUtil;

    public ValidatePostHelper(){
        this.languageToVisitedTitlesMap = new HashMap<>();
        this.languageToAddedTitlesMap = new HashMap<>();
        this.wordEditDistanceUtil = new WordEditDistanceUtil();
    }

    public Boolean validPostToAdd(String title, String language){

        if(languageToAddedTitlesMap.containsKey(language) &&
                languageToAddedTitlesMap.get(language).contains(title)){
            return false;
        }

        ArrayList<String> wordsInTitle = getLexicographicallySortedWordsInTitle(title);
        Boolean fuzzyMatch = hasFuzzyMatch(wordsInTitle, title, language);

        if(fuzzyMatch){
            return false;
        }

        updateLanguageMaps(wordsInTitle, title, language);

        return true;

    }

    private ArrayList<String> getLexicographicallySortedWordsInTitle(String title){

        /*

        TODO - Add support for i18n for common words - Most likely table in UserService
        to send as part of MessageQueueServiceDataPayload

        should not compare common words as this does not tell us the titles match
        perhaps consider also treating numbers as needing to be exact matches so
        storing words and number sets separately for separate comparison

        Ideally a service that provides common words for each language would exist

        */

        Set<String> wordsToIgnore = new HashSet<>();

        wordsToIgnore.add("JUST");
        wordsToIgnore.add("IS");
        wordsToIgnore.add("THE");
        wordsToIgnore.add("A");
        wordsToIgnore.add("FROM");
        wordsToIgnore.add("TO");
        wordsToIgnore.add("TOO");
        wordsToIgnore.add("WITH");
        wordsToIgnore.add("WAY");
        wordsToIgnore.add("IT");
        wordsToIgnore.add("IT'S");
        wordsToIgnore.add("I");
        wordsToIgnore.add("I'M");
        wordsToIgnore.add("AM");
        wordsToIgnore.add("THAT");
        wordsToIgnore.add("THIS");
        wordsToIgnore.add("THUS");
        wordsToIgnore.add("SHOULD");
        wordsToIgnore.add("NOT");
        wordsToIgnore.add("ISN'T");
        wordsToIgnore.add("WON'T");
        wordsToIgnore.add("WE");
        wordsToIgnore.add("SHE");
        wordsToIgnore.add("HE");
        wordsToIgnore.add("HIM");
        wordsToIgnore.add("HIS");
        wordsToIgnore.add("HER");
        wordsToIgnore.add("HERS");
        wordsToIgnore.add("AND");
        wordsToIgnore.add("AN");
        wordsToIgnore.add("AT");

        String[] wordsInTitleArray = title.split(WORD_REGULAR_EXPRESSION);
        ArrayList<String> wordsInTitle =  (ArrayList<String>)Arrays.stream(wordsInTitleArray)
                                                .distinct()
                                                .map(word -> word.toUpperCase())
                                                .filter(word -> !wordsToIgnore.contains(word))
                                                .sorted()
                                                .collect(Collectors.toList());

        return wordsInTitle;

    }

    private Boolean hasFuzzyMatch(ArrayList<String> wordsInTitle, String title, String language){

        for(Map.Entry<String, Set<ArrayList<String>>> entry : languageToAddedTitlesMap.entrySet()){

            if(!entry.getKey().equals(language)){
                continue;
            }

            if(!languageToAddedTitlesMap.containsKey(language)){
                languageToAddedTitlesMap.put(language, new HashSet<>());
            }

            Set<ArrayList<String>> existingTitles = entry.getValue();

            for(ArrayList<String> wordsInExistingTitle : existingTitles) {

                Boolean foundMatchOnExistingTitle = existingTitleIsFuzzyMatch(wordsInTitle, wordsInExistingTitle, title, language);
                if(foundMatchOnExistingTitle){
                    return true;
                }

            }

        }

        return false;

    }

    private Boolean existingTitleIsFuzzyMatch(ArrayList<String> wordsInTitle, ArrayList<String> wordsInExistingTitle, String title, String language){

        int matchCount = getMatchCount(wordsInTitle, wordsInExistingTitle);

        Double newWordMatchingPercent = matchCount / Double.parseDouble(Integer.toString(wordsInTitle.size()));
        Double oldWordMatchingPercent = matchCount / Double.parseDouble(Integer.toString(wordsInExistingTitle.size()));

        if(newWordMatchingPercent >= this.MATCH_FACTOR || oldWordMatchingPercent >= this.MATCH_FACTOR){
            this.languageToVisitedTitlesMap.get(language).add(title);
            return true;
        }

        return false;

    }

    private int getMatchCount(ArrayList<String> wordsInTitle, ArrayList<String> wordsInExistingTitle){

        int newTitleIndex = 0;
        int oldTitleIndex = 0;
        int matchCount = 0;

        int wordCountNewTitle = wordsInTitle.size();
        int wordCountExistingTitle = wordsInExistingTitle.size();

        while (newTitleIndex < wordCountNewTitle && oldTitleIndex < wordCountExistingTitle) {

            String newWord = wordsInTitle.get(newTitleIndex).toUpperCase();
            String oldWord = wordsInExistingTitle.get(oldTitleIndex).toUpperCase();
            Boolean fuzzyMatch = this.wordEditDistanceUtil.isFuzzyMatch(newWord, oldWord);

            Boolean newBeforeOld = newWord.compareTo(oldWord) < 0;

            if (fuzzyMatch) {
                matchCount++;
                newTitleIndex++;
                oldTitleIndex++;
            } else if (newBeforeOld) {
                newTitleIndex++;
            } else {
                oldTitleIndex++;
            }

        }

        return matchCount;

    }

    private void updateLanguageMaps(ArrayList<String> wordsInTitle, String title, String language){

        if(!languageToAddedTitlesMap.containsKey(language)){
            languageToAddedTitlesMap.put(language, new HashSet<>());
        }

        if(!languageToVisitedTitlesMap.containsKey(language)){
            languageToVisitedTitlesMap.put(language, new HashSet<>());
        }

        this.languageToAddedTitlesMap.get(language).add(wordsInTitle);
        this.languageToVisitedTitlesMap.get(language).add(title);

    }

}
