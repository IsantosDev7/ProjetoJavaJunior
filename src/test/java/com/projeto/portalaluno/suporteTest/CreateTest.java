package com.projeto.portalaluno.suporteTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;
import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.suporte.Chamado;
import com.projeto.portalaluno.suporte.ChamadoRepository;
import com.projeto.portalaluno.suporte.ChamadoService;
import com.projeto.portalaluno.suporte.dto.ChamadoRequest;
import com.projeto.portalaluno.suporte.dto.ChamadoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CreateTest {

    @Mock
    private ChamadoRepository chamadoRepository;
    @Mock
    private AlunoRepository alunoRepository;
    @InjectMocks
    private ChamadoService chamadoService;

    private ChamadoRequest chamado;
    private User usuarioLogado;

    @BeforeEach
    void setup() {
        chamado = new ChamadoRequest();
        chamado.setTitulo("Chamado de teste");
        chamado.setDescricao("Descrição de teste com mais de dez caracteres");

        usuarioLogado = new User();
    }

    @Test
    @DisplayName("case 1: criação normal de chamado.")
    void naoDeveLancarException(){
        Aluno alunoMock = new Aluno();
        alunoMock.setId(UUID.randomUUID());

        Chamado chamadoSalvo = new Chamado();
        chamadoSalvo.setAluno(alunoMock);
        chamadoSalvo.setTitulo(chamado.getTitulo());
        chamadoSalvo.setDescricao(chamado.getDescricao());

        when(alunoRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(alunoMock));
        when(chamadoRepository.save(any())).thenReturn(chamadoSalvo);

        ChamadoResponse resultado = chamadoService.criarChamado(chamado, usuarioLogado);

        assertEquals(chamado.getTitulo(), resultado.getTitulo());
        assertEquals(chamado.getDescricao(), resultado.getDescricao());
        assertEquals(alunoMock.getId(), resultado.getAlunoId());
        verify(chamadoRepository).save(any());
    }

    @Test
    @DisplayName("case 2: Usuário não é aluno")
    void deveLancarException(){
        when(alunoRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chamadoService.criarChamado(chamado, usuarioLogado));

        assertEquals("Somente alunos podem criar chamados.", exception.getMessage());
    }
}
