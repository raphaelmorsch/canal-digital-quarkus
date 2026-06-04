package br.com.energia.canal.resource;

import br.com.energia.canal.dto.NotificacaoResponse;
import br.com.energia.canal.service.ClienteContextService;
import br.com.energia.canal.service.NotificacaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/notificacoes")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Notificações")
public class NotificacaoResource {

    @Inject
    ClienteContextService clienteContext;

    @Inject
    NotificacaoService notificacaoService;

    @GET
    public List<NotificacaoResponse> listar(@HeaderParam("Authorization") String authorization) {
        return notificacaoService.listar(clienteContext.requireCliente(authorization));
    }

    @POST
    @Path("/{id}/ler")
    public NotificacaoResponse marcarLida(
            @HeaderParam("Authorization") String authorization, @PathParam("id") Long id) {
        return notificacaoService.marcarComoLida(clienteContext.requireCliente(authorization), id);
    }
}
