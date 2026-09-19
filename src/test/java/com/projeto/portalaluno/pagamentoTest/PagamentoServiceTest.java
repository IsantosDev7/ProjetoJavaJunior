package com.projeto.portalaluno.pagamentoTest;

import com.projeto.portalaluno.pagamento.Pagamento;
import com.projeto.portalaluno.pagamento.PagamentoRepository;
import com.projeto.portalaluno.pagamento.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    @BeforeEach
    void setup() {
    }

    @Test
    @DisplayName("Case 1: PagamentoService is not empty")
    void deveVerificarQuePagamentoServiceExiste() {
        assertNotNull(pagamentoService);
    }

    @Test
    @DisplayName("Case 2: PagamentoRepository is injected")
    void deveVerificarQueRepositorioEstaInjetado() {
        assertNotNull(pagamentoRepository);
    }
}
