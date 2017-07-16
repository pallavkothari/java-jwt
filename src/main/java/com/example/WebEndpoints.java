package com.example;

import com.example.security.TokenAuthenticationService;
import com.example.security.UserContext;
import com.google.common.net.UrlEscapers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by pallav.kothari on 7/16/17.
 */
@Controller
public class WebEndpoints {

    @GetMapping("/")
    public String index(HttpServletRequest req, Map<String, Object> model) {
        model.put("userInfo", UserContext.get().getUserInfo());
        model.put("fbLoginLink", makeFbLoginLink(req));
        return "index";
    }

    private String makeFbLoginLink(HttpServletRequest req) {
        return String.format("%s/facebook?success=%s",
                TokenAuthenticationService.LOGIN_SERVICE,
                UrlEscapers.urlPathSegmentEscaper().escape(req.getRequestURL().toString()));
    }

    @GetMapping("/login")
    public String login() {
        return "index";
    }
}
