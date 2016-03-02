package org.lucifer.abchat.controller;

import org.lucifer.abchat.domain.MessageLink;
import org.lucifer.abchat.domain.User;
import org.lucifer.abchat.service.MessageLinkService;
import org.lucifer.abchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller

@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private MessageLinkService messageLinkService;

    @RequestMapping(method = RequestMethod.GET)
    public String admin() {
        return "admin";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public
    @ResponseBody
    boolean logIn(User user) {
        return userService.logInAdmin(user);
    }

    @RequestMapping(value = "/mls/{page}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<MessageLink> mlPage(@PathVariable(value = "page")Long page) {
        return messageLinkService.getPage(page);
    }

    @RequestMapping(value = "/mls/count", method = RequestMethod.GET)
    public
    @ResponseBody
    Long mlCount() {
        return messageLinkService.count();
    }

    @RequestMapping(value = "/mls/delete/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    MessageLink mlDelete(@PathVariable(value = "id") Long id) {
        return messageLinkService.delete(id);
    }
}
