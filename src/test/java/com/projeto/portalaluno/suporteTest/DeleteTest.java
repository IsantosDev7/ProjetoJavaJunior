package com.projeto.portalaluno.suporteTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.suporte.Chamado;
import com.projeto.portalaluno.suporte.ChamadoRepository;
import com.projeto.portalaluno.suporte.ChamadoService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @InjectMocks
    private ChamadoService chamadoService;

    private Chamado chamado;
    private UUID chamadoId;

    @BeforeEach
    void setup() {
        Aluno aluno = new Aluno();
        aluno.setId(UUID.randomUUID());

        chamadoId = UUID.randomUUID();

        chamado = new Chamado();
        chamado.setId(chamadoId);
        chamado.setTitulo("Chamado de teste");
        chamado.setDescricao("Descrição de teste com mais de dez caracteres");
        chamado.setAluno(aluno);
    }

    @Test
    @DisplayName("Case 1: Delete chamado successfully")
    void deveDeletarChamadoComSucesso() {
        when(chamadoRepository.findById(chamadoId)).thenReturn(Optional.of(chamado));
        doNothing().when(chamadoRepository).delete(chamado);

        assertDoesNotThrow(() -> chamadoService.deletarChamado(chamadoId));

        verify(chamadoRepository).findById(chamadoId);
        verify(chamadoRepository).delete(chamado);
    }

    @Test
    @DisplayName("Case 2: Delete chamado fails - chamado not found")
    void deveLancarExcecaoChamadoNaoEncontradoAoDeletar() {
        when(chamadoRepository.findById(chamadoId)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            chamadoService.deletarChamado(chamadoId);
        });

        assertEquals("Chamado não encontrado", excecao.getMessage());
        verify(chamadoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Case 3: Verify delete is called once")
    void deveVerificarQueDeletarEhChamadoUmaVez() {
        when(chamadoRepository.findById(chamadoId)).thenReturn(Optional.of(chamado));
        doNothing().when(chamadoRepository).delete(chamado);

        chamadoService.deletarChamado(chamadoId);

        verify(chamadoRepository, times(1)).delete(chamado);
    }
}
