package com.justplay1994;

import com.justplay1994.httpClient.nio.JustPlayNIOHttpClient;

/**
 * Created by JustPlay1994 on 2018/8/6.
 * https://github.com/JustPlay1994/daily-log-manager
 */

public class NIOThread implements Runnable{
    public static int count = 0;
    @Override
    public void run() {
        try {
            JustPlayNIOHttpClient client = new JustPlayNIOHttpClient();
            client.connect(
                    Application.url,
                    Application.type,
                    Application.body
            );
            Application.builder1.append(client.stringBuilder);
            minusCount();
//                        System.out.println(i+": "+builder.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    synchronized public static void addCount(){
        count++;
    }

    synchronized public static void minusCount(){
        count--;
    }
}
