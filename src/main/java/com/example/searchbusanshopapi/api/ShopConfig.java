package com.example.searchbusanshopapi.api;

import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
public class ShopConfig {

    //시크릿키
    @Value("${external.service-key}")
    public String SERVICE_KEY;

    //요청api 주소
    private String BASE_URL = "http://apis.data.go.kr/6260000/GoodPriceStoreService/getGoodPriceStore?";

    private URL url = null;
    private HttpURLConnection conn = null;
    private BufferedReader rd = null;
    private StringBuilder urlBuilder;

    @PostConstruct
    public void init(){
        urlBuilder = new StringBuilder(BASE_URL + "serviceKey=" + SERVICE_KEY + "&numOfRows=5&resultType=json");
    }

    /**
     * 읽기전용, 부산가게리스트api에 요청을 보냅니다.
     * @param shopDTO 클라이언트가 보낸 검색옵션
     * @return 응답받은 가게리스트
     * @throws Exception ioexception인걸로 예상, 추후 리팩토링 예정
     */
    public JSONObject request(ShopDTO shopDTO, Integer pageNum) throws Exception{
        String appendUrl = urlBuild(shopDTO, pageNum).toString();
        urlBuilder.append(appendUrl);
        connect(urlBuilder);
        //다음에 재사용시 builder내에 남아있는 파라미터값들을 지웁니다.
        urlBuilder.delete(208, urlBuilder.length());

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        closed();
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject)parser.parse(sb.toString());
        sb = null;
        return obj;
    }

    /**
     * 클라이언트가 선택한 검색옵션으로 api에 요청할 url를 만듭니다.
     * @param shopDTO 클라이언트가 보낸 검색옵션
     * @return 요청보낼 Url
     */
    private StringBuilder urlBuild(ShopDTO shopDTO, Integer pageNum) {
        StringBuilder sb = new StringBuilder();

        if(pageNum >= 1) {
            sb.append("&pageNo="+pageNum);
        }
        //클라이언트가 검색옵션을 추가안한경우 페이지넘버만 추가한후 반환합니다.
        if(shopDTO == null) {
            return sb;
        }
        //옵션추가로직
        if(shopDTO.getShopName() != null){
            sb.append("&sj="+shopDTO.getShopName());
        }
        if(shopDTO.getCategori() != null){
            sb.append("&cnCd="+shopDTO.getCategori());
        }
        if(shopDTO.getLocale() != null){
            sb.append("&localeCd="+shopDTO.getLocale());
        }
        return sb;
    }

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
