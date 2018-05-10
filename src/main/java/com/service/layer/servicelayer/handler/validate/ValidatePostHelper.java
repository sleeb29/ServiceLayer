package com.service.layer.servicelayer.handler.validate;

import com.service.layer.servicelayer.ServiceLayerApplication;
import com.service.layer.servicelayer.model.StopWordsByLanguage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ValidatePostHelper {

    private static Log logger = LogFactory.getLog(ValidatePostHelper.class);

    private final double MATCH_FACTOR = 0.5;
    private final String WORD_REGULAR_EXPRESSION = "\\P{javaLetterOrDigit}+";
    private final String NUMBER_REGULAR_EXPRESSION = "\\P{javaDigit}+";
    private String stopWordsFilePath = "data/stop_words.json";

    HashMap<String, Set<String>> languageToVisitedTitlesMap;
    HashMap<String, Set<TitleData>> languageToAddedTitlesMap;
    WordEditDistanceUtil wordEditDistanceUtil;
    StopWordsByLanguage stopWordsByLanguage;

    public ValidatePostHelper() throws IOException {
        this.languageToVisitedTitlesMap = new HashMap<>();
        this.languageToAddedTitlesMap = new HashMap<>();
        this.wordEditDistanceUtil = new WordEditDistanceUtil();

       loadStopWordsByLanguage();
    }

    public Boolean validPostToAdd(String title, String language){

        if(languageToAddedTitlesMap.containsKey(language) &&
                languageToAddedTitlesMap.get(language).contains(title)){
            return false;
        }

        TitleData titleData = getLexicographicallySortedWordsInTitle(title, language);
        Boolean fuzzyMatch = hasFuzzyMatch(titleData, title, language);

        if(fuzzyMatch){
            return false;
        }

        updateLanguageMaps(titleData, title, language);

        return true;

    }

    private TitleData getLexicographicallySortedWordsInTitle(String title, String language){

        Set<String> wordsToIgnore = this.stopWordsByLanguage.getCommonWordsByLanguage(language);

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

    public void loadStopWordsByLanguage() throws IOException {

        ClassLoader classLoader = ValidatePostHelper.class.getClassLoader();
        File file = new File(classLoader.getResource(stopWordsFilePath).getFile());
        FileInputStream fis = new FileInputStream(file);
        byte[] fileData = new byte[(int) file.length()];
        fis.read(fileData);
        fis.close();

        String json = new String(fileData, "UTF-8").toUpperCase();

        JsonToObjectTransformer jsonToObjectTransformer = new JsonToObjectTransformer(StopWordsByLanguage.class);
        Message<String> message = new Message<String>() {
            @Override
            public String getPayload() {
                return json;
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        };

        Message<StopWordsByLanguage> stopWordsByLanguageMessage = (Message<StopWordsByLanguage>)jsonToObjectTransformer.transform(message);
        this.stopWordsByLanguage = stopWordsByLanguageMessage.getPayload();

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
