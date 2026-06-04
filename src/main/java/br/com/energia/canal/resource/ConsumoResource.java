package br.com.energia.canal.resource;

import br.com.energia.canal.dto.ConsumoResponse;
import br.com.energia.canal.service.ClienteContextService;
import br.com.energia.canal.service.ConsumoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/consumo")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Consumo")
public class ConsumoResource {

    @Inject
    ClienteContextService clienteContext;

    @Inject
    ConsumoService consumoService;

    @GET
    public List<ConsumoResponse> listar(@HeaderParam("Authorization") String authorization) {
        return consumoService.listar(clienteContext.requireCliente(authorization));
    }
}
