package com.projeto.portalaluno.funcionarioTest;

import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.funcionario.FuncionarioService;
import com.projeto.portalaluno.funcionario.FuncionarioStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FuncionarioService - Delete/Deactivate Tests")
public class FuncionarioServiceDeleteTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private Funcionario ativoFuncionario;
    private Funcionario inativoFuncionario;

    @BeforeEach
    void setUp() {
        // Create active funcionario
        ativoFuncionario = new Funcionario();
        ativoFuncionario.setId(UUID.randomUUID());
        ativoFuncionario.setName("John Active");
        ativoFuncionario.setCpf("12345678901");
        ativoFuncionario.setPhone("11987654321");
        ativoFuncionario.setBirthDate(LocalDate.of(1990, 5, 15));
        ativoFuncionario.setFuncionarioStatus(FuncionarioStatus.ATIVO);

        // Create inactive funcionario
        inativoFuncionario = new Funcionario();
        inativoFuncionario.setId(UUID.randomUUID());
        inativoFuncionario.setName("Jane Inactive");
        inativoFuncionario.setCpf("98765432101");
        inativoFuncionario.setPhone("11987654322");
        inativoFuncionario.setBirthDate(LocalDate.of(1988, 3, 20));
        inativoFuncionario.setFuncionarioStatus(FuncionarioStatus.INATIVO);
    }

    // ============ DESATIVAR FUNCIONARIO TESTS ============

    @Test
    @DisplayName("Success: Deactivate active employee")
    void desativarFuncionario_WithActivoEmployee_SetsFuncionarioStatusToInativo() {
        // Arrange
        when(funcionarioRepository.findById(ativoFuncionario.getId())).thenReturn(Optional.of(ativoFuncionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(ativoFuncionario);

        // Act
        funcionarioService.desativarFuncionario(ativoFuncionario.getId());

        // Assert
        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(captor.capture());
        assertEquals(FuncionarioStatus.INATIVO, captor.getValue().getFuncionarioStatus());
    }

    @Test
    @DisplayName("Success: Verify repository called correctly when deactivating")
    void desativarFuncionario_VerifiesRepositoryInteraction() {
        // Arrange
        when(funcionarioRepository.findById(ativoFuncionario.getId())).thenReturn(Optional.of(ativoFuncionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(ativoFuncionario);

        // Act
        funcionarioService.desativarFuncionario(ativoFuncionario.getId());

        // Assert
        verify(funcionarioRepository, times(1)).findById(ativoFuncionario.getId());
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
        verifyNoMoreInteractions(funcionarioRepository);
    }

    @Test
    @DisplayName("Failure: Throws exception when employee doesn't exist")
    void desativarFuncionario_WithNonExistentEmployee_ThrowsRuntimeException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(funcionarioRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                funcionarioService.desativarFuncionario(nonExistentId));
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Failure: Throws exception when trying to deactivate already inactive employee")
    void desativarFuncionario_WithAlreadyInativoEmployee_ThrowsRuntimeException() {
        // Arrange
        inativoFuncionario.setFuncionarioStatus(FuncionarioStatus.INATIVO);
        when(funcionarioRepository.findById(inativoFuncionario.getId())).thenReturn(Optional.of(inativoFuncionario));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                funcionarioService.desativarFuncionario(inativoFuncionario.getId()));
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Success: Deactivate employee preserves other data")
    void desativarFuncionario_PreservesEmployeeData() {
        // Arrange
        when(funcionarioRepository.findById(ativoFuncionario.getId())).thenReturn(Optional.of(ativoFuncionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(ativoFuncionario);

        String originalName = ativoFuncionario.getName();
        String originalCpf = ativoFuncionario.getCpf();
        String originalPhone = ativoFuncionario.getPhone();
        LocalDate originalBirthDate = ativoFuncionario.getBirthDate();

        // Act
        funcionarioService.desativarFuncionario(ativoFuncionario.getId());

        // Assert
        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(captor.capture());

        Funcionario deactivated = captor.getValue();
        assertEquals(originalName, deactivated.getName());
        assertEquals(originalCpf, deactivated.getCpf());
        assertEquals(originalPhone, deactivated.getPhone());
        assertEquals(originalBirthDate, deactivated.getBirthDate());
    }

    @Test
    @DisplayName("Edge Case: Deactivate employee with special characters in name")
    void desativarFuncionario_WithSpecialCharactersInName_DeactivatesSuccessfully() {
        // Arrange
        Funcionario specialFunc = new Funcionario();
        specialFunc.setId(UUID.randomUUID());
        specialFunc.setName("José da Silva-Santos");
        specialFunc.setPhone("11987654323");
        specialFunc.setBirthDate(LocalDate.of(1985, 1, 1));
        specialFunc.setFuncionarioStatus(FuncionarioStatus.ATIVO);

        when(funcionarioRepository.findById(specialFunc.getId())).thenReturn(Optional.of(specialFunc));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(specialFunc);

        // Act
        funcionarioService.desativarFuncionario(specialFunc.getId());

        // Assert
        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(captor.capture());
        assertEquals(FuncionarioStatus.INATIVO, captor.getValue().getFuncionarioStatus());
        assertEquals("José da Silva-Santos", captor.getValue().getName());
    }

    @Test
    @DisplayName("Boundary: Deactivate multiple employees sequentially")
    void desativarFuncionario_WithMultipleEmployees_DeactivatesAllSuccessfully() {
        // Arrange
        Funcionario func1 = new Funcionario();
        func1.setId(UUID.randomUUID());
        func1.setName("Employee 1");
        func1.setFuncionarioStatus(FuncionarioStatus.ATIVO);

        Funcionario func2 = new Funcionario();
        func2.setId(UUID.randomUUID());
        func2.setName("Employee 2");
        func2.setFuncionarioStatus(FuncionarioStatus.ATIVO);

        Funcionario func3 = new Funcionario();
        func3.setId(UUID.randomUUID());
        func3.setName("Employee 3");
        func3.setFuncionarioStatus(FuncionarioStatus.ATIVO);

        when(funcionarioRepository.findById(func1.getId())).thenReturn(Optional.of(func1));
        when(funcionarioRepository.findById(func2.getId())).thenReturn(Optional.of(func2));
        when(funcionarioRepository.findById(func3.getId())).thenReturn(Optional.of(func3));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(func1);

        // Act
        funcionarioService.desativarFuncionario(func1.getId());
        funcionarioService.desativarFuncionario(func2.getId());
        funcionarioService.desativarFuncionario(func3.getId());

        // Assert
        verify(funcionarioRepository, times(3)).findById(any(UUID.class));
        verify(funcionarioRepository, times(3)).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Edge Case: Deactivate employee right after creation")
    void desativarFuncionario_ImmediatelyAfterCreation_DeactivatesSuccessfully() {
        // Arrange
        Funcionario newFunc = new Funcionario();
        newFunc.setId(UUID.randomUUID());
        newFunc.setName("Newly Created");
        newFunc.setPhone("11999999999");
        newFunc.setBirthDate(LocalDate.of(1995, 6, 1));
        newFunc.setFuncionarioStatus(FuncionarioStatus.ATIVO);

        when(funcionarioRepository.findById(newFunc.getId())).thenReturn(Optional.of(newFunc));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(newFunc);

        // Act
        funcionarioService.desativarFuncionario(newFunc.getId());

        // Assert
        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(captor.capture());
        assertEquals(FuncionarioStatus.INATIVO, captor.getValue().getFuncionarioStatus());
    }

    @Test
    @DisplayName("Data Integrity: Verify deactivated employee ID remains unchanged")
    void desativarFuncionario_VerifiesIdRemains() {
        // Arrange
        UUID originalId = ativoFuncionario.getId();
        when(funcionarioRepository.findById(originalId)).thenReturn(Optional.of(ativoFuncionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(ativoFuncionario);

        // Act
        funcionarioService.desativarFuncionario(originalId);

        // Assert
        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(captor.capture());
        assertEquals(originalId, captor.getValue().getId());
    }

    @Test
    @DisplayName("Verification: Exception message for already inactive employee")
    void desativarFuncionario_VerifiesExceptionMessage() {
        // Arrange
        inativoFuncionario.setFuncionarioStatus(FuncionarioStatus.INATIVO);
        when(funcionarioRepository.findById(inativoFuncionario.getId())).thenReturn(Optional.of(inativoFuncionario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                funcionarioService.desativarFuncionario(inativoFuncionario.getId()));
        
        assertTrue(exception.getMessage().contains("inativo"));
    }

    @Test
    @DisplayName("Boundary: Deactivate employee with minimal data set")
    void desativarFuncionario_WithMinimalData_DeactivatesSuccessfully() {
        // Arrange
        Funcionario minimalFunc = new Funcionario();
        minimalFunc.setId(UUID.randomUUID());
        minimalFunc.setName("Minimal");
        minimalFunc.setFuncionarioStatus(FuncionarioStatus.ATIVO);

        when(funcionarioRepository.findById(minimalFunc.getId())).thenReturn(Optional.of(minimalFunc));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(minimalFunc);

        // Act
        funcionarioService.desativarFuncionario(minimalFunc.getId());

        // Assert
        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(captor.capture());
        assertEquals(FuncionarioStatus.INATIVO, captor.getValue().getFuncionarioStatus());
    }
}
