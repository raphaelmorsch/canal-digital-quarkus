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
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "faturas")
public class Fatura extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    public Cliente cliente;

    @Column(name = "referencia", nullable = false, length = 7)
    public String referencia;

    @Column(name = "data_vencimento", nullable = false)
    public LocalDate dataVencimento;

    @Column(name = "data_emissao", nullable = false)
    public LocalDate dataEmissao;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal valor;

    @Column(name = "consumo_kwh", nullable = false)
    public int consumoKwh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusFatura status;

    @Column(name = "codigo_barras")
    public String codigoBarras;

    public enum StatusFatura {
        PENDENTE,
        PAGA,
        VENCIDA
    }
}
