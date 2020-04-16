package com.example.ximanaya.Utils;

import java.text.DecimalFormat;

public class PlayConunt {

    public static String PlayConunts(long playCount){
        if (playCount<10000){
            return playCount+"";
        }else if (playCount>=10000&&playCount<100000000){
            playCount=playCount/10000;
            return new DecimalFormat("#").format(playCount)+"万";
        }else {
            playCount=playCount/100000000;
            return new DecimalFormat("#").format(playCount)+"亿";
        }
    }
}
