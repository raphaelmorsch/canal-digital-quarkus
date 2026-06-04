package br.com.energia.canal.service;

import br.com.energia.canal.dto.ConsumoResponse;
import br.com.energia.canal.dto.DashboardResponse;
import br.com.energia.canal.dto.FaturaResponse;
import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.entity.ConsumoMensal;
import br.com.energia.canal.entity.Fatura;
import br.com.energia.canal.entity.Notificacao;
import br.com.energia.canal.entity.Solicitacao;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class DashboardService {

    public DashboardResponse montar(Cliente cliente) {
        List<Fatura> faturas = Fatura.find("cliente", cliente).list();
        List<Fatura> pendentes = faturas.stream()
                .filter(f -> f.status == Fatura.StatusFatura.PENDENTE || f.status == Fatura.StatusFatura.VENCIDA)
                .toList();

        BigDecimal totalPendente = pendentes.stream()
                .map(f -> f.valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Fatura proxima = pendentes.stream()
                .min(Comparator.comparing(f -> f.dataVencimento))
                .orElse(null);

        List<ConsumoMensal> consumos = ConsumoMensal.find("cliente", Sort.descending("referencia"), cliente)
                .range(0, 5)
                .list();

        ConsumoMensal ultimo = consumos.isEmpty() ? null : consumos.get(0);
        int consumoAtual = ultimo != null ? ultimo.consumoKwh : 0;
        int mediaRegiao = ultimo != null ? ultimo.mediaRegiao : 0;

        long solicitacoesAbertas = Solicitacao.count(
                "cliente = ?1 and status in (?2, ?3)",
                cliente,
                Solicitacao.StatusSolicitacao.ABERTA,
                Solicitacao.StatusSolicitacao.EM_ANDAMENTO);

        long notificacoesNaoLidas = Notificacao.count("cliente = ?1 and lida = false", cliente);

        List<ConsumoResponse> historico =
                consumos.stream().map(ConsumoResponse::from).toList();

        return new DashboardResponse(
                ClienteMapper.toResponse(cliente),
                proxima != null ? FaturaResponse.from(proxima) : null,
                totalPendente,
                pendentes.size(),
                consumoAtual,
                mediaRegiao,
                (int) solicitacoesAbertas,
                (int) notificacoesNaoLidas,
                historico);
    }
}
