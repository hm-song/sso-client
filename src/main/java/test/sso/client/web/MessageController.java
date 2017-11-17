package test.sso.client.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MessageController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hellow";
    }
}
