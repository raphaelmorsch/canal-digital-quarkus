package br.com.energia.canal.resource;

import br.com.energia.canal.config.FeatureToggleService;
import br.com.energia.canal.dto.SimulacaoEconomiaRequest;
import br.com.energia.canal.dto.SimulacaoEconomiaResponse;
import br.com.energia.canal.dto.SimuladorResumoResponse;
import br.com.energia.canal.service.ClienteContextService;
import br.com.energia.canal.service.SimuladorEconomiaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/simulador-economia")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulador de Economia")
public class SimuladorEconomiaResource {

    @Inject
    FeatureToggleService features;

    @Inject
    ClienteContextService clienteContext;

    @Inject
    SimuladorEconomiaService simuladorService;

    @GET
    @Path("/resumo")
    public SimuladorResumoResponse resumo(@HeaderParam("Authorization") String authorization) {
        features.requireSimuladorEconomia();
        return simuladorService.resumo(clienteContext.requireCliente(authorization));
    }

    @POST
    @Path("/simular")
    public SimulacaoEconomiaResponse simular(
            @HeaderParam("Authorization") String authorization, @Valid SimulacaoEconomiaRequest request) {
        features.requireSimuladorEconomia();
        return simuladorService.simular(clienteContext.requireCliente(authorization), request.reducaoPercentual());
    }
}
