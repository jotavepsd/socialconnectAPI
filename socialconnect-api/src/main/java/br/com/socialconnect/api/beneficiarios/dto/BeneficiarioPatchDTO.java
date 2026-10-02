package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

// DTO para PATCH: todos os campos opcionais
@Schema(description = "Campos para atualização parcial (envie só o que quiser alterar)")
public record BeneficiarioPatchDTO(
        @Schema(example = "Maria da Silva Santos")
        @Size(max = 150, message = "{Size.nome}")
        String nome,

        @Schema(example = "11988887777")
        @Size(max = 20, message = "{Size.telefone}")
        String telefone,

        @Schema(example = "Rua Nova, 456")
        @Size(max = 255, message = "{Size.endereco}")
        String endereco,

        @Schema(example = "Desempregado")
        @Size(max = 500, message = "{Size.situacaoVulnerabilidade}")
        String situacaoVulnerabilidade
) {}
