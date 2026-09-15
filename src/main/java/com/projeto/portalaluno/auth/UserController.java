package com.projeto.portalaluno.auth;

import com.projeto.portalaluno.auth.dto.LoginRequestDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO dados) {
        // Delega a responsabilidade de validar e logar para o Service!
        return userService.login(dados.email(), dados.senha());
    }
}
