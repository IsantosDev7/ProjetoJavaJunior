package com.projeto.portalaluno.suporteTest;

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
public class UpdateTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @InjectMocks
    private ChamadoService chamadoService;

    private UUID chamadoId;
    private Chamado chamado;

    @BeforeEach
    void setup() {
        chamadoId = UUID.randomUUID();
        chamado = new Chamado();
        chamado.setResolvido(false);
    }

    @Test
    @DisplayName("case 1: Deve resolver o chamado com sucesso quando o ID for encontrado")
    void deveResolverChamadoComSucesso() {
        // Arrange
        when(chamadoRepository.findById(chamadoId)).thenReturn(Optional.of(chamado));

        // Act
        chamadoService.resolverChamado(chamadoId);

        // Assert
        assertTrue(chamado.isResolvido());
        assertNotNull(chamado.getUpdatedAt());

        // Garante que buscou o chamado e chamou o save no repositório
        verify(chamadoRepository, times(1)).findById(chamadoId);
        verify(chamadoRepository, times(1)).save(chamado);
    }

    @Test
    @DisplayName("case 2: Deve lançar RuntimeException quando o chamado não for encontrado")
    void deveLancarExceptionQuandoChamadoNaoEncontrado() {
        // Arrange
        when(chamadoRepository.findById(chamadoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chamadoService.resolverChamado(chamadoId));

        assertEquals("Chamado não encontrado.", exception.getMessage());

        // Garante que tentou buscar, mas NUNCA chamou o save
        verify(chamadoRepository, times(1)).findById(chamadoId);
        verify(chamadoRepository, never()).save(any());
    }
}