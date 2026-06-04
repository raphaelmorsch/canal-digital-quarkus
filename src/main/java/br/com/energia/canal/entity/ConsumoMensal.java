package br.com.energia.canal.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "consumo_mensal")
public class ConsumoMensal extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    public Cliente cliente;

    @Column(nullable = false, length = 7)
    public String referencia;

    @Column(name = "consumo_kwh", nullable = false)
    public int consumoKwh;

    @Column(name = "media_regiao")
    public int mediaRegiao;
}
