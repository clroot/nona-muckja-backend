package io.nonamuckja.backend.controller;

import io.nonamuckja.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class BaseController {

    private final UserRepository userRepository;

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "index";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/auth")
    @ResponseBody
    public String auth() {
        return "auth";
    }
}
