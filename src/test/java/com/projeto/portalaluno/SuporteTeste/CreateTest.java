package com.projeto.portalaluno.SuporteTeste;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.suporte.Chamado;
import com.projeto.portalaluno.suporte.ChamadoRepository;
import com.projeto.portalaluno.suporte.ChamadoService;
import com.projeto.portalaluno.suporte.dto.ChamadoRequest;
import com.projeto.portalaluno.suporte.dto.ChamadoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("case 1: criação normal de chamado.")
    void naoDeveLancarException(){
        ChamadoRequest chamado = new ChamadoRequest();
        chamado.setTitulo("Chamado");
        chamado.setDescricao("Ajuda");

        Aluno alunoMock = new Aluno();
        alunoMock.setId(UUID.randomUUID());
        User usuarioLogado = new User();

        Chamado chamadoSalvo = new Chamado();
        chamadoSalvo.setAluno(alunoMock);
        chamadoSalvo.setTitulo(chamado.getTitulo());
        chamadoSalvo.setDescricao(chamado.getDescricao());

        when(alunoRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(alunoMock));
        when(chamadoRepository.save(any())).thenReturn(chamadoSalvo);

        ChamadoResponse resultado = chamadoService.criarChamado(chamado, usuarioLogado);

        assertEquals("Chamado", resultado.getTitulo());
        assertEquals("Ajuda", resultado.getDescricao());
    }

    @Test
    @DisplayName("case 2: Usuário não é aluno")
    void deveLancarException(){
        ChamadoRequest chamado = new ChamadoRequest();
    }
}
