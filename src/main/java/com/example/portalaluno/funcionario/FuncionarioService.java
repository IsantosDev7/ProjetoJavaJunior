package com.example.portalaluno.funcionario;

import com.example.portalaluno.auth.tokenconvite.TokenConviteService;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.cargo.Cargo;
import com.example.portalaluno.cargo.CargoRepository;
import com.example.portalaluno.funcionario.dto.FuncionarioRequest;
import com.example.portalaluno.funcionario.dto.FuncionarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final TokenConviteService tokenConviteService;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, UserRepository userRepository, CargoRepository cargoRepository, TokenConviteService tokenConviteService) {
        this.funcionarioRepository = funcionarioRepository;
        this.userRepository = userRepository;
        this.cargoRepository = cargoRepository;
        this.tokenConviteService = tokenConviteService;

    }

    private FuncionarioResponse toResponse(Funcionario funcionarioSalvo) {
        return new FuncionarioResponse(
                funcionarioSalvo.getId(),
                funcionarioSalvo.getName(),
                funcionarioSalvo.getUsuario().getEmail(),
                funcionarioSalvo.getCpf(),
                funcionarioSalvo.getPhone(),
                funcionarioSalvo.getBirthDate()
        );
    }

    private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioRequest dadosFuncionario) {
        funcionario.setName(dadosFuncionario.getName());
        funcionario.setCpf(dadosFuncionario.getCpf());
        funcionario.setPhone(dadosFuncionario.getPhone());
        funcionario.setBirthDate(dadosFuncionario.getBirthDate());
        funcionario.setAddress(dadosFuncionario.getAddress());
        funcionario.setCep(dadosFuncionario.getCep());
        funcionario.setCity(dadosFuncionario.getCity());
        funcionario.setState(dadosFuncionario.getState());
        funcionario.setCountry(dadosFuncionario.getCountry());
    }

    @Transactional
    public FuncionarioResponse cadastrarFuncionario(FuncionarioRequest dadosFuncionario, List<String> nomesDosCargos) {
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
        novoFuncionario.setCargos(cargos);
        novoFuncionario.setUsuario(usuarioSalvo);
        atualizarDadosFuncionario(novoFuncionario, dadosFuncionario);


        tokenConviteService.gerarEEnviarConvite(usuarioSalvo);




        Funcionario funcionarioSalvo = funcionarioRepository.save(novoFuncionario);
        return toResponse(funcionarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<FuncionarioResponse> listarFuncionarios(String name, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Order.desc("nome"))
                );

        boolean temNome = name != null;
        Page<Funcionario> page = temNome
                ? funcionarioRepository.findByNameContainingIgnoreCase(name, pageable)
                : funcionarioRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    public void desativarFuncionario(UUID funcionarioId) {
       Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
               .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
       if(funcionario.getFuncionarioStatus().equals(FuncionarioStatus.INATIVO)) {
           throw new RuntimeException("Funcionário já está inativo!");
       }
       funcionario.setFuncionarioStatus(FuncionarioStatus.INATIVO);
       funcionarioRepository.save(funcionario);
    }

}
