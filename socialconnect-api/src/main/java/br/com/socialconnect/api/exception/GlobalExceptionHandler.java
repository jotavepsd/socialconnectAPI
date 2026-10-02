package br.com.socialconnect.api.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE_TYPE = "https://socialconnect.api/errors/";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // 400 - Bean Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<ProblemDetail.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ProblemDetail.FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "validacao", "Erro de validação",
                mensagem("erro.validacao"), request, errors);
    }

    // 400 - JSON malformado ou valor de enum/data inválido no corpo
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleJsonInvalido(HttpMessageNotReadableException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "requisicao-invalida", "Requisição inválida",
                mensagem("erro.json.invalido"), request, List.of());
    }

    // 400 - parâmetro de URL/query com tipo errado (ex: id=abc, tipo=XYZ)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTipoParametro(MethodArgumentTypeMismatchException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "parametro-invalido", "Parâmetro inválido",
                mensagem("erro.parametro.invalido", ex.getName()), request, List.of());
    }

    // 400 - ordenação por campo inexistente (ex: sort=abc)
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ProblemDetail> handleOrdenacaoInvalida(PropertyReferenceException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "ordenacao-invalida", "Ordenação inválida",
                mensagem("erro.ordenacao.invalida", ex.getPropertyName()), request, List.of());
    }

    // 400 - parâmetros de paginação/ordenação inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleArgumentoInvalido(IllegalArgumentException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, "parametro-invalido", "Parâmetro inválido",
                ex.getMessage(), request, List.of());
    }

    // 409 - CPF duplicado
    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleCpfDuplicado(CpfDuplicadoException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, "cpf-duplicado", "CPF já cadastrado",
                mensagem("cpf.duplicado", ex.getCpf()), request, List.of());
    }

    // 409 - nome de produto duplicado
    @ExceptionHandler(ProdutoNomeDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleProdutoDuplicado(ProdutoNomeDuplicadoException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, "produto-duplicado", "Produto já cadastrado",
                mensagem("produto.nome.duplicado", ex.getNome()), request, List.of());
    }

    // 422 - estoque atual negativo
    @ExceptionHandler(EstoqueNegativoException.class)
    public ResponseEntity<ProblemDetail> handleEstoqueNegativo(EstoqueNegativoException ex, WebRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "estoque-negativo", "Estoque inválido",
                mensagem("produto.estoque.negativo"), request, List.of());
    }

    // 404 - recurso não encontrado
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleNaoEncontrado(RecursoNaoEncontradoException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, "nao-encontrado", "Recurso não encontrado",
                ex.getMessage(), request, List.of());
    }

    // 500 - erro inesperado (sem expor stack trace).
    // Exceções do Spring MVC que já carregam um status (405, 404 de rota, 415...) mantêm esse status.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenerico(Exception ex, WebRequest request) {
        if (ex instanceof ErrorResponse erro) {
            return build(erro.getStatusCode(), "requisicao-invalida", "Requisição inválida",
                    mensagem("erro.requisicao", erro.getStatusCode().value()), request, List.of());
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "erro-interno", "Erro interno do servidor",
                mensagem("erro.interno"), request, List.of());
    }

    private ResponseEntity<ProblemDetail> build(HttpStatusCode status, String type, String title, String detail,
                                                WebRequest request, List<ProblemDetail.FieldError> errors) {
        ProblemDetail problem = new ProblemDetail(
                BASE_TYPE + type,
                title,
                status.value(),
                detail,
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(status).body(problem);
    }

    private String mensagem(String chave, Object... args) {
        return messageSource.getMessage(chave, args, LocaleContextHolder.getLocale());
    }
}
