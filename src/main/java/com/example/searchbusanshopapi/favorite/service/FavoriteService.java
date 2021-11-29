package com.example.searchbusanshopapi.favorite.service;

import com.example.searchbusanshopapi.api.ShopConfig;
import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.infra.exception.DataNotFoundInDatabaseException;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.UserNotFoundException;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;
    private final ShopConfig shopConfig;

    public void save(ShopDTO shopDTO, Long userId) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO, 1);
        List<Favorite> favorites = jsonRestaurantToFavorites(jsonObject);
        User user = userRepository.findById(userId).get();
        if(user == null){
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB, userId);
        }
        user.getFavorites().addAll(favorites);
        userRepository.save(user);
    }

    public void deleteFavorite(Long userId, Set<Long> favoriteIdSet){
        User user = null;
        try {
            user = userRepository.findById(userId).get();
        }catch (NoSuchElementException ex) {
            ex.printStackTrace();
            throw ex;
        }
        Iterator<Favorite> favoriteIterator = user.getFavorites().iterator();
        if(!favoriteIterator.hasNext()) {
            throw new DataNotFoundInDatabaseException(
                    "요청값 : " + favoriteIdSet.toString() + " (해당 유저는 즐겨찾기가 0개입니다.)",
                    Errorcode.NO_MATCHING_REQUEST_DATA_IN_DB);
        }

        while(favoriteIterator.hasNext()) {
            Favorite favorite = favoriteIterator.next();
            if(favoriteIdSet.contains(favorite.getId())){
                favoriteIterator.remove();
                favoriteIdSet.remove(favorite.getId());
            }
        }
        if(favoriteIdSet.size() > 0){
            throw new DataNotFoundInDatabaseException("요청 식별자리스트 : " + favoriteIdSet.toString(),
                    Errorcode.NO_MATCHING_REQUEST_DATA_IN_DB);
        }

    }


    /**
     * jsonObject타입의 가게리스트를 List<Favorite>타입의 자바 오브젝트로 바꿔줍니다. //vo객체로 이동필요
     * @param jsonObject 원래형태
     * @return 자바오브젝트로 변환
     */
    private List<Favorite> jsonRestaurantToFavorites(JSONObject jsonObject){
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
