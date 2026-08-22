package com.example.portalaluno.cargo;


import com.example.portalaluno.cargo.dto.CargoPorPessoaResponse;
import com.example.portalaluno.funcionario.Funcionario;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class CargoService {

    private CargoRepository cargoRepository;
    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    // LISTANDO MEUS FUNCIONARIOS POR CARGO!!!!VUMBORA
    public List<CargoPorPessoaResponse> listarCargoPorPessoa(){
        List<Cargo> cargos = cargoRepository.findAll();

        return cargos.stream().map(cargo -> {
            List<String> nomes = cargo.getFuncionarios().stream()
                    .map(Funcionario::getName)
                    .toList();

            return new CargoPorPessoaResponse(cargo.getId(), cargo.getName(), nomes);
        }).toList();
    }
}
