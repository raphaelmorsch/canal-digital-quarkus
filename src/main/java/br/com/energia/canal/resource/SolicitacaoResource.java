package br.com.energia.canal.resource;

import br.com.energia.canal.dto.SolicitacaoRequest;
import br.com.energia.canal.dto.SolicitacaoResponse;
import br.com.energia.canal.service.ClienteContextService;
import br.com.energia.canal.service.SolicitacaoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/solicitacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Solicitações")
public class SolicitacaoResource {

    @Inject
    ClienteContextService clienteContext;

    @Inject
    SolicitacaoService solicitacaoService;

    @GET
    public List<SolicitacaoResponse> listar(@HeaderParam("Authorization") String authorization) {
        return solicitacaoService.listar(clienteContext.requireCliente(authorization));
    }

    @POST
    public SolicitacaoResponse criar(
            @HeaderParam("Authorization") String authorization, @Valid SolicitacaoRequest request) {
        return solicitacaoService.criar(clienteContext.requireCliente(authorization), request);
    }
}
