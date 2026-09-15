package com.projeto.portalaluno.cargo;

import com.projeto.portalaluno.cargo.dto.CargoPorPessoaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cargo")
public class CargoController {

    private CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public List<CargoPorPessoaResponse> listarCargoPorPessoa(){
        return cargoService.listarCargoPorPessoa();
    }

}
