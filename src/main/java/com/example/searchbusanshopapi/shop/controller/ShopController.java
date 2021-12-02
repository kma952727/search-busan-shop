package com.example.searchbusanshopapi.shop.controller;

import com.example.searchbusanshopapi.api.ShopConfig;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

/**
 * 클라이언트가 가게리스트를 볼수있는기능을
 * 제공합니다.
 */
@Validated
@RestController
@AllArgsConstructor
public class ShopController {

    private final ShopConfig shopConfig;

    /**
     * api를 통해 가게리스트를 받아와 반환합니다.
     * @param shopDTO 검색옵션입니다.
     * @return
     * @throws Exception
     */
    @GetMapping("/shops")
    public ResponseEntity searchShops(
            @RequestBody(required = false) ShopDTO shopDTO
    ) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO, 1);

        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    /**
     * api를 통해 가게리스트를 받아와 반환합니다.
     * @param shopDTO 검색옵셥니다.
     * @return
     * @throws Exception
     */
    @GetMapping("/shops/{pageNum}")
    public ResponseEntity searchShops(
            @PathVariable @Positive Integer pageNum,
            @RequestBody(required = false) ShopDTO shopDTO) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO, pageNum);

        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

}
