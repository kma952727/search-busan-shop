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

@Validated
@RestController
@AllArgsConstructor
public class ShopController {

    private final ShopConfig shopConfig;

    @GetMapping("/shops")
    public ResponseEntity searchShops(
            @RequestBody(required = false) ShopDTO shopDTO
    ) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO, 1);

        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @GetMapping("/shops/{pageNum}")
    public ResponseEntity searchShops(
            @PathVariable @Positive Integer pageNum,
            @RequestBody(required = false) ShopDTO shopDTO) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO, pageNum);

        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

}
