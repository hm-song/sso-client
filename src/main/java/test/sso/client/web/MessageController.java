package test.sso.client.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping("/hello")
    public String hello() {
        return "index";
    }

    @RequestMapping(value = "/denied")
    public String deniedPage() {
        return "invalid_scope";
    }
}