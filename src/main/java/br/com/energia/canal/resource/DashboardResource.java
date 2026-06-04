package br.com.energia.canal.resource;

import br.com.energia.canal.dto.DashboardResponse;
import br.com.energia.canal.service.ClienteContextService;
import br.com.energia.canal.service.DashboardService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Dashboard")
public class DashboardResource {

    @Inject
    ClienteContextService clienteContext;

    @Inject
    DashboardService dashboardService;

    @GET
    public DashboardResponse dashboard(@HeaderParam("Authorization") String authorization) {
        return dashboardService.montar(clienteContext.requireCliente(authorization));
    }
}
