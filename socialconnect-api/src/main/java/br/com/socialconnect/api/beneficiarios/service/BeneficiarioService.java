package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository repository;

    public BeneficiarioService(BeneficiarioRepository repository) {
        this.repository = repository;
    }

    // Listar com paginação e filtros opcionais
    public Page<BeneficiarioResponseDTO> listar(String nome, String cpf, Pageable pageable) {
        Page<Beneficiario> page;
        if (cpf != null && !cpf.isBlank()) {
            page = repository.findByCpf(normalizarCpf(cpf), pageable);                        // filtro exato (prioridade)
        } else if (nome != null && !nome.isBlank()) {
            page = repository.findByNomeContainingIgnoreCase(nome, pageable);  // filtro parcial
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::toResponseDTO);
    }

    public BeneficiarioResponseDTO buscarPorId(Long idBeneficiario) {
        return toResponseDTO(buscarEntidade(idBeneficiario));
    }

    // POST
    public BeneficiarioResponseDTO criar(BeneficiarioRequestDTO dto) {
        String cpf = normalizarCpf(dto.cpf());
        if (repository.existsByCpf(cpf)) {
            throw new CpfDuplicadoException(cpf);
        }
        Beneficiario entity = Beneficiario.builder()
                .nome(dto.nome())
                .cpf(cpf)
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(LocalDate.now())
                .build();
        return toResponseDTO(repository.save(entity));
    }

    // PUT (substituição total)
    public BeneficiarioResponseDTO atualizar(Long idBeneficiario, BeneficiarioRequestDTO dto) {
        Beneficiario entity = buscarEntidade(idBeneficiario);
        String cpf = normalizarCpf(dto.cpf());
        if (!entity.getCpf().equals(cpf) && repository.existsByCpf(cpf)) {
            throw new CpfDuplicadoException(cpf);
        }
        entity.setNome(dto.nome());
        entity.setCpf(cpf);
        entity.setTelefone(dto.telefone());
        entity.setEndereco(dto.endereco());
        entity.setSituacaoVulnerabilidade(dto.situacaoVulnerabilidade());
        return toResponseDTO(repository.save(entity));
    }

    // PATCH (atualiza apenas os campos não-nulos)
    public BeneficiarioResponseDTO atualizarParcial(Long idBeneficiario, BeneficiarioPatchDTO dto) {
        Beneficiario entity = buscarEntidade(idBeneficiario);
        if (dto.nome() != null) entity.setNome(dto.nome());
        if (dto.telefone() != null) entity.setTelefone(dto.telefone());
        if (dto.endereco() != null) entity.setEndereco(dto.endereco());
        if (dto.situacaoVulnerabilidade() != null) entity.setSituacaoVulnerabilidade(dto.situacaoVulnerabilidade());
        return toResponseDTO(repository.save(entity));
    }

    public void deletar(Long idBeneficiario) {
        if (!repository.existsById(idBeneficiario)) {
            throw new RecursoNaoEncontradoException("Beneficiário não encontrado: " + idBeneficiario);
        }
        repository.deleteById(idBeneficiario);
    }

    private Beneficiario buscarEntidade(Long idBeneficiario) {
        return repository.findById(idBeneficiario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficiário não encontrado: " + idBeneficiario));
    }

    // CPF é sempre gravado só com dígitos, para a checagem de duplicidade não depender da máscara
    private String normalizarCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    // Mapeador Entity -> Response DTO
    private BeneficiarioResponseDTO toResponseDTO(Beneficiario e) {
        return new BeneficiarioResponseDTO(
                e.getIdBeneficiario(), e.getNome(), e.getCpf(),
                e.getTelefone(), e.getEndereco(),
                e.getSituacaoVulnerabilidade(), e.getDataCadastro()
        );
    }
}
