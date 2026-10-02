package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.validation.EstoqueNaoNegativo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar ou atualizar um produto")
public record ProdutoRequestDTO(
        @Schema(description = "Nome do produto", example = "Arroz 5kg")
        @NotBlank(message = "{produto.nome.obrigatorio}")
        @Size(max = 150, message = "{produto.nome.tamanho}")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        @NotNull(message = "{produto.categoria.obrigatoria}")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade disponível em estoque", example = "3")
        @NotNull(message = "{produto.estoque.atual.obrigatorio}")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima desejada em estoque", example = "10")
        @NotNull(message = "{produto.estoque.minimo.obrigatorio}")
        @EstoqueNaoNegativo(message = "{produto.estoque.minimo}")
        @Min(value = 0, message = "{produto.estoque.minimo}")
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida", example = "unidade")
        @NotBlank(message = "{produto.unidade.obrigatoria}")
        @Size(max = 20, message = "{produto.unidade.tamanho}")
        String unidadeMedida
) {}
