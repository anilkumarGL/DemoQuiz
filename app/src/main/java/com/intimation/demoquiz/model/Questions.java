package com.intimation.demoquiz.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Questions extends ModelClassMapper {

    public int status;

    @SerializedName("info")
    public ArrayList<Question> questions;
}
