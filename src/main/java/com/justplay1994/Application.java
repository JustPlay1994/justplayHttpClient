package com.justplay1994;

import com.justplay1994.httpClient.jdk.MyURLConnection;
import com.justplay1994.httpClient.nio.JustPlayNIOHttpClient;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by JustPlay1994 on 2018/8/5.
 * https://github.com/JustPlay1994/daily-log-manager
 */

public class Application {

    public static String url = "http://localhost:9000/gitblit/repositories";
    public static String type = "GET";
    public static String body = "";

    public static int max = 1000;
    public static StringBuilder builder1 = new StringBuilder();
    public static StringBuilder builder2 = new StringBuilder();
    static int i = 0;

    public static void main(String[] args){
        Thread progress= new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("progress: "+i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        progress.start();
        String nio = testNioClient();
        String connection = testUrlConnection();
        System.out.println(nio);
        System.out.println(connection);
        progress.stop();
    }

    public static String testNioClient() {
        long start = System.currentTimeMillis();
//        System.out.println(start);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                0,
                8,
                100,
                TimeUnit.MICROSECONDS,
                new ArrayBlockingQueue<Runnable>(1)
        );


        for (i = 0; i < max; ++i) {

            NIOThread.addCount();
            executor.execute(new Thread(new NIOThread()));

//            while (executor.getActiveCount()>=8){
            while (NIOThread.count>=8){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
        while (true){
            if(executor.getActiveCount()==0 && executor.getPoolSize()==0){
                executor.shutdown();
                break;
            }
        }

//        MyURLConnection c = new MyURLConnection();
//        c.request(url,type,body);
        return "use nioClient:"+"total spend " + (System.currentTimeMillis() - start) / 1000 + " s"+
                "\n"+
                builder1.length()/1024/1024+"M"+builder1.length()/1024%1024+"KB"+builder1.length()%1024+"B";

    }

    public static String testUrlConnection() {
        long start = System.currentTimeMillis();
//        System.out.println(start);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                0,
                8,
                100,
                TimeUnit.MICROSECONDS,
                new ArrayBlockingQueue<Runnable>(1)
        );


        for (i = 0; i < max; ++i) {

            executor.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MyURLConnection c = new MyURLConnection();
                        String a = c.request(url, type, body);
                        builder2.append(a);
//                        System.out.println(i+": "+builder.length());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));

            while (executor.getActiveCount()>=8){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        while (true){
            if(executor.getActiveCount()==0 && executor.getPoolSize()==0){
                executor.shutdown();
                break;
            }
        }

//        MyURLConnection c = new MyURLConnection();
//        c.request(url,type,body);
        return "use urlConnection:\n"+"total spend " + (System.currentTimeMillis() - start) / 1000 + " s"+
                "\n"+
                builder2.length()/1024/1024+"M"+builder2.length()/1024%1024+"KB"+builder2.length()%1024+"B";
    }
}
