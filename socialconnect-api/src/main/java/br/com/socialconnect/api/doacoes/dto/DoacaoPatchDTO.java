package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.validation.DataNaoFutura;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO para PATCH: todos os campos opcionais
@Schema(description = "Campos para atualização parcial de uma doação")
public record DoacaoPatchDTO(
        @Schema(example = "2026-09-14")
        @DataNaoFutura(message = "{DataNaoFutura.dataDoacao}")
        LocalDate dataDoacao,

        @Schema(example = "150.50")
        @Positive(message = "{Positive.valor}")
        BigDecimal valor,

        @Schema(example = "ROUPA")
        TipoDoacao tipo,

        @Schema(example = "Agasalhos de inverno")
        @Size(max = 500, message = "{Size.descricao}")
        String descricao
) {}
