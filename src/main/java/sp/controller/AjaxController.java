package sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for asynchronous user interaction
 *
 * @author Paul Kulitski
 */
@Controller
@RequestMapping(value = "/report/ajax")
public class AjaxController {

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody
    String deleteReport(@RequestParam("id") Long id,
            Model model) {
        return "success";
    }

    @RequestMapping(value = "/get-uri")
    public @ResponseBody
    String getUri(@RequestParam("id") Long id,
            Model model) {
        return "url";
    }

    @RequestMapping(value = "update")
    public @ResponseBody
    String update(@RequestBody String report, Model model) {
        return "success";
    }

    @RequestMapping(value = "add")
    public @ResponseBody
    String add(@RequestBody String report, Model model) {
        return "success";
    }
}
