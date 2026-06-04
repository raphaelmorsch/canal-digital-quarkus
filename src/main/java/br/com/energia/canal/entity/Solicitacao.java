package br.com.energia.canal.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes")
public class Solicitacao extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    public Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public TipoSolicitacao tipo;

    @Column(nullable = false, length = 500)
    public String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusSolicitacao status;

    @Column(name = "criado_em", nullable = false)
    public LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    public LocalDateTime atualizadoEm;

    @Column(name = "protocolo", unique = true)
    public String protocolo;

    public enum TipoSolicitacao {
        RELIGACAO,
        SEGUNDA_VIA,
        TROCA_TITULARIDADE,
        RECLAMACAO,
        INFORMACAO_CONSUMO,
        OUTROS
    }

    public enum StatusSolicitacao {
        ABERTA,
        EM_ANDAMENTO,
        CONCLUIDA,
        CANCELADA
    }
}
