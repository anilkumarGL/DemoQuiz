package com.intimation.demoquiz.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Question {
    // GSON
    public int qid;
    public String question;
    public String ch1;
    public String ch2;
    public String ch3;
    public String ch4;
    public int correctch;

    @SerializedName("types")
    public char type;

    // LOCAL
    private int mSelectedChoice;

    public void setSelectedChoice(int selectedChoice) {
        mSelectedChoice = selectedChoice;
    }

    public boolean isAnswered() {
        return mSelectedChoice != 0;
    }

    public boolean isAnswerCorrect() {
        return mSelectedChoice == correctch;
    }
}
