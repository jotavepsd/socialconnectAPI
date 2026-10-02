package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

// DTO de SAÍDA: inclui campos gerados pelo servidor
@Schema(description = "Beneficiário retornado pela API")
public record BeneficiarioResponseDTO(
        @Schema(description = "Identificador do beneficiário", example = "1")
        Long idBeneficiario,
        @Schema(example = "Maria da Silva")
        String nome,
        @Schema(example = "52998224725")
        String cpf,
        @Schema(example = "11999999999")
        String telefone,
        @Schema(example = "Rua das Flores, 123 - Centro")
        String endereco,
        @Schema(example = "Renda familiar baixa")
        String situacaoVulnerabilidade,
        @Schema(description = "Data de cadastro (gerada pelo servidor)", example = "2026-09-14")
        LocalDate dataCadastro
) {}
