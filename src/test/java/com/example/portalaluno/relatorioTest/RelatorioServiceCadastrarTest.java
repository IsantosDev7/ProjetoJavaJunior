//package com.example.portalaluno.relatorioTest;
//
//import com.example.portalaluno.aluno.AlunoRepository;
//import com.example.portalaluno.aula.AulaRepository;
//import com.example.portalaluno.funcionario.FuncionarioRepository;
//import com.example.portalaluno.relatorio.Relatorio;
//import com.example.portalaluno.relatorio.RelatorioRepository;
//import com.example.portalaluno.relatorio.RelatorioService;
//import com.example.portalaluno.relatorio.dto.RelatorioRequest;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//public class RelatorioServiceCadastrarTest {
//
//    @Mock
//    private AulaRepository aulaRepository;
//    @Mock
//    private FuncionarioRepository funcionarioRepository;
//    @Mock
//    private RelatorioRepository relatorioRepository;
//    @Mock
//    private AlunoRepository alunoRepository;
//
//    @InjectMocks
//    private RelatorioService relatorioService;
//
//    @Test
//    @DisplayName("Case 1: Cadastro de relatório ocorre normalmente")
//    void NaoDeveLancarNenhumaExcessao(){
//        RelatorioRequest request = new RelatorioRequest();
//        request.setProfessorId("0b38abfb-63e3-423e-b9ce-bfe57920ad01");
//        request.setTexto("aaaaaaaaaaaaaa");
//        request.setAulaid();
//    }
//}
