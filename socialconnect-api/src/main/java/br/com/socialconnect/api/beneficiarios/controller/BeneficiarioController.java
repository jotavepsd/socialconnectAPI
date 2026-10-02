package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
import br.com.socialconnect.api.exception.ProblemDetail;
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
@RequestMapping("/api/v1/beneficiarios")
@Tag(name = "Beneficiários", description = "API para gestão de beneficiários")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista beneficiários",
            description = "Retorna uma lista paginada de beneficiários com filtros opcionais por nome (parcial) ou CPF (exato)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<BeneficiarioResponseDTO>> listar(
            @Parameter(description = "Nome para filtrar (parcial, sem diferenciar maiúsculas)", example = "maria")
            @RequestParam(required = false) String nome,
            @Parameter(description = "CPF para filtrar (exato)", example = "52998224725")
            @RequestParam(required = false) String cpf,
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação no formato campo,direção", example = "idBeneficiario,asc")
            @RequestParam(defaultValue = "idBeneficiario,asc") String sort) {

        // Sanitiza o parâmetro sort (remove colchetes, aspas, espaços)
        String sortLimpo = sort.replaceAll("[\\[\\]\" ]", "");

        // Divide em campo e direção
        String[] sortParts = sortLimpo.split(",");
        String campo = sortParts[0];
        Sort.Direction direcao = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // Cria o Pageable manualmente
        Pageable pageable = PageRequest.of(page, size, Sort.by(direcao, campo));

        return ResponseEntity.ok(service.listar(nome, cpf, pageable));
    }

    @GetMapping("/{idBeneficiario}")
    @Operation(summary = "Busca beneficiário por ID")
    @ApiResponse(responseCode = "200", description = "Beneficiário encontrado")
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(
            @Parameter(description = "ID do beneficiário", example = "1") @PathVariable Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    @PostMapping
    @Operation(summary = "Cria um novo beneficiário", description = "Cadastra um novo beneficiário no sistema")
    @ApiResponse(responseCode = "201", description = "Beneficiário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> criar(@Valid @RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + salvo.idBeneficiario());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{idBeneficiario}")
    @Operation(summary = "Substitui um beneficiário", description = "Atualização total: campos ausentes viram nulos")
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado para outro beneficiário",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(
            @Parameter(description = "ID do beneficiário", example = "1") @PathVariable Long idBeneficiario,
            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idBeneficiario, dto));
    }

    @PatchMapping("/{idBeneficiario}")
    @Operation(summary = "Atualiza parcialmente um beneficiário", description = "Só os campos enviados são alterados")
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizarParcial(
            @Parameter(description = "ID do beneficiário", example = "1") @PathVariable Long idBeneficiario,
            @Valid @RequestBody BeneficiarioPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idBeneficiario, dto));
    }

    @DeleteMapping("/{idBeneficiario}")
    @Operation(summary = "Remove um beneficiário")
    @ApiResponse(responseCode = "204", description = "Beneficiário removido")
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do beneficiário", example = "1") @PathVariable Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}
