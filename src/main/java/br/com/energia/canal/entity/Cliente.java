package br.com.energia.canal.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String cpf;

    @Column(nullable = false)
    public String nome;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String senha;

    @Column(nullable = false)
    public String telefone;

    @Column(name = "numero_instalacao", nullable = false, unique = true)
    public String numeroInstalacao;

    @Column(nullable = false)
    public String endereco;

    @Column(nullable = false)
    public String cidade;

    @Column(nullable = false, length = 2)
    public String uf;

    @Column(nullable = false)
    public String cep;

    @Column(nullable = false)
    public String tipoTarifa;

    @Column(nullable = false)
    public boolean ativo = true;
}
