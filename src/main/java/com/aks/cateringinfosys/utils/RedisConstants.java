package com.aks.cateringinfosys.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_RESTAURANT_TTL = 30L;
    public static final String CACHE_HOT_RESTAURANT_KEY = "cache:hotRestaurant:";
    public static final String CACHE_RESTAURANT_KEY = "cache:restaurant:";
    public static final String CACHE_NULL = "isNUll";
    public static final String CACHE_COMMENTLIST_KEY = "cache:commentlist:";
    public static final Long CACHE_COMMENTLIST_TTL = 2L;
    public static final String CACHE_FOOD_KEY = "cache:food:";
    public static final Long CACHE_FOOD_TTL = 10L;
    public static final String CACHE_COUPON_KEY = "cache:coupon:";
    public static final Long CACHE_COUPON_TTL = 2L;
    public static final String CACHE_ORDER_KEY = "cache:order:";
    public static final Long CACHE_ORDER_TTL = 10L;
    public static final String SNAPPED_LOCK = "lock:coupon";
}
