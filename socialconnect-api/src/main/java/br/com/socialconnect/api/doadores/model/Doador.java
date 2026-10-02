package br.com.socialconnect.api.doadores.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doador")
    private Long idDoador;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoDoador tipo;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;
}
