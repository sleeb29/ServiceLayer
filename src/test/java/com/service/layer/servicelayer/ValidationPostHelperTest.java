package com.service.layer.servicelayer;

import com.service.layer.servicelayer.handler.validate.ValidatePostHelper;
import com.service.layer.servicelayer.handler.validate.WordEditDistanceUtil;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationPostHelperTest {

    @Test
    public void validateOneFuzzyMatchingTitleSameLanguage(){

        String firstTitle = "This title should be added";
        String secondTitle = "This title should not be added";
        String language = "ENGLISH";

        ValidatePostHelper validatePostHelper = new ValidatePostHelper();
        Boolean firstTitleIsValid = validatePostHelper.validPostToAdd(firstTitle, language);
        Boolean secondTitleIsValid = validatePostHelper.validPostToAdd(secondTitle, language);

        assert (firstTitleIsValid);
        assert (!secondTitleIsValid);

    }


    @Test
    public void validateOneFuzzyMatchingTitleDifferentLanguage(){

        String firstTitle = "This title should be added";
        String firstLanguage = "ENGLISH";

        String secondTitle = "This title should also be added";
        String secondLanguage = "SPANISH";

        ValidatePostHelper validatePostHelper = new ValidatePostHelper();
        Boolean firstTitleIsValid = validatePostHelper.validPostToAdd(firstTitle, firstLanguage);
        Boolean secondTitleIsValid = validatePostHelper.validPostToAdd(secondTitle, secondLanguage);

        assert (firstTitleIsValid);
        assert (secondTitleIsValid);

    }

    @Test
    public void validateFuzzyMatchingWithNumbers(){

        String firstTitle = "1 matches if numbers count";
        String secondTitle = "1 matches when numbers matter";
        String language = "ENGLISH";

        ValidatePostHelper validatePostHelper = new ValidatePostHelper();
        Boolean firstTitleIsValid = validatePostHelper.validPostToAdd(firstTitle, language);
        Boolean secondTitleIsValid = validatePostHelper.validPostToAdd(secondTitle, language);

        assert (firstTitleIsValid);
        assert (!secondTitleIsValid);


    }

    @Test
    public void validateFuzzyMatchingWithSpanish(){

        String firstTitle = "Este título debería ser agregado?";
        String secondTitle = "Este título no debe ser agregado";

        String language = "SPANISH";

        ValidatePostHelper validatePostHelper = new ValidatePostHelper();
        Boolean firstTitleIsValid = validatePostHelper.validPostToAdd(firstTitle, language);
        Boolean secondTitleIsValid = validatePostHelper.validPostToAdd(secondTitle, language);

        assert (firstTitleIsValid);
        assert (!secondTitleIsValid);

    }

    @Test
    public void validateWordIsNotFuzzyMatch(){

        String firstWord = "CLEVINGER";
        String secondWord = "CLEVELAND";

        WordEditDistanceUtil wordEditDistanceUtil = new WordEditDistanceUtil();

        assert (!wordEditDistanceUtil.isFuzzyMatch(firstWord, secondWord));

    }

}
