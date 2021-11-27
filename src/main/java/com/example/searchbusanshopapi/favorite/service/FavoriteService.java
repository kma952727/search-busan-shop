package com.example.searchbusanshopapi.favorite.service;

import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.Set;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;

    public void deleteFavorite(Long userId, Set<Long> favoriteIdSet){
        User user = userRepository.findById(userId).get();
        Iterator<Favorite> favoriteIterator = user.getFavorites().iterator();

        while(favoriteIterator.hasNext()) {
            Favorite favorite = favoriteIterator.next();
            if(favoriteIdSet.contains(favorite.getId())){
                favoriteIterator.remove();
            }
        }

    }
}
