package com.example.security;

import lombok.Builder;
import lombok.Data;

/**
 * Holds thread-local user info; code elsewhere can check the `valid` flag to decide if the user has been auth'd.
 * Ought to be cleaned up in a finally block -- see {@link JWTAuthenticationFilter}
 * Created by pallav.kothari on 7/16/17.
 */
public enum UserContext {
    INSTANCE;

    private ThreadLocal<UserInfo> info = ThreadLocal.withInitial(() -> UserInfo.builder().build());

    public static UserContext get() {
        return INSTANCE;
    }

    public void setInfo(UserInfo info) {
        this.info.set(info);
    }

    public UserInfo getUserInfo() {
        return this.info.get();
    }

    @Data
    @Builder
    public static final class UserInfo {
        private String username, provider, name, photo, accessToken, id;
        private boolean valid;
    }
}
