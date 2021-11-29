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

@Component
public class ConvertConfig {

    public List<Favorite> favoritesJSONToEntity(List<FavoriteDTO> favoritesDTO) throws JsonProcessingException {
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
        JSONObject jsonMap =(JSONObject)jsonObject.get("getGoodPriceStore");
        JSONArray jsonList = (JSONArray)jsonMap.get("item");

        List<Favorite> favorites = new ArrayList<>();
        Favorite favorite = null;

        for(Object element : jsonList) {
            HashMap<String, String> restaurntFiled = (HashMap<String, String>)element;
            favorite = new Favorite.FavoriteBuilder()
                    .setOwner(restaurntFiled.get("mNm"))
                    .setCategori(restaurntFiled.get("cn"))
                    .setLocale(restaurntFiled.get("locale"))
                    .setOwner(restaurntFiled.get("mNm"))
                    .setShopName(restaurntFiled.get("sj"))
                    .setImg(restaurntFiled.get("imgFile1"))
                    .build();
            favorites.add(favorite);
        }
        return favorites;
    }
}
