package com.example.searchbusanshopapi.favorite.controller;

import com.example.searchbusanshopapi.favorite.service.FavoriteService;
import com.example.searchbusanshopapi.infra.exception.DataNotFoundInDatabaseException;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.UserNotFoundException;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import com.example.searchbusanshopapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    @GetMapping("/favorites")
    public ResponseEntity<Object> findFavorite(){

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 요청형태 ex) [1, 2, 3, ...]
     * @param usersId
     * @param favoriteIdSet
     * @return
     */
    @DeleteMapping("/users/{usersId}/favorites")
    public ResponseEntity deleteFavorite(@Validated @PathVariable @Positive Long usersId,
                                         @RequestBody(required = false) @Validated @NotNull Set<Long> favoriteIdSet,
                                         BindingResult bindingResult){
        //수정중
        if(bindingResult.hasErrors()){
            System.out.println("에러 "+ bindingResult.getAllErrors());
        }

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
     * @param shopDTO
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("/users/{userId}/favorites")
    public ResponseEntity saveFavorite(@RequestBody ShopDTO shopDTO, @PathVariable Long userId) throws Exception{
        try {
            userService.save(shopDTO, userId);
        }catch (Exception e){
            throw new Exception("넌 저장실패했어, 난 컨트롤러에서 나왔어");
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
