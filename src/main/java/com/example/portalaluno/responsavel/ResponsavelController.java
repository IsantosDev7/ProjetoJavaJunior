package com.example.portalaluno.responsavel;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/responsavel")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @GetMapping
    public List<Responsavel> listarResponsavel(@RequestParam String name) {
        return responsavelService.consultarReponsavelPorNome(name);
    }

}
