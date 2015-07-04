package com.intimation.demoquiz.utils;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Utils {

    public static final String URL_VIEW_QUESTION = "http://www.intimationsoftware.com/ws/questionbank/viewquestion.php?topicid=1&clientid=1&appid=1";

    public static String getTotalTime(int no) {
        int minutes = no * 1;
        String mins = (minutes % 60) + " Minutes";
        if (minutes > 59)
            mins = String.format("%02d", (minutes / 60)) + " : " + (minutes % (60 * (minutes/60))) + " Minutes";
        return mins;
    }
}
