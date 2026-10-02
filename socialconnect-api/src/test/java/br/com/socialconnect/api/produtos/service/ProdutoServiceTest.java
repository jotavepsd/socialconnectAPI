package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.ProdutoNomeDuplicadoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
    @Mock private ProdutoRepository repository;
    @InjectMocks private ProdutoServiceImpl service;

    @Test
    void deveCriarProdutoQuandoDadosValidos() {
        // ARRANGE
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade");
        Produto salvo = Produto.builder().idProduto(1L).nome("Arroz 5kg").categoria(CategoriaProduto.ALIMENTO)
                .estoqueAtual(3).estoqueMinimo(10).unidadeMedida("unidade").dataCadastro(LocalDate.now()).build();
        Mockito.when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(false);
        Mockito.when(repository.save(Mockito.any(Produto.class))).thenReturn(salvo);

        // ACT
        ProdutoResponseDTO resultado = service.criar(dto);

        // ASSERT
        Assertions.assertEquals(1L, resultado.idProduto());
        Assertions.assertTrue(resultado.estoqueBaixo());
        Mockito.verify(repository).save(Mockito.any(Produto.class));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // ARRANGE
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, -1, 10, "unidade");

        // ACT + ASSERT
        Assertions.assertThrows(EstoqueNegativoException.class, () -> service.criar(dto));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // ARRANGE
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade");
        Mockito.when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(true);

        // ACT + ASSERT
        Assertions.assertThrows(ProdutoNomeDuplicadoException.class, () -> service.criar(dto));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveCalcularEstoqueBaixoQuandoAtualMenorQueMinimo() {
        // ARRANGE
        Produto produto = Produto.builder().idProduto(1L).nome("Arroz 5kg").categoria(CategoriaProduto.ALIMENTO)
                .estoqueAtual(3).estoqueMinimo(10).unidadeMedida("unidade").dataCadastro(LocalDate.now()).build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(produto));

        // ACT
        ProdutoResponseDTO resultado = service.buscarPorId(1L);

        // ASSERT
        Assertions.assertTrue(resultado.estoqueBaixo());
    }
}
