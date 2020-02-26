package com.learn.client;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
class MainController {

    @Value("${casLogoutUrl}")
    private String casLogoutUrl;

    public String getCasLogoutUrl() {
        return casLogoutUrl;
    }

    public void setCasLogoutUrl(String casLogoutUrl) {
        this.casLogoutUrl = casLogoutUrl;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "index";
    }

    @RequestMapping(value = "/protected", method = RequestMethod.GET)
    public String protected1(HttpServletRequest request, Model model) {
        AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
        model.addAttribute("principal", principal);
        return "protected";
    }

}