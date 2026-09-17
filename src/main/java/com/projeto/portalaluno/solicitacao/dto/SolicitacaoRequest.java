package com.projeto.portalaluno.solicitacao.dto;

import com.projeto.portalaluno.solicitacao.status.SolicitacaoTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoRequest {

    @NotNull
    private SolicitacaoTipo tipo;

    @NotNull
    private UUID funcionarioAlvoId;

    @NotBlank
    @Size(min = 10, max = 100)
    private String motivo;

}
