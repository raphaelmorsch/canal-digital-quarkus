package br.com.energia.canal.service;

import br.com.energia.canal.dto.ClienteResponse;
import br.com.energia.canal.entity.Cliente;

public final class ClienteMapper {

    private ClienteMapper() {}

    public static ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
                c.id,
                c.cpf,
                c.nome,
                c.email,
                c.telefone,
                c.numeroInstalacao,
                c.endereco,
                c.cidade,
                c.uf,
                c.cep,
                c.tipoTarifa
        );
    }
}
