package com.example.portalaluno.cargo;

import com.example.portalaluno.cargo.dto.CargoPorPessoaResponse;
import com.example.portalaluno.funcionario.Funcionario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
