package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Error controller
 *
 * @author Paul Kulitski
 */
@Controller
public class ErrorController {

    @RequestMapping(value = "/error")
    public String error() {
        return "error";
    }

    @RequestMapping(value = "/missing")
    public String missing() {
        return "missing";
    }

    @RequestMapping(value = "/access")
    public String access() {
        return "missing";
    }

    @RequestMapping(value = "/notsupported")
    public String notssupported() {
        return "notsupported";
    }
}
