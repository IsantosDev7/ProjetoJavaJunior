package com.projeto.portalaluno.suporteTest;

import com.projeto.portalaluno.aluno.Aluno;
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
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadTest {

    @Mock
    private ChamadoRepository chamadoRepository;
    @InjectMocks
    private ChamadoService chamadoService;

    private ChamadoRequest chamadoRequest;
    private Chamado novoChamado;

    @BeforeEach
    void setup() {
        Aluno aluno = new Aluno();
        aluno.setId(UUID.randomUUID());
        aluno.setName("Israel Santos");

        chamadoRequest = new ChamadoRequest();
        chamadoRequest.setTitulo("Chamado de teste");
        chamadoRequest.setDescricao("Descrição de teste com mais de dez caracteres");

        novoChamado = new Chamado();
        novoChamado.setTitulo(chamadoRequest.getTitulo());
        novoChamado.setDescricao(chamadoRequest.getDescricao());
        novoChamado.setAluno(aluno);
    }

    @Test
    @DisplayName("case 1: Deve listar chamados com paginação sem filtro de datas")
    void listarTodosOsChamados(){
        int pagina = 0;
        int tamanho = 10;

        Pageable pageableExpected = PageRequest.of(pagina, tamanho, Sort.by(Sort.Order.desc("createdAt")));

        Page<Chamado> pageEntity = new PageImpl<>(List.of(novoChamado), pageableExpected, 1);
        when(chamadoRepository.findAll(pageableExpected)).thenReturn(pageEntity);

        Page<ChamadoResponse> result = chamadoService.listarChamados(pagina, tamanho, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(chamadoRepository, times(1)).findAll(pageableExpected);
        verify(chamadoRepository, never()).findByCreatedAtBetween(any(), any(), any());
    }

    @Test
    @DisplayName("case 2: Deve listar chamados filtrando por intervalo de datas")
    void listarChamadosComFiltroDeDatas() {

        int pagina = 0;
        int tamanho = 10;
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();

        Pageable pageableExpected = PageRequest.of(pagina, tamanho, Sort.by(Sort.Order.desc("createdAt")));

        Page<Chamado> pageEntity = new PageImpl<>(List.of(novoChamado), pageableExpected, 1);

        // Define o comportamento do mock para a busca com filtro de data
        when(chamadoRepository.findByCreatedAtBetween(inicio, fim, pageableExpected))
                .thenReturn(pageEntity);

        // Act
        Page<ChamadoResponse> result = chamadoService.listarChamados(pagina, tamanho, inicio, fim);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        // Garante que chamou o filtro de datas e ignorou o findAll simples
        verify(chamadoRepository, times(1)).findByCreatedAtBetween(inicio, fim, pageableExpected);
        verify(chamadoRepository, never()).findAll(any(Pageable.class));
    }
}
