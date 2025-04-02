package com.anuragkanwar.slackmessagebackend.constants;

import org.springframework.http.HttpHeaders;

import java.time.Duration;

public class Constants {
    public static final String WELCOME_MESSAGE = "%s joined to chat";
    public static final String DISCONNECT_MESSAGE = "%s disconnected";
    public static final String USERNAME_AUTH_HEADER = "UsernameAuth";
    public static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;

    public static final String API_CONTEXT_V0 = "/api/v0";


    public static final String LOCAL_DATE_FORMAT = "dd-MM-yyyy";
    public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss";
    public static final String ZONED_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss";

    public static final int PAGE_SIZE = 5;
    public static final long USER_EXPIRES_AFTER_MINUTES = Duration.ofMinutes(30).toMinutes();
    public static final long VALID_START_DATE_AFTER_MINUTES = Duration.ofMinutes(30).toMinutes();

    public static final String JWT_SECRET_KEY = "Ght385FvnjU54fhasjkdjahl798q9dlashdohi0GF4578JNKkhfR466utVjh"; // dev Only
    public static final Long JWT_TOKEN_EXPIRES_AFTER = 604800000L;

    /**
     * Midnight  cron pattern
     */
    public static final String CRON_MIDNIGHT = "0 0 0 * * *";

    /**
     * Runs each Monday at 12am..
     */
    public static final String CRON_MONDAY_MIDAY = "0 0 12 * * 1";

    public static final String AVATAR_URL = "https://api.dicebear.com/6.x/fun-emoji/svg?seed=";
    public static final String AVATAR_URL_QUERIES = "&backgroundColor=059ff2,71cf62,d84be5,d9915b,f6d594,fcbc34,ffd5dc,ffdfbf,d1d4f9,c0aede,b6e3f4&backgroundType=solid,gradientLinear&randomizeIds=true&mouth=cute,lilSmile,smileLol,smileTeeth,tongueOut,wideSmile,kissHeart,plain,shout";

}
