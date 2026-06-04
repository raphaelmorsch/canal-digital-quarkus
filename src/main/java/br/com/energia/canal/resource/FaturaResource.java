package br.com.energia.canal.resource;

import br.com.energia.canal.dto.FaturaResponse;
import br.com.energia.canal.dto.PagamentoRequest;
import br.com.energia.canal.service.ClienteContextService;
import br.com.energia.canal.service.FaturaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/faturas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Faturas")
public class FaturaResource {

    @Inject
    ClienteContextService clienteContext;

    @Inject
    FaturaService faturaService;

    @GET
    public List<FaturaResponse> listar(@HeaderParam("Authorization") String authorization) {
        return faturaService.listar(clienteContext.requireCliente(authorization));
    }

    @GET
    @Path("/{id}")
    public FaturaResponse buscar(
            @HeaderParam("Authorization") String authorization, @PathParam("id") Long id) {
        return faturaService.buscar(clienteContext.requireCliente(authorization), id);
    }

    @POST
    @Path("/{id}/pagar")
    public FaturaResponse pagar(
            @HeaderParam("Authorization") String authorization,
            @PathParam("id") Long id,
            @Valid PagamentoRequest request) {
        return faturaService.pagar(clienteContext.requireCliente(authorization), id, request);
    }
}
