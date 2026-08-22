package com.example.portalaluno.funcionario;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.cargo.Cargo;
import com.example.portalaluno.cargo.CargoRepository;
import com.example.portalaluno.funcionario.dto.FuncionarioRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, UserRepository userRepository, CargoRepository cargoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.userRepository = userRepository;
        this.cargoRepository = cargoRepository;
    }

    @Transactional
    public Funcionario cadastrarFuncionario(FuncionarioRequest dadosFuncionario, List<String> nomesDosCargos) {
        if (userRepository.findByEmail(dadosFuncionario.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um funcionario cadastrado com este e-mail.");
        }

        User usuario = new User();
        usuario.setEmail(dadosFuncionario.getEmail());
        usuario.setRole(UserRole.FUNCIONARIO);
        usuario.setPassword(null);

        User usuarioSalvo = userRepository.save(usuario);

        Set<Cargo> cargos = new HashSet<>();
        for (String nomeCargo : nomesDosCargos) {
            Cargo cargo = cargoRepository.findByName(nomeCargo)
                    .orElseThrow(() -> new RuntimeException("Cargo inválido: " + nomeCargo));

            cargos.add(cargo);
        }
        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setUsuario(usuarioSalvo);
        novoFuncionario.setCargos(cargos);
        novoFuncionario.setName(dadosFuncionario.getName());
        novoFuncionario.setCpf(dadosFuncionario.getCpf());
        novoFuncionario.setPhone(dadosFuncionario.getPhone());
        novoFuncionario.setBirthDate(dadosFuncionario.getBirthDate());
        novoFuncionario.setAddress(dadosFuncionario.getAddress());
        novoFuncionario.setCep(dadosFuncionario.getCep());
        novoFuncionario.setCity(dadosFuncionario.getCity());
        novoFuncionario.setState(dadosFuncionario.getState());
        novoFuncionario.setCountry(dadosFuncionario.getCountry());

        return funcionarioRepository.save(novoFuncionario);
    }

    public List<Funcionario> consultarFuncionariosPorNome(String name) {
        List<Funcionario> funcionarios = funcionarioRepository.findByNameContainingIgnoreCase(name);

        if (funcionarios.isEmpty()) {
            throw new RuntimeException("Nenhum funcionario cadastrado com este nome");
        }
        return funcionarios;
    }

}
