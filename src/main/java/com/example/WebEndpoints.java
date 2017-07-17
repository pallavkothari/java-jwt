package com.example;

import com.example.security.TokenAuthenticationService;
import com.example.security.UserContext;
import com.google.common.net.UrlEscapers;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Web endpoints
 * Created by pallav.kothari on 7/16/17.
 */
@Controller
public class WebEndpoints {

    /**
     * publicly accessible
     */
    @GetMapping("/")
    public String index(HttpServletRequest req, Map<String, Object> model) {
        model.put("userInfo", UserContext.get().getUserInfo());
        model.put("loginLinks", makeLoginLinks(req));
        model.put("logoutLink", makeLogoutLink(req));
        return "index";
    }

    private String makeLogoutLink(HttpServletRequest req) {
        return String.format("%s/logout?success=%s",
                TokenAuthenticationService.LOGIN_SERVICE,
                UrlEscapers.urlPathSegmentEscaper().escape(req.getRequestURL().toString()));
    }

    private List<ProviderLink> makeLoginLinks(HttpServletRequest req) {
        return Arrays.stream(Providers.values())
                .map(p -> {
                    String link = String.format("%s/%s?success=%s&failure=%s",
                            TokenAuthenticationService.LOGIN_SERVICE,
                            p.name(),
                            UrlEscapers.urlPathSegmentEscaper().escape(req.getRequestURL().toString()),
                            UrlEscapers.urlPathSegmentEscaper().escape(req.getRequestURL().toString()));
                    return new ProviderLink(p.name(), link);
                }).collect(Collectors.toList());
    }

    enum Providers {
        facebook, github, google
    }

    @Data @AllArgsConstructor
    private static final class ProviderLink {
        String provider, link;
    }
}
