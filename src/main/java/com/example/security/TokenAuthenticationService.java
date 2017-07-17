package com.example.security;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * checks for a JWT and lets you through if you're authentic (assumed JWT has been created by `login-with`)
 *
 * Created by pallav.kothari on 7/15/17.
 */
@Slf4j
public class TokenAuthenticationService {
    // env vars
    private static final String SECRET = Optional.ofNullable(System.getenv("COOKIE_SECRET")).orElse("secret0");
    public static final String COOKIE_NAME = Optional.ofNullable(System.getenv("COOKIE_NAME")).orElse("jwt");
    public static final String LOGIN_SERVICE = Optional.ofNullable(System.getenv("LOGIN_SERVICE_URL")).orElse("http://localhost:4001");

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String PHOTO = "photo";
    public static final String PROVIDER = "provider";
    public static final OkHttpClient CLIENT = new OkHttpClient();
    public static final String GRAPH_API = "https://graph.facebook.com/me?access_token=%s";
    public static final Gson GSON = new GsonBuilder().create();

    static Authentication getAuthentication(HttpServletRequest request) {
        String jwt = null;

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            // TODO verify domain, secure, etc
            if (COOKIE_NAME.equals(cookie.getName())) {
                jwt = cookie.getValue();
                break;
            }
        }

        if (jwt == null) {
            return null;
        }

        // parse the token.
        Jwt parsed = null;
        try {
            parsed = Jwts.parser()
                    .setSigningKey(BaseEncoding.base64Url().encode(SECRET.getBytes(Charsets.UTF_8)) )
                    .parseClaimsJws(jwt);
        } catch (ExpiredJwtException | UnsupportedJwtException | SignatureException | MalformedJwtException | IllegalArgumentException e) {
            return null;
        }

        UserContext.UserInfo info = null;
        try {
            Claims body = (Claims) parsed.getBody();
            String accessToken = body.get(ACCESS_TOKEN, String.class);
            Map<String, String> profile = body.get("profile", Map.class);
            String provider = profile.get(PROVIDER);
            info = UserContext.UserInfo.builder()
                    .valid(true)
                    .accessToken(accessToken)
                    .name(profile.get(NAME))
                    .username(profile.get(USERNAME))
                    .photo(profile.get(PHOTO))
                    .provider(provider)
                    .id(getId(provider, accessToken))
                    .build();
        } catch (Exception e) {
            return null;
        }
        UserContext.get().setInfo(info);
        log.info("authenticated {}", info);
        return new UsernamePasswordAuthenticationToken(info.getUsername(), null, emptyList());
    }

    private static String getId(String provider, String accessToken) {
        String id = null;
        if ("facebook".equals(provider)) {

            Request request = new Request.Builder()
                    .url(String.format(GRAPH_API, accessToken))
                    .get()
                    .build();

            try (Response response = CLIENT.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        String json = body.string();
                        JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);
                        id = jsonObject.get("id").getAsString();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.ofNullable(id).orElse("N/A");
    }

}
