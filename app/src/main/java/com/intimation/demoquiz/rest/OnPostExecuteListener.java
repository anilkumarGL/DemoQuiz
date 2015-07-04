package com.intimation.demoquiz.rest;


import com.intimation.demoquiz.model.ModelClassMapper;

/**
 * Created by gorillalogic on 6/12/15.
 */
public interface OnPostExecuteListener {
    public void onSuccess(ModelClassMapper model);
    public void onFailure();
}
