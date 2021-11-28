package com.example.searchbusanshopapi.favorite.service;

import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.infra.exception.DataNotFoundInDatabaseException;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;

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
            throw new DataNotFoundInDatabaseException("db에 없는 식별자 : " + favoriteIdSet.toString(),
                    Errorcode.NO_MATCHING_REQUEST_DATA_IN_DB);
        }

    }
}
