package com.example.searchbusanshopapi.favorite.controller;

import com.example.searchbusanshopapi.favorite.service.FavoriteService;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import com.example.searchbusanshopapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
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
    public ResponseEntity deleteFavorite(@PathVariable Long usersId,
                                         @RequestBody Set<Long> favoriteIdSet){

        favoriteService.deleteFavorite(usersId, favoriteIdSet);
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
