package com.hnds.app;

import com.hnds.client.session.GlobalRestTgtManager;
import com.hnds.client.session.GlobalSessionManager;
import com.hnds.client.utils.RestAuthUtils;
import com.hnds.client.utils.SpringWebUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/14 0014 13:17
 */
@Controller
class MainController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GlobalRestTgtManager globalRestTgtManager;
    @Autowired
    private GlobalSessionManager globalSessionManager;

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

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody String login(String service) {

//        String url = RestAuthUtils.getRedirectUrl(restTemplate, "linxi", "123456", service);
        String url = RestAuthUtils.getRedirectUrl(restTemplate, "hnds", "1234", service);

        return url;
    }

    @RequestMapping(value = "/logout")
    public ModelAndView logout(String service) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");

        HttpSession session = SpringWebUtils.getSession();
        String tgt = globalRestTgtManager.removeTgt(session.getId());
        RestAuthUtils.deleteTgt(restTemplate, tgt);

        globalSessionManager.destroySession(session);

        System.out.println(SpringWebUtils.getSession().getId());

        if (!StringUtils.isEmpty(service)) {
            mav.setViewName("redirect:" + service);
        }

        return mav;
    }

}