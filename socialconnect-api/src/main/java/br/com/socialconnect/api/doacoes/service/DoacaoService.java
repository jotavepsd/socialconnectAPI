package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doacoes.repository.DoacaoSpecifications;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final DoadorRepository doadorRepository;

    public DoacaoService(DoacaoRepository doacaoRepository, DoadorRepository doadorRepository) {
        this.doacaoRepository = doacaoRepository;
        this.doadorRepository = doadorRepository;
    }

    @Transactional(readOnly = true)
    public Page<DoacaoResponseDTO> listar(LocalDate dataInicio, LocalDate dataFim, TipoDoacao tipo, Pageable pageable) {
        return doacaoRepository
                .findAll(DoacaoSpecifications.filtrar(dataInicio, dataFim, tipo), pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public DoacaoResponseDTO buscarPorId(Long idDoacao) {
        return toResponseDTO(buscarEntidade(idDoacao));
    }

    // POST
    @Transactional
    public DoacaoResponseDTO criar(DoacaoRequestDTO dto) {
        Doador doador = buscarDoador(dto.idDoador());
        Doacao doacao = Doacao.builder()
                .doador(doador)
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(dto.descricao())
                .build();
        return toResponseDTO(doacaoRepository.save(doacao));
    }

    // PUT (substituição total)
    @Transactional
    public DoacaoResponseDTO atualizar(Long idDoacao, DoacaoRequestDTO dto) {
        Doacao doacao = buscarEntidade(idDoacao);
        doacao.setDoador(buscarDoador(dto.idDoador()));
        doacao.setDataDoacao(dto.dataDoacao());
        doacao.setValor(dto.valor());
        doacao.setTipo(dto.tipo());
        doacao.setDescricao(dto.descricao());
        return toResponseDTO(doacaoRepository.save(doacao));
    }

    // PATCH (atualiza apenas os campos não-nulos)
    @Transactional
    public DoacaoResponseDTO atualizarParcial(Long idDoacao, DoacaoPatchDTO dto) {
        Doacao doacao = buscarEntidade(idDoacao);
        if (dto.dataDoacao() != null) doacao.setDataDoacao(dto.dataDoacao());
        if (dto.valor() != null) doacao.setValor(dto.valor());
        if (dto.tipo() != null) doacao.setTipo(dto.tipo());
        if (dto.descricao() != null) doacao.setDescricao(dto.descricao());
        return toResponseDTO(doacaoRepository.save(doacao));
    }

    @Transactional
    public void deletar(Long idDoacao) {
        if (!doacaoRepository.existsById(idDoacao)) {
            throw new RecursoNaoEncontradoException("Doação não encontrada: " + idDoacao);
        }
        doacaoRepository.deleteById(idDoacao);
    }

    private Doacao buscarEntidade(Long idDoacao) {
        return doacaoRepository.findById(idDoacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação não encontrada: " + idDoacao));
    }

    private Doador buscarDoador(Long idDoador) {
        return doadorRepository.findById(idDoador)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doador não encontrado: " + idDoador));
    }

    private DoacaoResponseDTO toResponseDTO(Doacao d) {
        Doador doador = d.getDoador();
        return new DoacaoResponseDTO(
                d.getIdDoacao(),
                doador != null ? doador.getIdDoador() : null,
                doador != null ? doador.getNome() : null,
                d.getDataDoacao(),
                d.getValor(),
                d.getTipo(),
                d.getDescricao()
        );
    }
}
