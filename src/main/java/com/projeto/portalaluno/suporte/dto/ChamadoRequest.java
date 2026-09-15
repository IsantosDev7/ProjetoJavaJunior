package com.projeto.portalaluno.suporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChamadoRequest {

    @NotBlank
    @Size(min = 10, max = 100)
    private String titulo;

    @NotBlank
    @Size(min = 10, max = 500)
    private String descricao;
}
