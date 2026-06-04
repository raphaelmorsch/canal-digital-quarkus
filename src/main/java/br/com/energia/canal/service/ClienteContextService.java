package br.com.energia.canal.service;

import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.exception.CanalException;
import br.com.energia.canal.security.SessaoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ClienteContextService {

    @Inject
    SessaoService sessaoService;

    public Cliente requireCliente(String authorization) {
        Long clienteId = sessaoService
                .resolverClienteId(authorization)
                .orElseThrow(() -> CanalException.unauthorized("Sessão inválida ou expirada"));
        Cliente cliente = Cliente.findById(clienteId);
        if (cliente == null || !cliente.ativo) {
            throw CanalException.unauthorized("Sessão inválida ou expirada");
        }
        return cliente;
    }
}
