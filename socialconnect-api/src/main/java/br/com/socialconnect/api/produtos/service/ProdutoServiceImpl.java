package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.ProdutoNomeDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoServiceImpl(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        Page<Produto> page;
        boolean temNome = nome != null && !nome.isBlank();
        if (temNome && categoria != null) {
            page = repository.findByNomeContainingIgnoreCaseAndCategoria(nome.trim(), categoria, pageable);
        } else if (temNome) {
            page = repository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        } else if (categoria != null) {
            page = repository.findByCategoria(categoria, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::toResponseDTO);
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validarEstoque(dto.estoqueAtual());
        if (repository.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new ProdutoNomeDuplicadoException(dto.nome());
        }

        Produto produto = Produto.builder()
                .nome(dto.nome().trim())
                .categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual())
                .estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(dto.unidadeMedida().trim())
                .dataCadastro(LocalDate.now())
                .build();
        return toResponseDTO(repository.save(produto));
    }

    @Override
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        validarEstoque(dto.estoqueAtual());
        Produto produto = buscarEntidade(id);
        String nome = dto.nome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdProdutoNot(nome, id)) {
            throw new ProdutoNomeDuplicadoException(dto.nome());
        }

        produto.setNome(nome);
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(dto.unidadeMedida().trim());
        return toResponseDTO(repository.save(produto));
    }

    @Override
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    private void validarEstoque(Integer estoqueAtual) {
        if (estoqueAtual != null && estoqueAtual < 0) {
            throw new EstoqueNegativoException();
        }
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getIdProduto(), produto.getNome(), produto.getCategoria(),
                produto.getEstoqueAtual(), produto.getEstoqueMinimo(), produto.getUnidadeMedida(),
                produto.getDataCadastro(), produto.getEstoqueAtual() < produto.getEstoqueMinimo()
        );
    }
}
