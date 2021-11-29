package com.example.searchbusanshopapi.favorite.service;

import com.example.searchbusanshopapi.api.ShopConfig;
import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.favorite.model.FavoriteDTO;
import com.example.searchbusanshopapi.infra.config.ConvertConfig;
import com.example.searchbusanshopapi.infra.exception.DataNotFoundInDatabaseException;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.UserNotFoundException;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ConvertConfig convertConfig;

    public void save(List<FavoriteDTO> favoritesDTO, Long userId) throws Exception{

        List<Favorite> favorites = convertConfig.favoritesJSONToEntity(favoritesDTO);

        User user;
        try {
            user = userRepository.findById(userId).get();
            user.getFavorites().addAll(favorites);
            userRepository.save(user);
        }catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB, userId);
        }catch (Exception e) {
            e.printStackTrace();
        }
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
}
