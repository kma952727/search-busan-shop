package com.example.searchbusanshopapi.api;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ShopConfig {

    @Value("${api.base-url}")
    private String BASE_URL;
    @Value("${api.secret-key}")
    private String SECRET_KEY;

    private URL url = null;
    private HttpURLConnection conn = null;
    private BufferedReader rd = null;
    private StringBuilder urlBuilder =new StringBuilder(BASE_URL + "serviceKey=" + SECRET_KEY + "&pageNo=1&numOfRows=5&resultType=json");


    /**
     * db연결기능을 합니다.
     * @param urlBuilder 사용자가 얻고자하는 데이터를 요청하는 url
     */
    private void connect(StringBuilder urlBuilder){
        try {
            url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * db연결을 종료합니다.
     */
    private void closed(){
        try {
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.disconnect();
    }
}
