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
     * @param favoriteIdSet 삭제하고자하는 식별자를 가지고옵니다.
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
     *
     * 요청형태 -> 코드 제일아래에 기입해두었습니다.
     * @param favoritesDTO 가게리스트입니다.
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("/users/{userId}/favorites")
    public ResponseEntity saveFavorite(@RequestBody(required = false) List<FavoriteDTO> favoritesDTO,
                                       @Validated @PathVariable @Positive Long userId) throws Exception{

        try {
            favoriteService.saveFavorite(favoritesDTO, userId);
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
/**
 * saveFavorite() 요청형태 예시
 *
 * json형태로 요청합니다.
 * shopController 메서드들에서 반환하는 형태 그대로 saveFavorite()에 요청합니다.
 * [
 *              {
 *                 "imgFile2": "busan.go.kr/comm/getImage?srvcId=MULGAKIND&upperNo=4688&fileTy=IMG&fileNo=2",
 *                 "cnCd": 676,
 *                 "imgFile1": "busan.go.kr/comm/getImage?srvcId=MULGAKIND&upperNo=4688&fileTy=IMG&fileNo=1",
 *                 "parkngAt": "Y",
 *                 "cn": "기타",
 *                 "locale": "다대동",
 *                 "mNm": "이세진",
 *                 "localeCd": 215,
 *                 "creatDt": "20210726033230",
 *                 "sj": "해변당구장",
 *                 "imgName2": "해변당구장(매장).jpg",
 *                 "tel": "",
 *                 "adres": "(49505) 부산광역시 사하구 다대낙조1길 5, 2층 (다대동)",
 *                 "idx": 4688,
 *                 "imgName1": "해변당구장(간판).jpg",
 *                 "bsnTime": "12:00~24:00",
 *                 "intrcn": "<p>저렴한 가격으로 단골고객을 확보, 편안하게 놀 수 있는 분위기 조성<br></p>"
 *             },
 *             {
 *                 "imgFile2": "busan.go.kr/comm/getImage?srvcId=MULGAKIND&upperNo=4687&fileTy=IMG&fileNo=2",
 *                 "cnCd": 604,
 *                 "imgFile1": "busan.go.kr/comm/getImage?srvcId=MULGAKIND&upperNo=4687&fileTy=IMG&fileNo=1",
 *                 "parkngAt": "N",
 *                 "cn": "목욕",
 *                 "locale": "하단동",
 *                 "mNm": "허현문",
 *                 "localeCd": 226,
 *                 "creatDt": "20210726033042",
 *                 "sj": "만수탕",
 *                 "imgName2": "만수탕(메뉴판).jpg",
 *                 "tel": "051-204-7100",
 *                 "adres": "(49408) 부산광역시 사하구 낙동대로451번안길 12 (하단동)",
 *                 "idx": 4687,
 *                 "imgName1": "만수탕(간판).jpg",
 *                 "bsnTime": "05:00~19:00",
 *                 "intrcn": "<p>소비자(단골손님)의 요구를 충족시키고 부부경영으로 인건비 절약<br></p>"
 *             }
 * ]
 *
 */
