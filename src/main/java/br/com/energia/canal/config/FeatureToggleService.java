package br.com.energia.canal.config;

import br.com.energia.canal.exception.CanalException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FeatureToggleService {

    @ConfigProperty(name = "canal.feature.simulador-economia.enabled", defaultValue = "false")
    boolean simuladorEconomiaEnabled;

    public boolean isSimuladorEconomiaEnabled() {
        return simuladorEconomiaEnabled;
    }

    public void requireSimuladorEconomia() {
        if (!simuladorEconomiaEnabled) {
            throw CanalException.notFound("Simulador de Economia está desabilitado.");
        }
    }
}
