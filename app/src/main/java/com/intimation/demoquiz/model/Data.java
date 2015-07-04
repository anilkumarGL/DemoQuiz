package com.intimation.demoquiz.model;

import java.util.List;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Data {
    private static Data mInstance;

    private List<Question> mQuestions;
    private Question mQuestion;

    private Data() {}

    public static Data getInstance() {
        if (mInstance == null)
            mInstance = new Data();
        return mInstance;
    }

    public void setQuestions(List<Question> questions) {
        mQuestions = questions;
    }

    public List<Question> getAllQuestions() {
        return mQuestions;
    }

    public Question getCurrentQuestion() {
        return mQuestion;
    }

    public void setCurrentQuestion(Question q) {
        mQuestion = q;
    }
}
