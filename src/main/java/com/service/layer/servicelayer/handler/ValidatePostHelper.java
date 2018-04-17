package com.service.layer.servicelayer.handler;
import com.buzzilla.webhose.client.WebhosePost;

import java.util.*;

public class ValidatePostHelper {

    HashMap<String, HashSet<String>> languageToVisitedTitlesMap;
    HashMap<String, HashSet<String>> langaugeToAddedTitlesMap;

    public ValidatePostHelper(){
        this.languageToVisitedTitlesMap = new HashMap<>();
        this.langaugeToAddedTitlesMap = new HashMap<>();
    }

    public Boolean addPost(WebhosePost post){

        String title = post.title;
        WordEditDistance wordEditDistance = new WordEditDistance();

        if(langaugeToAddedTitlesMap.containsKey(title)){
            return false;
        }

        if(languageToVisitedTitlesMap.containsKey(title)){
            return false;
        }

        String[] wordsInTitle = title.split("\\W+");
        Arrays.sort(wordsInTitle);
        ArrayList<String> wordsInTitleSet = new ArrayList<>(new TreeSet<>(Arrays.asList(wordsInTitle)));

        for(Map.Entry<String, HashSet<String>> entry : langaugeToAddedTitlesMap.entrySet()){

            if(!entry.getKey().equals(post.language)){
                continue;
            }

            if(!langaugeToAddedTitlesMap.containsKey(post.language)){
                langaugeToAddedTitlesMap.put(post.language, new HashSet<>());
            }

            for(String oldTitle : entry.getValue()) {

                String[] wordsInExistingTitle = oldTitle.split("\\W+");
                Arrays.sort(wordsInExistingTitle);
                ArrayList<String> wordsInExistingTitleSet = new ArrayList<>(new TreeSet<>(Arrays.asList(wordsInExistingTitle)));

                int newTitleIndex = 0;
                int oldTitleIndex = 0;
                int matchCount = 0;

                while (newTitleIndex < wordsInTitleSet.size() && oldTitleIndex < wordsInExistingTitleSet.size()) {

                    String newWord = wordsInTitleSet.get(newTitleIndex).toUpperCase();
                    String oldWord = wordsInExistingTitleSet.get(oldTitleIndex).toUpperCase();
                    Boolean fuzzyMatch = wordEditDistance.isFuzzyMatch(newWord, oldWord);

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

                if(matchCount >= wordsInTitle.length/2 || matchCount >= wordsInExistingTitle.length){
                    System.out.println("OLD TITLE: " + oldTitle + " NEW TITLE: " + title);
                    this.languageToVisitedTitlesMap.get(post.language).add(title);
                    return false;
                }

            }

        }

        if(!langaugeToAddedTitlesMap.containsKey(post.language)){
            langaugeToAddedTitlesMap.put(post.language, new HashSet<>());
        }

        this.langaugeToAddedTitlesMap.get(post.language).add(title);
        this.languageToVisitedTitlesMap.get(post.language).add(title);
        return true;
    }

}
