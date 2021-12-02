package com.example.searchbusanshopapi.favorite.service;

import com.example.searchbusanshopapi.api.ShopConfig;
import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.favorite.model.FavoriteDTO;
import com.example.searchbusanshopapi.infra.config.ConvertConfig;
import com.example.searchbusanshopapi.infra.exception.DataNotFoundInDatabaseException;
import com.example.searchbusanshopapi.infra.exception.DuplicatedKeyException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;
    private final ConvertConfig convertConfig;

    public void saveFavorite(List<FavoriteDTO> favoritesDTO, Long userId) throws Exception{

        List<Favorite> favorites = convertConfig.favoritesDTOToEntity(favoritesDTO);

        User user;
        try {
            //userId로 User를 찾아온후 받아온 favoirte리스트를 추가한후 저장합니다.
            user = userRepository.findById(userId).get();
            user.getFavorites().addAll(favorites);
            userRepository.save(user);
        }catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB, userId);
        }catch (DataIntegrityViolationException e) {
            //favorite의 기존값과 새로운값의 pk가 겹칠경우 예외를 발생시킵니다.
            e.printStackTrace();
            throw new DuplicatedKeyException(e.getRootCause().getMessage(), Errorcode.DUPLICATED_KEY_IN_DB);
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
            //삭제시도하려는 유저의 favorite크기가 0일경우 던집니다.
            throw new DataNotFoundInDatabaseException(
                    "요청값 : " + favoriteIdSet.toString() + " (해당 유저는 즐겨찾기가 0개입니다.)",
                    Errorcode.NO_MATCHING_REQUEST_DATA_IN_DB);
        }
        //iterator를 통해 favorite의 값들을 삭제합니다.
        while(favoriteIterator.hasNext()) {
            Favorite favorite = favoriteIterator.next();
            if(favoriteIdSet.contains(favorite.getId())){
                favoriteIterator.remove();
                favoriteIdSet.remove(favorite.getId());
            }
        }
        //삭제완료후 요청값으로 받아온 favoriteSet의 값이 남아있을경우 예외를 발생시킵니다.
        //삭제시킬려는 값이 잘못되었습니다.
        if(favoriteIdSet.size() > 0){
            throw new DataNotFoundInDatabaseException("요청 식별자리스트 : " + favoriteIdSet.toString(),
                    Errorcode.NO_MATCHING_REQUEST_DATA_IN_DB);
        }

    }
}
