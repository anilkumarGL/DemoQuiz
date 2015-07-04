package com.intimation.demoquiz.model;


import com.intimation.demoquiz.utils.Utils;

import java.util.HashMap;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class ModelClassMapper {
    private static HashMap<String, Class> classHashMap;
    static {
        classHashMap = new HashMap<>();
        classHashMap.put(Utils.URL_VIEW_QUESTION, Questions.class);
    }

    public static Class<ModelClassMapper> getModelClass(String url) {
        return classHashMap.get(url);
    }

}
