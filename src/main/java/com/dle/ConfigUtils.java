package com.dle;

import org.eclipse.microprofile.config.ConfigProvider;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class ConfigUtils {

    public static final String authBaseUrl = ConfigProvider.getConfig().getValue("shop.auth.base", String.class);
    public static final String apiBaseUrl = ConfigProvider.getConfig().getValue("shop.open-api.base", String.class);
    public static final String getTokenUrl = ConfigProvider.getConfig().getValue("shop.auth.getToken", String.class);
    public static final String refreshTokenUrl = ConfigProvider.getConfig().getValue("shop.auth.refreshToken", String.class);
    public static final String authorizationPath = ConfigProvider.getConfig().getValue("shop.authorization.path", String.class);
    public static final String listOrderPath = ConfigProvider.getConfig().getValue("shop.listOrder.path", String.class);
    public static final String orderDetailPath = ConfigProvider.getConfig().getValue("shop.orderDetail.path", String.class);
    public static final String shippingDocumentPath = ConfigProvider.getConfig().getValue("shop.shippingDocument.path", String.class);
    public static final String listProductPath = ConfigProvider.getConfig().getValue("shop.listProduct.path", String.class);
    public static final String productDetailPath = ConfigProvider.getConfig().getValue("shop.productDetail.path", String.class);

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
