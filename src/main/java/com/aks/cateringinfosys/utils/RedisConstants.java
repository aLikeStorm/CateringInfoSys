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


    public static final String CACHE_COUPON_KEY = "cache:coupon:";
    public static final Long CACHE_COUPON_TTL = 2L;
    public static final String SNAPPED_LOCK = "lock:coupon";

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";
}
