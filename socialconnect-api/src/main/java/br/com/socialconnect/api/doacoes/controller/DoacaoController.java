package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.service.DoacaoService;
import br.com.socialconnect.api.exception.ProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doacoes")
@Tag(name = "Doações", description = "API para registro e consulta de doações")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @GetMapping
    @Operation(summary = "Lista doações", description = "Lista paginada com filtros opcionais por período e tipo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<DoacaoResponseDTO>> listar(
            @Parameter(description = "Data inicial (inclusive)", example = "2026-09-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final (inclusive)", example = "2026-09-30")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @Parameter(description = "Tipo da doação", example = "ALIMENTO")
            @RequestParam(required = false) TipoDoacao tipo,
            @ParameterObject @PageableDefault(size = 20, sort = "dataDoacao") Pageable pageable) {
        return ResponseEntity.ok(doacaoService.listar(dataInicio, dataFim, tipo, pageable));
    }

    @GetMapping("/{idDoacao}")
    @Operation(summary = "Busca doação por ID")
    @ApiResponse(responseCode = "200", description = "Doação encontrada")
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> buscarPorId(
            @Parameter(description = "ID da doação", example = "1") @PathVariable Long idDoacao) {
        return ResponseEntity.ok(doacaoService.buscarPorId(idDoacao));
    }

    @PostMapping
    @Operation(summary = "Registra uma doação")
    @ApiResponse(responseCode = "201", description = "Doação registrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doador não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> criar(@Valid @RequestBody DoacaoRequestDTO dto) {
        DoacaoResponseDTO salva = doacaoService.criar(dto);
        URI location = URI.create("/api/v1/doacoes/" + salva.idDoacao());
        return ResponseEntity.created(location).body(salva);
    }

    @PutMapping("/{idDoacao}")
    @Operation(summary = "Substitui uma doação")
    @ApiResponse(responseCode = "200", description = "Doação atualizada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doação ou doador não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> atualizar(
            @Parameter(description = "ID da doação", example = "1") @PathVariable Long idDoacao,
            @Valid @RequestBody DoacaoRequestDTO dto) {
        return ResponseEntity.ok(doacaoService.atualizar(idDoacao, dto));
    }

    @PatchMapping("/{idDoacao}")
    @Operation(summary = "Atualiza parcialmente uma doação")
    @ApiResponse(responseCode = "200", description = "Doação atualizada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> atualizarParcial(
            @Parameter(description = "ID da doação", example = "1") @PathVariable Long idDoacao,
            @Valid @RequestBody DoacaoPatchDTO dto) {
        return ResponseEntity.ok(doacaoService.atualizarParcial(idDoacao, dto));
    }

    @DeleteMapping("/{idDoacao}")
    @Operation(summary = "Remove uma doação")
    @ApiResponse(responseCode = "204", description = "Doação removida")
    @ApiResponse(responseCode = "404", description = "Doação não encontrada",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da doação", example = "1") @PathVariable Long idDoacao) {
        doacaoService.deletar(idDoacao);
        return ResponseEntity.noContent().build();
    }
}
