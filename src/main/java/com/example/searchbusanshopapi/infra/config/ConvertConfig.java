package com.example.searchbusanshopapi.infra.config;

import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.favorite.model.FavoriteDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 비즈니스로직에 필요한 오브젝트간 변환작업을 넣었습니다.
 */
@Component
public class ConvertConfig {

    //json list key
    private final static String GET_GOOD_PRICE_STORE = "getGoodPriceStore";
    //json list key
    private final static String ITEM = "item";
    //target data
    private final static String OWNER = "mNm";
    private final static String CATEGORI = "cn";
    private final static String LOCALE = "locale";
    private final static String SHOP_NAME = "sj";
    private final static String IMG1 = "imgFile1";

    /**
     * DTO -> ENTITY로 변환합니다.
     * @param favoritesDTO 클라이언트에서 요청한 데이터입니다.
     * @return 매핑이 완료된 ENTITY
     */
    public List<Favorite> favoritesDTOToEntity(List<FavoriteDTO> favoritesDTO) {
        List<Favorite> favorites = new ArrayList<>();
        Favorite favorite;
        for(FavoriteDTO element : favoritesDTO){
            favorite = new Favorite.FavoriteBuilder()
                    .setOwner(element.getMNm())
                    .setShopName(element.getSj())
                    .setIntroduce(element.getIntrcn())
                    .setPhoneNumber(element.getTel())
                    .setCategori(element.getCn())
                    .setImg(element.getImgFile1())
                    .setLocale(element.getLocale())
                    .setShopId(element.getIdx())
                    .build();
            favorites.add(favorite);
        }
        return favorites;
    }

    /**
     * jsonObject타입의 가게리스트를 List<Favorite>타입의 자바 오브젝트로 바꿔줍니다. //vo객체로 이동필요
     * @param jsonObject 원래형태
     * @return 자바오브젝트로 변환
     */
    public List<Favorite> jsonRestaurantToFavorites(JSONObject jsonObject){
        JSONObject jsonMap =(JSONObject)jsonObject.get(GET_GOOD_PRICE_STORE);
        JSONArray jsonList = (JSONArray)jsonMap.get(ITEM);

        List<Favorite> favorites = new ArrayList<>();
        Favorite favorite = null;

        for(Object element : jsonList) {
            HashMap<String, String> restaurntFiled = (HashMap<String, String>)element;
            favorite = new Favorite.FavoriteBuilder()
                    .setOwner(restaurntFiled.get(OWNER))
                    .setCategori(restaurntFiled.get(CATEGORI))
                    .setLocale(restaurntFiled.get(LOCALE))
                    .setShopName(restaurntFiled.get(SHOP_NAME))
                    .setImg(restaurntFiled.get(IMG1))
                    .build();
            favorites.add(favorite);
        }
        return favorites;
    }
}
