package br.com.energia.canal.security;

import br.com.energia.canal.entity.Cliente;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessaoService {

    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    public String criarToken(Cliente cliente) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, cliente.id);
        return token;
    }

    public Optional<Long> resolverClienteId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String bearer = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
        return Optional.ofNullable(tokens.get(bearer));
    }

    public void invalidar(String token) {
        if (token != null) {
            String bearer = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
            tokens.remove(bearer);
        }
    }
}
