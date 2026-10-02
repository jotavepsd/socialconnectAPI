package br.com.socialconnect.api.beneficiarios.dto;

import br.com.socialconnect.api.validation.CPF;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO de ENTRADA: apenas campos que o cliente pode enviar
@Schema(description = "Dados para cadastrar ou substituir um beneficiário")
public record BeneficiarioRequestDTO(
        @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva")
        @NotBlank(message = "{NotBlank.nome}")
        @Size(max = 150, message = "{Size.nome}")
        String nome,

        @Schema(description = "CPF com ou sem máscara (validado pelos dígitos verificadores)", example = "52998224725")
        @NotBlank(message = "{NotBlank.cpf}")
        @CPF
        String cpf,

        @Schema(description = "Telefone com DDD", example = "11999999999")
        @Size(max = 20, message = "{Size.telefone}")
        String telefone,

        @Schema(description = "Endereço completo", example = "Rua das Flores, 123 - Centro")
        @Size(max = 255, message = "{Size.endereco}")
        String endereco,

        @Schema(description = "Descrição da situação de vulnerabilidade", example = "Renda familiar baixa")
        @Size(max = 500, message = "{Size.situacaoVulnerabilidade}")
        String situacaoVulnerabilidade
) {}
