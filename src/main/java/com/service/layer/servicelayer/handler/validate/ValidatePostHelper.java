package com.service.layer.servicelayer.handler.validate;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ValidatePostHelper {

    private static Log logger = LogFactory.getLog(ValidatePostHelper.class);
    private final double MATCH_FACTOR = 0.5;
    private final String WORD_REGULAR_EXPRESSION = "\\P{javaLetterOrDigit}+";
    private final String NUMBER_REGULAR_EXPRESSION = "\\P{javaDigit}+";

    HashMap<String, Set<String>> languageToVisitedTitlesMap;
    HashMap<String, Set<TitleData>> languageToAddedTitlesMap;
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

        TitleData titleData = getLexicographicallySortedWordsInTitle(title);
        Boolean fuzzyMatch = hasFuzzyMatch(titleData, title, language);

        if(fuzzyMatch){
            return false;
        }

        updateLanguageMaps(titleData, title, language);

        return true;

    }

    private TitleData getLexicographicallySortedWordsInTitle(String title){

        /*

        TODO - Add support for i18n for common words - Most likely table in UserService
        to send as part of MessageQueueServiceDataPayload

        should not compare common words as this does not tell us the titles match

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

        ArrayList<Integer> numbersInTitle = (ArrayList<Integer>)wordsInTitle.parallelStream()
                .filter(word -> !word.matches(NUMBER_REGULAR_EXPRESSION))
                .map(word -> Integer.parseInt(word))
                .sorted()
                .collect(Collectors.toList());

        return new TitleData(wordsInTitle, numbersInTitle);

    }

    private Boolean hasFuzzyMatch(TitleData titleData, String title, String language){

        for(Map.Entry<String, Set<TitleData>> entry : languageToAddedTitlesMap.entrySet()){

            if(!entry.getKey().equals(language)){
                continue;
            }

            if(!languageToAddedTitlesMap.containsKey(language)){
                languageToAddedTitlesMap.put(language, new HashSet<>());
            }

            Set<TitleData> existingTitles = entry.getValue();

            for(TitleData existingTitleData : existingTitles) {

                Boolean foundMatchOnExistingTitle = existingTitleIsFuzzyMatch(titleData, existingTitleData, title, language);
                if(foundMatchOnExistingTitle){
                    return true;
                }

            }

        }

        return false;

    }

    private Boolean existingTitleIsFuzzyMatch(TitleData titleData, TitleData existingTitleData, String title, String language){

        int wordMatchCound = getMatchCount(titleData.wordList, existingTitleData.wordList, true);
        int intMatchCount = getMatchCount(titleData.numberList, existingTitleData.numberList, false);
        int matchCount = wordMatchCound + intMatchCount;

        Double newWordMatchingPercent = matchCount / Double.parseDouble(Integer.toString(titleData.count));
        Double oldWordMatchingPercent = matchCount / Double.parseDouble(Integer.toString(existingTitleData.count));

        if(newWordMatchingPercent >= this.MATCH_FACTOR || oldWordMatchingPercent >= this.MATCH_FACTOR){
            this.languageToVisitedTitlesMap.get(language).add(title);
            return true;
        }

        return false;

    }

    private int getMatchCount(ArrayList<?> newList, ArrayList<?> oldList, Boolean isStringList){

        int newTitleIndex = 0;
        int oldTitleIndex = 0;
        int matchCount = 0;

        int wordCountNewTitle = newList.size();
        int wordCountExistingTitle = oldList.size();

        while (newTitleIndex < wordCountNewTitle && oldTitleIndex < wordCountExistingTitle) {

            Boolean isMatch;

            Object newObject = newList.get(newTitleIndex);
            Object oldObject = oldList.get(oldTitleIndex);

            String newWord = String.valueOf(newObject).toUpperCase();
            String oldWord = String.valueOf(oldObject).toUpperCase();

            Boolean newBeforeOld;

            if(isStringList) {
                isMatch = this.wordEditDistanceUtil.isFuzzyMatch(newWord, oldWord);
                newBeforeOld = newWord.compareTo(oldWord) < 0;
            } else {
                Integer newInt = Integer.parseInt(newWord);
                Integer oldInt = Integer.parseInt(oldWord);
                isMatch = newInt.equals(oldInt);
                newBeforeOld = newInt < oldInt;
            }

            if (isMatch) {
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

    private void updateLanguageMaps(TitleData titleData, String title, String language){

        if(!languageToAddedTitlesMap.containsKey(language)){
            languageToAddedTitlesMap.put(language, new HashSet<>());
        }

        if(!languageToVisitedTitlesMap.containsKey(language)){
            languageToVisitedTitlesMap.put(language, new HashSet<>());
        }

        this.languageToAddedTitlesMap.get(language).add(titleData);
        this.languageToVisitedTitlesMap.get(language).add(title);

    }

    private class TitleData {

        ArrayList<String> wordList;
        ArrayList<Integer> numberList;

        int count;

        public TitleData(ArrayList<String> wordList, ArrayList<Integer> numberList){
            this.wordList = wordList;
            this.numberList = numberList;

            count = this.wordList.size() + this.numberList.size();
        }

    }

}
