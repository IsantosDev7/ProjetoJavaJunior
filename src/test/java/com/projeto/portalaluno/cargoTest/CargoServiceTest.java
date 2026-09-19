package com.projeto.portalaluno.cargoTest;

import com.projeto.portalaluno.cargo.Cargo;
import com.projeto.portalaluno.cargo.CargoRepository;
import com.projeto.portalaluno.cargo.CargoService;
import com.projeto.portalaluno.cargo.dto.CargoPorPessoaResponse;
import com.projeto.portalaluno.funcionario.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CargoService - listarCargoPorPessoa Tests")
public class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @InjectMocks
    private CargoService cargoService;

    private Cargo cargo1;
    private Cargo cargo2;
    private Cargo emptyRoleCargo;
    private Funcionario funcionario1;
    private Funcionario funcionario2;
    private Funcionario funcionario3;

    @BeforeEach
    void setUp() {
        // Create test funcionarios with realistic data
        funcionario1 = new Funcionario();
        funcionario1.setId(UUID.randomUUID());
        funcionario1.setName("João Silva");
        funcionario1.setCpf("12345678901");
        funcionario1.setPhone("11987654321");
        funcionario1.setBirthDate(LocalDate.of(1990, 5, 15));

        funcionario2 = new Funcionario();
        funcionario2.setId(UUID.randomUUID());
        funcionario2.setName("Maria Santos");
        funcionario2.setCpf("98765432101");
        funcionario2.setPhone("11987654322");
        funcionario2.setBirthDate(LocalDate.of(1988, 3, 20));

        funcionario3 = new Funcionario();
        funcionario3.setId(UUID.randomUUID());
        funcionario3.setName("Pedro Oliveira");
        funcionario3.setCpf("55555555501");
        funcionario3.setPhone("11987654323");
        funcionario3.setBirthDate(LocalDate.of(1992, 7, 10));

        // Create test cargos
        cargo1 = new Cargo();
        cargo1.setId(UUID.randomUUID());
        cargo1.setName("Gerente");
        cargo1.setFuncionarios(Arrays.asList(funcionario1, funcionario2));

        cargo2 = new Cargo();
        cargo2.setId(UUID.randomUUID());
        cargo2.setName("Desenvolvedor");
        cargo2.setFuncionarios(Collections.singletonList(funcionario3));

        emptyRoleCargo = new Cargo();
        emptyRoleCargo.setId(UUID.randomUUID());
        emptyRoleCargo.setName("Desocupado");
        emptyRoleCargo.setFuncionarios(new ArrayList<>());
    }

    @Test
    @DisplayName("Success: List all cargos with associated funcionarios")
    void listarCargoPorPessoa_WithMultipleCargos_ReturnsListOfCargoPorPessoaResponse() {
        // Arrange
        List<Cargo> cargos = Arrays.asList(cargo1, cargo2);
        when(cargoRepository.findAll()).thenReturn(cargos);

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(cargo1.getId(), result.get(0).id());
        assertEquals("Gerente", result.get(0).name());
        assertEquals(2, result.get(0).nomes().size());
        assertTrue(result.get(0).nomes().contains("João Silva"));
        assertTrue(result.get(0).nomes().contains("Maria Santos"));

        assertEquals(cargo2.getId(), result.get(1).id());
        assertEquals("Desenvolvedor", result.get(1).name());
        assertEquals(1, result.get(1).nomes().size());
        assertEquals("Pedro Oliveira", result.get(1).nomes().get(0));

        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Success: Return single cargo with single funcionario")
    void listarCargoPorPessoa_WithSingleCargoAndFuncionario_ReturnsCorrectData() {
        // Arrange
        List<Cargo> cargos = Collections.singletonList(cargo2);
        when(cargoRepository.findAll()).thenReturn(cargos);

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Desenvolvedor", result.get(0).name());
        assertEquals(1, result.get(0).nomes().size());
        assertEquals("Pedro Oliveira", result.get(0).nomes().get(0));
    }

    @Test
    @DisplayName("Edge Case: Empty list when no cargos exist")
    void listarCargoPorPessoa_WithNoCargos_ReturnsEmptyList() {
        // Arrange
        when(cargoRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Edge Case: Cargo with no associated funcionarios")
    void listarCargoPorPessoa_WithEmptyFuncionarios_ReturnsCargoWithEmptyList() {
        // Arrange
        List<Cargo> cargos = Collections.singletonList(emptyRoleCargo);
        when(cargoRepository.findAll()).thenReturn(cargos);

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Desocupado", result.get(0).name());
        assertTrue(result.get(0).nomes().isEmpty());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Edge Case: Multiple cargos including one with empty funcionarios")
    void listarCargoPorPessoa_MixedCargosWithAndWithoutFuncionarios_ReturnsAllCargos() {
        // Arrange
        List<Cargo> cargos = Arrays.asList(cargo1, emptyRoleCargo);
        when(cargoRepository.findAll()).thenReturn(cargos);

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).nomes().size());
        assertEquals(0, result.get(1).nomes().size());
    }

    @Test
    @DisplayName("Boundary: Maintains all funcionario names in correct order")
    void listarCargoPorPessoa_WithMultipleFuncionarios_MaintainsAllNames() {
        // Arrange
        Funcionario f1 = new Funcionario();
        f1.setName("Alice");
        Funcionario f2 = new Funcionario();
        f2.setName("Bob");
        Funcionario f3 = new Funcionario();
        f3.setName("Charlie");

        Cargo cargoMultiple = new Cargo();
        cargoMultiple.setId(UUID.randomUUID());
        cargoMultiple.setName("Administrador");
        cargoMultiple.setFuncionarios(Arrays.asList(f1, f2, f3));

        when(cargoRepository.findAll()).thenReturn(Collections.singletonList(cargoMultiple));

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).nomes().size());
        assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), result.get(0).nomes());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Boundary: Handle special characters in names and cargos")
    void listarCargoPorPessoa_WithSpecialCharactersInNames_ProcessesSuccessfully() {
        // Arrange
        Funcionario specialFunc = new Funcionario();
        specialFunc.setName("José da Silva-Santos");

        Cargo specialCargo = new Cargo();
        specialCargo.setId(UUID.randomUUID());
        specialCargo.setName("Gerente & Diretor");
        specialCargo.setFuncionarios(Collections.singletonList(specialFunc));

        when(cargoRepository.findAll()).thenReturn(Collections.singletonList(specialCargo));

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Gerente & Diretor", result.get(0).name());
        assertEquals("José da Silva-Santos", result.get(0).nomes().get(0));
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Verification: Repository method called exactly once")
    void listarCargoPorPessoa_VerifiesRepositoryCallCount() {
        // Arrange
        when(cargoRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        cargoService.listarCargoPorPessoa();

        // Assert
        verify(cargoRepository, times(1)).findAll();
        verifyNoMoreInteractions(cargoRepository);
    }

    @Test
    @DisplayName("Boundary: Large dataset with many cargos and funcionarios")
    void listarCargoPorPessoa_WithLargeDataset_ProcessesSuccessfully() {
        // Arrange
        List<Cargo> largeCargos = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Cargo c = new Cargo();
            c.setId(UUID.randomUUID());
            c.setName("Cargo_" + i);
            List<Funcionario> funcs = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                Funcionario f = new Funcionario();
                f.setName("Funcionario_" + i + "_" + j);
                funcs.add(f);
            }
            c.setFuncionarios(funcs);
            largeCargos.add(c);
        }

        when(cargoRepository.findAll()).thenReturn(largeCargos);

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(10, result.get(i).nomes().size());
        }
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Data Integrity: Response contains correct cargo ID and name")
    void listarCargoPorPessoa_VerifiesDataIntegrity() {
        // Arrange
        UUID expectedCargoId = UUID.randomUUID();
        String expectedCargoName = "Coordenador";
        Cargo testCargo = new Cargo();
        testCargo.setId(expectedCargoId);
        testCargo.setName(expectedCargoName);
        testCargo.setFuncionarios(Collections.emptyList());

        when(cargoRepository.findAll()).thenReturn(Collections.singletonList(testCargo));

        // Act
        List<CargoPorPessoaResponse> result = cargoService.listarCargoPorPessoa();

        // Assert
        assertEquals(expectedCargoId, result.get(0).id());
        assertEquals(expectedCargoName, result.get(0).name());
    }
}
