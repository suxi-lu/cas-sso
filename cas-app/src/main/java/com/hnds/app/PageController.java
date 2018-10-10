package com.hnds.app;

//import com.learn.client.utils.SpringWebUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/page")
public class PageController {

    @GetMapping("/**/*.html")
    public ModelAndView page(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

//        String uri = SpringWebUtils.getRequest().getRequestURI();
        String uri = request.getRequestURI();
        String viewName = uri.replace("/page/", "").replace(".html", "");
        mav.setViewName(viewName);
        return mav;
    }

}
