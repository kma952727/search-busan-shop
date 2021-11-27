package com.example.searchbusanshopapi.shop.controller;

import com.example.searchbusanshopapi.api.ShopConfig;
import com.example.searchbusanshopapi.shop.dto.ShopDTO;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ShopController {

    private final ShopConfig shopConfig;

    @GetMapping("/shops")
    public EntityModel<JSONObject> searchShops(
            @RequestBody ShopDTO shopDTO
    ) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO);
        EntityModel<JSONObject> entityModel =
                new EntityModel<>(jsonObject);
        return entityModel;
    }

    @GetMapping("/shops/{pageNum}")
    public EntityModel<JSONObject> searchShops(
            @PathVariable Integer pageNum,
            @RequestBody ShopDTO shopDTO) throws Exception{
        JSONObject jsonObject = shopConfig.request(shopDTO);
        EntityModel<JSONObject> entityModel =
                new EntityModel<>(jsonObject);
        return entityModel;
    }

}
