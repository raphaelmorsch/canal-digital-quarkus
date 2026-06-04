package br.com.energia.canal.service;

import br.com.energia.canal.dto.LoginRequest;
import br.com.energia.canal.dto.LoginResponse;
import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.exception.CanalException;
import br.com.energia.canal.security.SessaoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    SessaoService sessaoService;

    public LoginResponse login(LoginRequest request) {
        String id = request.identificador().trim().toLowerCase();
        String cpfNumeros = id.replaceAll("\\D", "");
        Cliente cliente = Cliente.find("lower(email) = ?1 or cpf = ?2", id, cpfNumeros.isEmpty() ? id : cpfNumeros)
                .firstResult();
        if (cliente == null || !cliente.senha.equals(request.senha())) {
            throw CanalException.unauthorized("CPF/e-mail ou senha inválidos");
        }
        if (!cliente.ativo) {
            throw CanalException.unauthorized("Conta desativada. Entre em contato com o atendimento.");
        }
        String token = sessaoService.criarToken(cliente);
        return new LoginResponse(token, ClienteMapper.toResponse(cliente));
    }

    public void logout(String authorization) {
        sessaoService.invalidar(authorization);
    }
}
