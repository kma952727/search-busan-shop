package com.example.searchbusanshopapi.favorite.controller;

import com.example.searchbusanshopapi.favorite.model.Favorite;
import com.example.searchbusanshopapi.favorite.model.FavoriteDTO;
import com.example.searchbusanshopapi.favorite.service.FavoriteService;
import com.example.searchbusanshopapi.infra.exception.DataNotFoundInDatabaseException;
import com.example.searchbusanshopapi.infra.exception.DuplicatedKeyException;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.UserNotFoundException;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import com.example.searchbusanshopapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 요청형태 ex) [1, 2, 3, ...]
     * @param usersId
     * @param favoriteIdSet
     * @return
     */
    @DeleteMapping("/users/{usersId}/favorites")
    public ResponseEntity deleteFavorite(@Validated @PathVariable @Positive Long usersId,
                                         @RequestBody(required = false) @Validated @NotNull Set<Long> favoriteIdSet){

        try {
            favoriteService.deleteFavorite(usersId, favoriteIdSet);
        }catch (NoSuchElementException ex){
            throw new UserNotFoundException(Errorcode.USER_NOT_FOUND_IN_DB, usersId);
        }catch (DataNotFoundInDatabaseException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * body에서 json객체를 가져와 favorite객체에 맵핑한 user를 insert합니다.
     * @param
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("/users/{userId}/favorites")
    public ResponseEntity saveFavorite(@RequestBody(required = false) List<FavoriteDTO> favoritesDTO,
                                       @Validated @PathVariable @Positive Long userId) throws Exception{

        try {
            favoriteService.save(favoritesDTO, userId);
        }catch (UserNotFoundException e){
            e.printStackTrace();
            throw e;
        }catch (DuplicatedKeyException e){
            e.printStackTrace();
            throw e;
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
