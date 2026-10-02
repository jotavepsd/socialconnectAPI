package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO de SAÍDA
@Schema(description = "Doação retornada pela API")
public record DoacaoResponseDTO(
        @Schema(example = "1")
        Long idDoacao,
        @Schema(example = "1")
        Long idDoador,
        @Schema(example = "Mercado Bom Preço")
        String nomeDoador,
        @Schema(example = "2026-09-14")
        LocalDate dataDoacao,
        @Schema(example = "100.00")
        BigDecimal valor,
        @Schema(example = "ALIMENTO")
        TipoDoacao tipo,
        @Schema(example = "10 cestas básicas")
        String descricao
) {}
