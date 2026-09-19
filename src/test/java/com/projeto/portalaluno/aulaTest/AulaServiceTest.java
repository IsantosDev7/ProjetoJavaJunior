package com.projeto.portalaluno.aulaTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;
import com.projeto.portalaluno.aula.Aula;
import com.projeto.portalaluno.aula.AulaRepository;
import com.projeto.portalaluno.aula.AulaService;
import com.projeto.portalaluno.aula.dto.AulaRequest;
import com.projeto.portalaluno.aula.dto.AulaResponse;
import com.projeto.portalaluno.aula.roles.Modalidade;
import com.projeto.portalaluno.aula.roles.StatusAula;
import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRole;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AulaService - Comprehensive Tests")
public class AulaServiceTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private AulaService aulaService;

    private User usuarioLogado;
    private User professorUser;
    private Funcionario professor;
    private Aluno aluno;
    private Aula aula;
    private AulaRequest aulaRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        usuarioLogado = new User();
        usuarioLogado.setId(UUID.randomUUID());
        usuarioLogado.setEmail("professor@test.com");
        usuarioLogado.setPassword("password123");
        usuarioLogado.setRole(UserRole.FUNCIONARIO);

        professorUser = new User();
        professorUser.setId(UUID.randomUUID());
        professorUser.setEmail("professor2@test.com");
        professorUser.setPassword("password123");
        professorUser.setRole(UserRole.FUNCIONARIO);

        // Create test funcionario (professor)
        professor = new Funcionario();
        professor.setId(UUID.randomUUID());
        professor.setName("Professor João");
        professor.setUsuario(usuarioLogado);
        professor.setPhone("11987654321");
        professor.setBirthDate(LocalDate.of(1985, 5, 15));

        // Create test aluno
        aluno = new Aluno();
        aluno.setId(UUID.randomUUID());
        aluno.setName("Aluno Maria");
        aluno.setPhone("11987654322");
        aluno.setBirthDate(LocalDate.of(2000, 3, 20));

        // Create test aula
        aula = new Aula();
        aula.setId(UUID.randomUUID());
        aula.setTitulo("Matemática Avançada");
        aula.setModalidade(Modalidade.PRESENCIAL);
        aula.setDuracaoAula(60);
        aula.setDataHoraAula(LocalDateTime.of(2024, 6, 15, 14, 0));
        aula.setStatusAula(StatusAula.PREVISTA);
        aula.setProfessor(professor);
        aula.setAluno(aluno);


        aulaRequest = new AulaRequest(
                "Física Clássica",
                Modalidade.EAD,
                90,
                aluno.getId(),
                LocalDateTime.of(2024, 6, 16, 15, 0)
        );
    }

    // ============ CADASTRAR AULA TESTS ============

    @Test
    @DisplayName("Success: Create lesson when professor and student exist")
    void cadastrarAula_WithValidProfessorAndAluno_ReturnsAulaResponse() {
        // Arrange
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(alunoRepository.findById(aulaRequest.getAlunoId())).thenReturn(Optional.of(aluno));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        // Act
        AulaResponse result = aulaService.cadastrarAula(aulaRequest, usuarioLogado);

        // Assert
        assertNotNull(result);
        assertEquals(aula.getTitulo(), result.getTitulo());
        assertEquals(aula.getModalidade(), result.getModalidade());
        assertEquals(aula.getDuracaoAula(), result.getDuracao());
        verify(aulaRepository, times(1)).save(any(Aula.class));
    }

    @Test
    @DisplayName("Failure: Throws exception when professor not found")
    void cadastrarAula_WithInvalidProfessor_ThrowsRuntimeException() {
        // Arrange
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aulaService.cadastrarAula(aulaRequest, usuarioLogado));
        verify(aulaRepository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Failure: Throws exception when student not found")
    void cadastrarAula_WithInvalidAluno_ThrowsRuntimeException() {
        // Arrange
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(alunoRepository.findById(aulaRequest.getAlunoId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aulaService.cadastrarAula(aulaRequest, usuarioLogado));
        verify(aulaRepository, never()).save(any(Aula.class));
    }

    // ============ MINHA AULAS TESTS ============

    @Test
    @DisplayName("Success: List student's lessons with pagination")
    void minhaAulas_WithValidStudent_ReturnsPageOfAulas() {
        // Arrange
        User studentUser = new User();
        studentUser.setId(UUID.randomUUID());
        studentUser.setEmail("student@test.com");
        studentUser.setRole(UserRole.ALUNO);

        Page<Aula> aulaPage = new PageImpl<>(Collections.singletonList(aula), PageRequest.of(0, 10), 1);
        when(alunoRepository.findByUsuario(studentUser)).thenReturn(Optional.of(aluno));
        when(aulaRepository.findByAlunoId(aluno.getId(), PageRequest.of(0, 10, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "dataHoraAula")))).thenReturn(aulaPage);

        // Act
        Page<AulaResponse> result = aulaService.minhaAulas(studentUser, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(alunoRepository, times(1)).findByUsuario(studentUser);
    }

    @Test
    @DisplayName("Failure: Throws exception when student profile not found")
    void minhaAulas_WithNoStudentProfile_ThrowsRuntimeException() {
        // Arrange
        User invalidStudent = new User();
        invalidStudent.setEmail("invalid@test.com");
        when(alunoRepository.findByUsuario(invalidStudent)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aulaService.minhaAulas(invalidStudent, 0, 10));
    }

    // ============ LISTAR AULAS ALUNOS TESTS ============

    @Test
    @DisplayName("Success: List lessons by student ID with pagination")
    void listarAulasAlunos_WithValidStudentId_ReturnsPageOfAulas() {
        // Arrange
        Page<Aula> aulaPage = new PageImpl<>(Collections.singletonList(aula), PageRequest.of(0, 10), 1);
        when(aulaRepository.findByAlunoId(eq(aluno.getId()), any(Pageable.class))).thenReturn(aulaPage);

        // Act
        Page<AulaResponse> result = aulaService.listarAulasAlunos(aluno.getId(), 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(aulaRepository, times(1)).findByAlunoId(eq(aluno.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("Success: Return empty page when student has no lessons")
    void listarAulasAlunos_WithStudentHavingNoLessons_ReturnsEmptyPage() {
        // Arrange
        Page<Aula> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(aulaRepository.findByAlunoId(eq(aluno.getId()), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        Page<AulaResponse> result = aulaService.listarAulasAlunos(aluno.getId(), 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    // ============ LISTAR AULAS PROFESSOR TESTS ============

    @Test
    @DisplayName("Success: List lessons by teacher ID with pagination")
    void listarAulasProfessor_WithValidProfessorId_ReturnsPageOfAulas() {
        // Arrange
        Page<Aula> aulaPage = new PageImpl<>(Collections.singletonList(aula), PageRequest.of(0, 10), 1);
        when(aulaRepository.findByProfessorId(eq(professor.getId()), any(Pageable.class))).thenReturn(aulaPage);

        // Act
        Page<AulaResponse> result = aulaService.listarAulasProfessor(professor.getId(), 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(aulaRepository, times(1)).findByProfessorId(eq(professor.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("Success: Return empty page when teacher has no lessons")
    void listarAulasProfessor_WithTeacherHavingNoLessons_ReturnsEmptyPage() {
        // Arrange
        Page<Aula> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(aulaRepository.findByProfessorId(eq(professor.getId()), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        Page<AulaResponse> result = aulaService.listarAulasProfessor(professor.getId(), 0, 10);

        // Assert
        assertEquals(0, result.getTotalElements());
    }

    // ============ ATUALIZAR AULA TESTS ============

    @Test
    @DisplayName("Success: Update lesson when professor and student exist")
    void atualizarAula_WithValidData_ReturnsUpdatedAulaResponse() {
        // Arrange
        AulaRequest updateRequest = new AulaRequest(
                "Física Moderna",
                Modalidade.PRESENCIAL,
                120,
                aluno.getId(),
                LocalDateTime.of(2024, 6, 17, 16, 0)
        );

        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(alunoRepository.findById(updateRequest.getAlunoId())).thenReturn(Optional.of(aluno));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        // Act
        AulaResponse result = aulaService.atualizarAula(aula.getId(), updateRequest, usuarioLogado);

        // Assert
        assertNotNull(result);
        ArgumentCaptor<Aula> captor = ArgumentCaptor.forClass(Aula.class);
        verify(aulaRepository).save(captor.capture());
        assertEquals(StatusAula.PREVISTA, captor.getValue().getStatusAula());
    }

    @Test
    @DisplayName("Failure: Throws exception when lesson doesn't exist")
    void atualizarAula_WithNonExistentLesson_ThrowsRuntimeException() {
        // Arrange
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aulaService.atualizarAula(aula.getId(), aulaRequest, usuarioLogado));
    }

    @Test
    @DisplayName("Failure: Throws exception when student doesn't exist during update")
    void atualizarAula_WithNonExistentStudent_ThrowsRuntimeException() {
        // Arrange
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(alunoRepository.findById(aulaRequest.getAlunoId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aulaService.atualizarAula(aula.getId(), aulaRequest, usuarioLogado));
    }

    // ============ CANCELAR AULA TESTS ============

    @Test
    @DisplayName("Success: Cancel lesson successfully")
    void cancelarAula_WithValidLesson_SetStatusToCancelada() {
        // Arrange
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        // Act
        aulaService.cancelarAula(aula.getId());

        // Assert
        ArgumentCaptor<Aula> captor = ArgumentCaptor.forClass(Aula.class);
        verify(aulaRepository).save(captor.capture());
        assertEquals(StatusAula.CANCELADA, captor.getValue().getStatusAula());
    }

    @Test
    @DisplayName("Failure: Throws exception when lesson doesn't exist")
    void cancelarAula_WithNonExistentLesson_ThrowsRuntimeException() {
        // Arrange
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aulaService.cancelarAula(aula.getId()));
        verify(aulaRepository, never()).save(any(Aula.class));
    }

    @Test
    @DisplayName("Edge Case: Cancel already cancelled lesson")
    void cancelarAula_WithAlreadyCancelledLesson_SetsCancelledStatus() {
        // Arrange
        aula.setStatusAula(StatusAula.CANCELADA);
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        // Act
        aulaService.cancelarAula(aula.getId());

        // Assert
        ArgumentCaptor<Aula> captor = ArgumentCaptor.forClass(Aula.class);
        verify(aulaRepository).save(captor.capture());
        assertEquals(StatusAula.CANCELADA, captor.getValue().getStatusAula());
    }

    // ============ DATA INTEGRITY & EDGE CASES ============

    @Test
    @DisplayName("Edge Case: Multiple lessons on same day")
    void minhaAulas_WithMultipleLessonsOnSameDay_ReturnsAllLessons() {
        // Arrange
        User studentUser = new User();
        studentUser.setEmail("student@test.com");
        studentUser.setRole(UserRole.ALUNO);

        Aula aula2 = new Aula();
        aula2.setId(UUID.randomUUID());
        aula2.setTitulo("Química");
        aula2.setDataHoraAula(LocalDateTime.of(2024, 6, 15, 10, 0));

        Page<Aula> aulaPage = new PageImpl<>(Arrays.asList(aula, aula2), PageRequest.of(0, 10), 2);
        when(alunoRepository.findByUsuario(studentUser)).thenReturn(Optional.of(aluno));
        when(aulaRepository.findByAlunoId(eq(aluno.getId()), any(Pageable.class))).thenReturn(aulaPage);

        // Act
        Page<AulaResponse> result = aulaService.minhaAulas(studentUser, 0, 10);

        // Assert
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("Boundary: Lesson duration edge cases")
    void cadastrarAula_WithVariousDurations_CreateSuccessfully() {
        // Arrange
        AulaRequest shortRequest = new AulaRequest(
                "Quick Class",
                Modalidade.EAD,
                5,
                aluno.getId(),
                LocalDateTime.now().plusHours(1)
        );

        AulaRequest longRequest = new AulaRequest(
                "Long Class",
                Modalidade.EAD,
                480,
                aluno.getId(),
                LocalDateTime.now().plusHours(1)
        );

        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(alunoRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        // Act & Assert
        AulaResponse shortResult = aulaService.cadastrarAula(shortRequest, usuarioLogado);
        AulaResponse longResult = aulaService.cadastrarAula(longRequest, usuarioLogado);

        assertNotNull(shortResult);
        assertNotNull(longResult);
        verify(aulaRepository, times(2)).save(any(Aula.class));
    }
}
