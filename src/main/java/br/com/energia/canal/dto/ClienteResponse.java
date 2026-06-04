package br.com.energia.canal.dto;

public record ClienteResponse(
        Long id,
        String cpf,
        String nome,
        String email,
        String telefone,
        String numeroInstalacao,
        String endereco,
        String cidade,
        String uf,
        String cep,
        String tipoTarifa
) {}
