package com.intimation.demoquiz.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Question {
    // GSON
    public int qNo;
    public String question;
    public List<String> options = new ArrayList<>();
    public int correctch; // options index.
    public String subject;

    @SerializedName("types")
    public char type;

    // LOCAL
    private int mSelectedChoice = -1;

    public void setSelectedChoice(int selectedChoice) {
        mSelectedChoice = selectedChoice;
    }

    public boolean isAnswered() {
        return mSelectedChoice != -1;
    }

    public boolean isAnswerCorrect() {
        return mSelectedChoice == correctch;
    }
}
