package com.dle.controller;

import com.dle.Repository.ShopRepository;
import com.dle.bean.database.ShopInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

public class BaseController {

    @Inject
    ShopRepository shopRepository;

//    public Response checkShopInfo(String shopCode) {
//        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
//        if (shopInfo.isEmpty()) {
//            return Response.serverError().build();
//        }
//        return
//    }

}
