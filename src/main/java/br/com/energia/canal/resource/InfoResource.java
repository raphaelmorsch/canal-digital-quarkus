package br.com.energia.canal.resource;

import br.com.energia.canal.entity.Cliente;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/info")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Info")
public class InfoResource {

    private static final String BUILD_ID = "2026-06-05-demo-loader-v2";

    @GET
    public Map<String, Object> info() {
        return Map.of(
                "buildId", BUILD_ID,
                "dataLoader", "DemoDataLoader (Java)",
                "importSql", false,
                "clientesCadastrados", Cliente.count());
    }
}
