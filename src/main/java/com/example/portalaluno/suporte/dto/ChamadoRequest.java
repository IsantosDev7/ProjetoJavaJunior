package com.example.portalaluno.suporte.dto;

import com.example.portalaluno.suporte.ChamadoPrioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ChamadoRequest {

    @NotBlank
    @Size(min = 10, max = 100)
    private String titulo;

    @NotBlank
    @Size(min = 10, max = 500)
    private String descricao;

    private UUID alunoId;
}
