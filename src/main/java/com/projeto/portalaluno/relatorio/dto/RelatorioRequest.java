package com.projeto.portalaluno.relatorio.dto;

import com.projeto.portalaluno.shared.security.SanitizeHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RelatorioRequest {

    @NotNull
    private UUID aulaid;

    @Size(max = 3000, message = "O relatório não pode exceder 3000 caracteres")
    @SanitizeHtml
    @NotBlank
    private String texto;

    @NotNull
    private UUID professorId;

}
