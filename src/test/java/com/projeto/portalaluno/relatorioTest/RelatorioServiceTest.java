package com.projeto.portalaluno.relatorioTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;
import com.projeto.portalaluno.aula.Aula;
import com.projeto.portalaluno.aula.AulaRepository;
import com.projeto.portalaluno.aula.roles.Modalidade;
import com.projeto.portalaluno.aula.roles.StatusAula;
import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRole;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.relatorio.Relatorio;
import com.projeto.portalaluno.relatorio.RelatorioRepository;
import com.projeto.portalaluno.relatorio.RelatorioService;
import com.projeto.portalaluno.relatorio.StatusRelatorio;
import com.projeto.portalaluno.relatorio.dto.RelatorioRequest;
import com.projeto.portalaluno.relatorio.dto.RelatorioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RelatorioService - Comprehensive Tests")
public class RelatorioServiceTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private RelatorioRepository relatorioRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private User professorUser;
    private User studentUser;
    private Funcionario professor;
    private Aluno aluno;
    private Aula aula;
    private Relatorio relatorio;
    private RelatorioRequest relatorioRequest;

    @BeforeEach
    void setUp() {
        // Create test users
        professorUser = new User();
        professorUser.setId(UUID.randomUUID());
        professorUser.setEmail("professor@test.com");
        professorUser.setRole(UserRole.FUNCIONARIO);

        studentUser = new User();
        studentUser.setId(UUID.randomUUID());
        studentUser.setEmail("student@test.com");
        studentUser.setRole(UserRole.ALUNO);

        // Create test professor
        professor = new Funcionario();
        professor.setId(UUID.randomUUID());
        professor.setName("Professor João");
        professor.setUsuario(professorUser);
        professor.setPhone("11987654321");
        professor.setBirthDate(LocalDate.of(1985, 5, 15));

        // Create test student
        aluno = new Aluno();
        aluno.setId(UUID.randomUUID());
        aluno.setName("Aluno Maria");
        aluno.setUsuario(studentUser);
        aluno.setPhone("11987654322");
        aluno.setBirthDate(LocalDate.of(2000, 3, 20));

        // Create test aula
        aula = new Aula();
        aula.setId(UUID.randomUUID());
        aula.setTitulo("Matemática");
        aula.setModalidade(Modalidade.PRESENCIAL);
        aula.setDuracaoAula(60);
        aula.setDataHoraAula(LocalDateTime.of(2024, 6, 15, 14, 0));
        aula.setStatusAula(StatusAula.PREVISTA);
        aula.setProfessor(professor);
        aula.setAluno(aluno);

        // Create test relatorio
        relatorio = new Relatorio();
        relatorio.setId(UUID.randomUUID());
        relatorio.setAula(aula);
        relatorio.setProfessor(professor);
        relatorio.setTexto("Aula foi muito produtiva");
        relatorio.setLido(false);
        relatorio.setStatus(StatusRelatorio.ATIVO);
        relatorio.setCreatedAt(LocalDateTime.now());

        // Create test request
        relatorioRequest = new RelatorioRequest(
                aula.getId(),
                "Aluno participou ativamente da aula",
                null
        );
    }

    // ============ CRIAR RELATORIO TESTS ============

    @Test
    @DisplayName("Success: Create report with logged-in professor")
    void criarRelatorio_WithLoggedInProfessor_ReturnsRelatorioResponse() {
        // Arrange
        when(funcionarioRepository.findByUsuario(professorUser)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(relatorioRepository.existsByAulaId(aula.getId())).thenReturn(false);
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        // Act
        RelatorioResponse result = relatorioService.criarRelatorio(professorUser, relatorioRequest);

        // Assert
        assertNotNull(result);
        assertEquals(aula.getId(), result.aulaId());
        assertEquals(professor.getId(), result.professorId());
        verify(relatorioRepository, times(1)).save(any(Relatorio.class));
    }

    @Test
    @DisplayName("Success: Create report with specified professor")
    void criarRelatorio_WithSpecifiedProfessor_ReturnsRelatorioResponse() {
        // Arrange
        RelatorioRequest requestWithProfessor = new RelatorioRequest(
                aula.getId(),
                "Report text",
                professor.getId()
        );

        when(funcionarioRepository.findById(professor.getId())).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(relatorioRepository.existsByAulaId(aula.getId())).thenReturn(false);
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        // Act
        RelatorioResponse result = relatorioService.criarRelatorio(professorUser, requestWithProfessor);

        // Assert
        assertNotNull(result);
        verify(relatorioRepository, times(1)).save(any(Relatorio.class));
    }

    @Test
    @DisplayName("Failure: Throws exception when lesson doesn't exist")
    void criarRelatorio_WithNonExistentLesson_ThrowsRuntimeException() {
        // Arrange
        when(funcionarioRepository.findByUsuario(professorUser)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.criarRelatorio(professorUser, relatorioRequest));
        verify(relatorioRepository, never()).save(any(Relatorio.class));
    }

    @Test
    @DisplayName("Failure: Throws exception when report already exists for lesson")
    void criarRelatorio_WithExistingReport_ThrowsRuntimeException() {
        // Arrange
        when(funcionarioRepository.findByUsuario(professorUser)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(relatorioRepository.existsByAulaId(aula.getId())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.criarRelatorio(professorUser, relatorioRequest));
        verify(relatorioRepository, never()).save(any(Relatorio.class));
    }

    // ============ ATUALIZAR RELATORIO TESTS ============

    @Test
    @DisplayName("Success: Update report text")
    void atualizarTextoRelatorio_WithValidReport_ReturnsUpdatedResponse() {
        // Arrange
        RelatorioRequest updateRequest = new RelatorioRequest(
                aula.getId(),
                "Updated report text",
                null
        );

        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.of(relatorio));
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        // Act
        RelatorioResponse result = relatorioService.atualizarTextoRelatorio(
                professorUser,
                updateRequest,
                relatorio.getId()
        );

        // Assert
        assertNotNull(result);
        ArgumentCaptor<Relatorio> captor = ArgumentCaptor.forClass(Relatorio.class);
        verify(relatorioRepository).save(captor.capture());
    }

    @Test
    @DisplayName("Failure: Throws exception when report doesn't exist")
    void atualizarTextoRelatorio_WithNonExistentReport_ThrowsRuntimeException() {
        // Arrange
        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.atualizarTextoRelatorio(professorUser, relatorioRequest, relatorio.getId()));
    }

    // ============ MEUS RELATORIOS TESTS ============

    @Test
    @DisplayName("Success: List professor's reports")
    void meusRelatorios_WithLoggedInProfessor_ReturnsPageOfReports() {
        // Arrange
        Page<Relatorio> reportPage = new PageImpl<>(
                Collections.singletonList(relatorio),
                PageRequest.of(0, 10),
                1
        );

        when(funcionarioRepository.findByUsuario(professorUser)).thenReturn(Optional.of(professor));
        when(relatorioRepository.findByProfessorId(eq(professor.getId()), any(Pageable.class)))
                .thenReturn(reportPage);

        // Act
        Page<RelatorioResponse> result = relatorioService.meusRelatorios(professorUser, 0, 10, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Success: List student's reports")
    void meusRelatorios_WithLoggedInStudent_ReturnsPageOfReports() {
        // Arrange
        Page<Relatorio> reportPage = new PageImpl<>(
                Collections.singletonList(relatorio),
                PageRequest.of(0, 10),
                1
        );

        when(funcionarioRepository.findByUsuario(studentUser)).thenReturn(Optional.empty());
        when(alunoRepository.findByUsuario(studentUser)).thenReturn(Optional.of(aluno));
        when(relatorioRepository.findByAulaAlunoId(eq(aluno.getId()), any(Pageable.class)))
                .thenReturn(reportPage);

        // Act
        Page<RelatorioResponse> result = relatorioService.meusRelatorios(studentUser, 0, 10, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Success: Filter reports by title")
    void meusRelatorios_WithTitleFilter_ReturnsFilteredReports() {
        // Arrange
        Page<Relatorio> filteredPage = new PageImpl<>(
                Collections.singletonList(relatorio),
                PageRequest.of(0, 10),
                1
        );

        when(funcionarioRepository.findByUsuario(professorUser)).thenReturn(Optional.of(professor));
        when(relatorioRepository.findByProfessorIdAndAulaTituloContainingIgnoreCase(
                eq(professor.getId()),
                eq("Matemática"),
                any(Pageable.class)
        )).thenReturn(filteredPage);

        // Act
        Page<RelatorioResponse> result = relatorioService.meusRelatorios(professorUser, 0, 10, "Matemática");

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Failure: Throws exception when user profile not found")
    void meusRelatorios_WithNoUserProfile_ThrowsRuntimeException() {
        // Arrange
        when(funcionarioRepository.findByUsuario(professorUser)).thenReturn(Optional.empty());
        when(alunoRepository.findByUsuario(professorUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.meusRelatorios(professorUser, 0, 10, null));
    }

    // ============ LISTA RELATORIOS TESTS ============

    @Test
    @DisplayName("Success: List all reports")
    void listaRelatorios_WithoutFilters_ReturnsAllReports() {
        // Arrange
        Page<Relatorio> reportPage = new PageImpl<>(
                Collections.singletonList(relatorio),
                PageRequest.of(0, 10),
                1
        );

        when(relatorioRepository.findAll(any(Pageable.class))).thenReturn(reportPage);

        // Act
        Page<RelatorioResponse> result = relatorioService.listaRelatorios(0, 10, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Success: Filter reports by professor name")
    void listaRelatorios_WithProfessorNameFilter_ReturnsFilteredReports() {
        // Arrange
        Page<Relatorio> filteredPage = new PageImpl<>(
                Collections.singletonList(relatorio),
                PageRequest.of(0, 10),
                1
        );

        when(relatorioRepository.findByProfessorNameContainingIgnoreCase("João", any(Pageable.class)))
                .thenReturn(filteredPage);

        // Act
        Page<RelatorioResponse> result = relatorioService.listaRelatorios(0, 10, "João", null);

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Success: Filter reports by student name")
    void listaRelatorios_WithStudentNameFilter_ReturnsFilteredReports() {
        // Arrange
        Page<Relatorio> filteredPage = new PageImpl<>(
                Collections.singletonList(relatorio),
                PageRequest.of(0, 10),
                1
        );

        when(relatorioRepository.findByAulaAlunoNameContainingIgnoreCase("Maria", any(Pageable.class)))
                .thenReturn(filteredPage);

        // Act
        Page<RelatorioResponse> result = relatorioService.listaRelatorios(0, 10, null, "Maria");

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    // ============ CANCELAR RELATORIO TESTS ============

    @Test
    @DisplayName("Success: Cancel active report")
    void cancelarRelatorio_WithActiveReport_SetsCancelledStatus() {
        // Arrange
        relatorio.setStatus(StatusRelatorio.ATIVO);
        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.of(relatorio));
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        // Act
        relatorioService.cancelarRelatorio(relatorio.getId());

        // Assert
        ArgumentCaptor<Relatorio> captor = ArgumentCaptor.forClass(Relatorio.class);
        verify(relatorioRepository).save(captor.capture());
        assertEquals(StatusRelatorio.CANCELADO, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Failure: Throws exception when trying to cancel already cancelled report")
    void cancelarRelatorio_WithAlreadyCancelledReport_ThrowsRuntimeException() {
        // Arrange
        relatorio.setStatus(StatusRelatorio.CANCELADO);
        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.of(relatorio));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.cancelarRelatorio(relatorio.getId()));
        verify(relatorioRepository, never()).save(any(Relatorio.class));
    }

    @Test
    @DisplayName("Failure: Throws exception when report doesn't exist")
    void cancelarRelatorio_WithNonExistentReport_ThrowsRuntimeException() {
        // Arrange
        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.cancelarRelatorio(relatorio.getId()));
    }

    // ============ CONFIRMAR LEITURA TESTS ============

    @Test
    @DisplayName("Success: Student confirms reading report")
    void confirmarLeitura_WithValidStudent_MarksReportAsRead() {
        // Arrange
        relatorio.setLido(false);
        when(alunoRepository.findByUsuario(studentUser)).thenReturn(Optional.of(aluno));
        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.of(relatorio));
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        // Act
        relatorioService.confirmarLeitura(relatorio.getId(), studentUser);

        // Assert
        ArgumentCaptor<Relatorio> captor = ArgumentCaptor.forClass(Relatorio.class);
        verify(relatorioRepository).save(captor.capture());
        assertTrue(captor.getValue().getLido());
    }

    @Test
    @DisplayName("Failure: Throws exception when non-student tries to confirm reading")
    void confirmarLeitura_WithNonStudent_ThrowsRuntimeException() {
        // Arrange
        when(alunoRepository.findByUsuario(studentUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.confirmarLeitura(relatorio.getId(), studentUser));
    }

    @Test
    @DisplayName("Failure: Throws exception when student doesn't own the report")
    void confirmarLeitura_WithStudentNotOwningReport_ThrowsRuntimeException() {
        // Arrange
        Aluno otherStudent = new Aluno();
        otherStudent.setId(UUID.randomUUID());
        otherStudent.setName("Other Student");

        User otherStudentUser = new User();
        otherStudentUser.setId(UUID.randomUUID());
        otherStudentUser.setEmail("other@test.com");
        otherStudent.setUsuario(otherStudentUser);

        when(alunoRepository.findByUsuario(otherStudentUser)).thenReturn(Optional.of(otherStudent));
        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.of(relatorio));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.confirmarLeitura(relatorio.getId(), otherStudentUser));
    }

    @Test
    @DisplayName("Failure: Throws exception when report doesn't exist")
    void confirmarLeitura_WithNonExistentReport_ThrowsRuntimeException() {
        // Arrange
        when(alunoRepository.findByUsuario(studentUser)).thenReturn(Optional.of(aluno));
        when(relatorioRepository.findById(relatorio.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                relatorioService.confirmarLeitura(relatorio.getId(), studentUser));
    }

    // ============ DATA INTEGRITY TESTS ============

    @Test
    @DisplayName("Data Integrity: Report response contains all necessary data")
    void criarRelatorio_VerifiesResponseDataIntegrity() {
        // Arrange
        when(funcionarioRepository.findByUsuario(professorUser)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(relatorioRepository.existsByAulaId(aula.getId())).thenReturn(false);
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        // Act
        RelatorioResponse result = relatorioService.criarRelatorio(professorUser, relatorioRequest);

        // Assert
        assertNotNull(result.aulaId());
        assertNotNull(result.professorId());
        assertNotNull(result.createdAt());
        assertFalse(result.lido());
    }
}
