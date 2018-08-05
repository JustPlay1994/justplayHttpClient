package com.justplay1994.httpClient.jdk;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 缺点，不能读取一部分内容，必须全部换成到内存里，才可以读取。大数据量的时候，容易内存溢出。
 */
public class MyURLConnection {
//    private static final Logger logger = LoggerFactory.getLogger(MyURLConnection.class);
    String url;
    String type;
    String body;
    String result;

    public MyURLConnection(){

    }

    /**
     * 单线程 http客户端
     * @param url
     * @param type
     * @param body
     * @return
     */
    public String request(String url, String type, String body) throws IOException {
        URLConnection urlConnection = null;
        HttpURLConnection httpURLConnection=null;
        try {
//            logger.debug("[ur: " + url + " ,type: " + type + " ,body: ]\n" + body);
//            URL url = new URL(url);
            this.url = url;
            this.type = type;
            this.body = body;

            urlConnection = new URL(url).openConnection();
            httpURLConnection = (HttpURLConnection) urlConnection;

            /*输入默认为false，post需要打开*/
//            if ("POST".equals(type))
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.setRequestMethod(type);


//            httpURLConnection.setConnectTimeout(3000);


            httpURLConnection.connect();

            if ("POST".equals(type)) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(body.getBytes());
            }


            InputStream inputStream = httpURLConnection.getInputStream();


            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            result = builder.toString();
//            logger.debug(result);

        }finally{
            assert httpURLConnection != null;
            httpURLConnection.disconnect();/*关闭连接*/
        }

        return result;

    }
}
