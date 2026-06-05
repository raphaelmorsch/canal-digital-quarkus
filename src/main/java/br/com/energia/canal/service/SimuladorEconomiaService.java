package br.com.energia.canal.service;

import br.com.energia.canal.dto.DicaEconomiaResponse;
import br.com.energia.canal.dto.SimulacaoEconomiaResponse;
import br.com.energia.canal.dto.SimuladorResumoResponse;
import br.com.energia.canal.entity.Cliente;
import br.com.energia.canal.entity.ConsumoMensal;
import br.com.energia.canal.entity.Fatura;
import br.com.energia.canal.exception.CanalException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SimuladorEconomiaService {

    public SimuladorResumoResponse resumo(Cliente cliente) {
        ConsumoMensal ultimo =
                ConsumoMensal.<ConsumoMensal>find("cliente", Sort.descending("referencia"), cliente).firstResult();
        Fatura ultimaFatura = Fatura.<Fatura>find("cliente", Sort.descending("referencia"), cliente).firstResult();

        if (ultimo == null || ultimaFatura == null) {
            throw CanalException.badRequest("Dados insuficientes para o simulador. Aguarde o histórico de consumo.");
        }

        BigDecimal tarifaMedia = tarifaPorKwh(ultimaFatura.valor, ultimaFatura.consumoKwh);
        return new SimuladorResumoResponse(
                ultimo.consumoKwh,
                ultimo.mediaRegiao,
                tarifaMedia,
                montarDicas(ultimo.consumoKwh, ultimo.mediaRegiao));
    }

    public SimulacaoEconomiaResponse simular(Cliente cliente, int reducaoPercentual) {
        if (reducaoPercentual < 1 || reducaoPercentual > 50) {
            throw CanalException.badRequest("Informe uma redução entre 1% e 50%.");
        }

        SimuladorResumoResponse resumo = resumo(cliente);
        int consumoSimulado = (int) Math.round(resumo.consumoAtualKwh() * (100 - reducaoPercentual) / 100.0);
        BigDecimal valorAtual = resumo.tarifaMediaKwh()
                .multiply(BigDecimal.valueOf(resumo.consumoAtualKwh()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorSimulado = resumo.tarifaMediaKwh()
                .multiply(BigDecimal.valueOf(consumoSimulado))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal economiaMensal = valorAtual.subtract(valorSimulado).max(BigDecimal.ZERO);

        return new SimulacaoEconomiaResponse(
                resumo.consumoAtualKwh(),
                consumoSimulado,
                valorAtual,
                valorSimulado,
                economiaMensal,
                economiaMensal.multiply(BigDecimal.valueOf(12)).setScale(2, RoundingMode.HALF_UP),
                resumo.dicas());
    }

    private List<DicaEconomiaResponse> montarDicas(int consumoAtual, int mediaRegiao) {
        List<DicaEconomiaResponse> dicas = new ArrayList<>();

        if (consumoAtual > mediaRegiao) {
            int diff = consumoAtual - mediaRegiao;
            dicas.add(new DicaEconomiaResponse(
                    "Consumo acima da média regional",
                    "Seu consumo está " + diff + " kWh acima da média da região. "
                            + "Revise equipamentos ligados em stand-by e o uso de ar-condicionado no horário de pico.",
                    "ALTA"));
        } else {
            dicas.add(new DicaEconomiaResponse(
                    "Consumo dentro da média",
                    "Seu consumo está alinhado ou abaixo da média regional. Mantenha hábitos eficientes.",
                    "BAIXA"));
        }

        dicas.add(new DicaEconomiaResponse(
                "Horário de pico (18h–21h)",
                "Evite usar chuveiro elétrico, ferro de passar e secadora nesse período para reduzir até 15% na tarifa.",
                "MEDIA"));
        dicas.add(new DicaEconomiaResponse(
                "Iluminação LED",
                "Substituir lâmpadas incandescentes por LED pode reduzir até 80% do consumo de iluminação.",
                "MEDIA"));
        dicas.add(new DicaEconomiaResponse(
                "Geladeira e freezer",
                "Mantenha a borracha de vedação em bom estado e evite abrir portas com frequência.",
                "BAIXA"));

        return dicas;
    }

    private BigDecimal tarifaPorKwh(BigDecimal valor, int consumoKwh) {
        if (consumoKwh <= 0) {
            return BigDecimal.ZERO;
        }
        return valor.divide(BigDecimal.valueOf(consumoKwh), 4, RoundingMode.HALF_UP);
    }
}
