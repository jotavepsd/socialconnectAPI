package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    @Mock
    private BeneficiarioRepository repository;

    @InjectMocks
    private BeneficiarioService service;

    @Test
    @DisplayName("Deve criar beneficiário com CPF normalizado e data de cadastro de hoje")
    void deveCriarBeneficiarioQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva", "529.982.247-25", "11999999999", "Rua das Flores, 123", "Renda familiar baixa"
        );
        Mockito.when(repository.existsByCpf("52998224725")).thenReturn(false);
        Mockito.when(repository.save(Mockito.any())).thenAnswer(inv -> {
            Beneficiario b = inv.getArgument(0);
            b.setIdBeneficiario(1L);
            return b;
        });

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        BeneficiarioResponseDTO resultado = service.criar(dto);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        ArgumentCaptor<Beneficiario> captor = ArgumentCaptor.forClass(Beneficiario.class);
        Mockito.verify(repository, Mockito.times(1)).save(captor.capture());
        Assertions.assertEquals("52998224725", captor.getValue().getCpf());
        Assertions.assertEquals(LocalDate.now(), captor.getValue().getDataCadastro());
        Assertions.assertEquals(1L, resultado.idBeneficiario());
    }

    @Test
    @DisplayName("Deve lançar CpfDuplicadoException quando o CPF já existir")
    void deveLancarExcecaoQuandoCpfDuplicado() {
        // ARRANGE
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO("Maria", "52998224725", null, null, null);
        Mockito.when(repository.existsByCpf("52998224725")).thenReturn(true);

        // ACT + ASSERT
        Assertions.assertThrows(CpfDuplicadoException.class, () -> service.criar(dto));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("PATCH deve alterar apenas os campos enviados")
    void deveAtualizarSomenteCamposEnviadosNoPatch() {
        // ARRANGE
        Beneficiario existente = Beneficiario.builder()
                .idBeneficiario(1L).nome("Maria").cpf("52998224725")
                .telefone("11999999999").endereco("Rua A").dataCadastro(LocalDate.of(2026, 9, 1))
                .build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
        BeneficiarioPatchDTO patch = new BeneficiarioPatchDTO(null, "11988887777", null, null);

        // ACT
        BeneficiarioResponseDTO resultado = service.atualizarParcial(1L, patch);

        // ASSERT
        Assertions.assertEquals("11988887777", resultado.telefone());
        Assertions.assertEquals("Maria", resultado.nome());
        Assertions.assertEquals("Rua A", resultado.endereco());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao deletar ID inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        // ARRANGE
        Mockito.when(repository.existsById(99L)).thenReturn(false);

        // ACT + ASSERT
        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(99L));
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.any());
    }
}
