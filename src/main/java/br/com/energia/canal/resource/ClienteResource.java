package br.com.energia.canal.resource;

import br.com.energia.canal.dto.ClienteResponse;
import br.com.energia.canal.service.ClienteContextService;
import br.com.energia.canal.service.ClienteMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/cliente")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Cliente")
public class ClienteResource {

    @Inject
    ClienteContextService clienteContext;

    @GET
    @Path("/me")
    public ClienteResponse me(@HeaderParam("Authorization") String authorization) {
        return ClienteMapper.toResponse(clienteContext.requireCliente(authorization));
    }
}
