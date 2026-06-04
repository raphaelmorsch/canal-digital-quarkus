package br.com.energia.canal.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class Notificacao extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    public Cliente cliente;

    @Column(nullable = false, length = 200)
    public String titulo;

    @Column(nullable = false, length = 1000)
    public String mensagem;

    @Column(name = "criado_em", nullable = false)
    public LocalDateTime criadoEm;

    @Column(nullable = false)
    public boolean lida = false;
}
