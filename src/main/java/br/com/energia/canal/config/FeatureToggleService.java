package br.com.energia.canal.config;

import br.com.energia.canal.exception.CanalException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FeatureToggleService {

    @ConfigProperty(name = "canal.feature.simulador-economia.enabled", defaultValue = "false")
    String simuladorEconomiaEnabledRaw;

    public boolean isSimuladorEconomiaEnabled() {
        return parseTruthy(simuladorEconomiaEnabledRaw);
    }

    public String getSimuladorEconomiaConfigRaw() {
        return simuladorEconomiaEnabledRaw;
    }

    public void requireSimuladorEconomia() {
        if (!isSimuladorEconomiaEnabled()) {
            throw CanalException.notFound("Simulador de Economia está desabilitado.");
        }
    }

    static boolean parseTruthy(String value) {
        if (value == null) {
            return false;
        }
        return switch (value.trim().toLowerCase()) {
            case "true", "1", "yes", "on" -> true;
            default -> false;
        };
    }
}
