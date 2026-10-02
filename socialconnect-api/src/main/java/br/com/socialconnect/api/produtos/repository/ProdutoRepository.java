package br.com.socialconnect.api.produtos.repository;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Produto> findByCategoria(CategoriaProduto categoria, Pageable pageable);
    Page<Produto> findByNomeContainingIgnoreCaseAndCategoria(String nome, CategoriaProduto categoria, Pageable pageable);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdProdutoNot(String nome, Long idProduto);
}
