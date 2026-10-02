package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.validation.DataNaoFutura;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO de ENTRADA para POST e PUT
@Schema(description = "Dados para registrar ou substituir uma doação")
public record DoacaoRequestDTO(
        @Schema(description = "ID do doador", example = "1")
        @NotNull(message = "{NotNull.idDoador}")
        Long idDoador,

        @Schema(description = "Data em que a doação foi recebida", example = "2026-09-14")
        @NotNull(message = "{NotNull.dataDoacao}")
        @DataNaoFutura(message = "{DataNaoFutura.dataDoacao}")
        LocalDate dataDoacao,

        @Schema(description = "Valor estimado/monetário da doação", example = "100.00")
        @Positive(message = "{Positive.valor}")
        BigDecimal valor,

        @Schema(description = "Tipo da doação", example = "ALIMENTO")
        @NotNull(message = "{NotNull.tipo}")
        TipoDoacao tipo,

        @Schema(description = "Descrição livre", example = "10 cestas básicas")
        @Size(max = 500, message = "{Size.descricao}")
        String descricao
) {}
