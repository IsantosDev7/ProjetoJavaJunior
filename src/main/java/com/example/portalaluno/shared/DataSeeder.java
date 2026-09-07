package com.example.portalaluno.shared;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.cargo.Cargo;
import com.example.portalaluno.cargo.CargoRepository;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

@Configuration
public class DataSeeder {

    private final UserRepository userRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CargoRepository cargoRepository;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository,
                      FuncionarioRepository funcionarioRepository,
                      CargoRepository cargoRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.cargoRepository = cargoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {
            boolean existeAdmin = userRepository.findByEmail(adminEmail).isPresent();

            if (!existeAdmin) {
                // 1. Cria o User
                User adminUser = new User();
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setRole(UserRole.SUPER_ADMIN);

                User adminSalvo = userRepository.save(adminUser);

                // 2. Busca o cargo 'Administrador' inserido pelo Flyway
                Cargo cargoAdmin = cargoRepository.findByName("Administrador")
                        .orElseThrow(() -> new RuntimeException("Cargo Administrador não encontrado no banco de dados."));

                // 3. Cria o Funcionario correspondente
                Funcionario adminFuncionario = new Funcionario();
                adminFuncionario.setName("Administrador do Sistema");
                adminFuncionario.setCpf("00000000000");
                adminFuncionario.setPhone("(82) 99999-9999");
                adminFuncionario.setBirthDate(LocalDate.of(1990, 1, 1));
                adminFuncionario.setAddress("Avenida Central, 100");
                adminFuncionario.setCep("57000000");
                adminFuncionario.setCity("Maceió");
                adminFuncionario.setState("AL");
                adminFuncionario.setCountry("Brasil");
                adminFuncionario.setUsuario(adminUser);
                adminFuncionario.setCargos(Set.of(cargoAdmin));

                funcionarioRepository.save(adminFuncionario);

                System.out.println(">>> Super Admin e Funcionário Administrador criados com sucesso via Seed!");
            }
        };
    }
}