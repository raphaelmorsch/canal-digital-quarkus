package br.com.energia.canal.service;

import br.com.energia.canal.dto.ConsumoResponse;
import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.entity.ConsumoMensal;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConsumoService {

    public List<ConsumoResponse> listar(Cliente cliente) {
        return ConsumoMensal.<ConsumoMensal>find("cliente", Sort.descending("referencia"), cliente)
                .list()
                .stream()
                .map(ConsumoResponse::from)
                .toList();
    }
}
