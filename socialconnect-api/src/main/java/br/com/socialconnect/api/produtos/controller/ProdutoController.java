package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.exception.ProblemDetail;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "API para gestão de produtos e estoque")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista produtos", description = "Retorna produtos paginados com filtros opcionais por nome parcial e categoria")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(
            @Parameter(description = "Nome parcial do produto", example = "arroz")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Categoria do produto", example = "ALIMENTO")
            @RequestParam(required = false) CategoriaProduto categoria,
            @Parameter(description = "Número da página, começando em 0", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação no formato campo,direção", example = "nome,asc")
            @RequestParam(defaultValue = "nome,asc") String sort) {

        if (page < 0) {
            throw new IllegalArgumentException("O parâmetro page não pode ser negativo.");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("O parâmetro size deve estar entre 1 e 100.");
        }

        String sortLimpo = sort.replaceAll("[\\[\\]\" ]", "");
        String[] sortParts = sortLimpo.split(",");
        if (sortParts.length == 0 || sortParts[0].isBlank()) {
            throw new IllegalArgumentException("O parâmetro sort é inválido.");
        }
        Sort.Direction direcao;
        if (sortParts.length == 1 || sortParts[1].equalsIgnoreCase("asc")) {
            direcao = Sort.Direction.ASC;
        } else if (sortParts[1].equalsIgnoreCase("desc")) {
            direcao = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("A direção da ordenação deve ser asc ou desc.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direcao, sortParts[0]));
        return ResponseEntity.ok(service.listar(nome, categoria, pageable));
    }

    @GetMapping("/{idProduto}")
    @Operation(summary = "Busca produto por ID")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long idProduto) {
        return ResponseEntity.ok(service.buscarPorId(idProduto));
    }

    @PostMapping
    @Operation(summary = "Cria um produto")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome do produto já cadastrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque não pode ser negativo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/produtos/" + salvo.idProduto());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{idProduto}")
    @Operation(summary = "Atualiza um produto", description = "Atualização completa do produto")
    @ApiResponse(responseCode = "200", description = "Produto atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome do produto já cadastrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque não pode ser negativo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long idProduto,
            @Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idProduto, dto));
    }

    @DeleteMapping("/{idProduto}")
    @Operation(summary = "Remove um produto")
    @ApiResponse(responseCode = "204", description = "Produto removido")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long idProduto) {
        service.deletar(idProduto);
        return ResponseEntity.noContent().build();
    }
}
