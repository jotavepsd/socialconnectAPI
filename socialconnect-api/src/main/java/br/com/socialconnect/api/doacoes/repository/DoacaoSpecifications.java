package br.com.socialconnect.api.doacoes.repository;

import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

// Filtros dinâmicos: cada filtro nulo vira "sem restrição"
public final class DoacaoSpecifications {

    private DoacaoSpecifications() {
    }

    public static Specification<Doacao> filtrar(LocalDate dataInicio, LocalDate dataFim, TipoDoacao tipo) {
        return (root, query, cb) -> {
            var predicado = cb.conjunction();
            if (dataInicio != null) {
                predicado = cb.and(predicado, cb.greaterThanOrEqualTo(root.get("dataDoacao"), dataInicio));
            }
            if (dataFim != null) {
                predicado = cb.and(predicado, cb.lessThanOrEqualTo(root.get("dataDoacao"), dataFim));
            }
            if (tipo != null) {
                predicado = cb.and(predicado, cb.equal(root.get("tipo"), tipo));
            }
            return predicado;
        };
    }
}
