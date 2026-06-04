package br.com.energia.canal.exception;

import br.com.energia.canal.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class CanalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof CanalException canal) {
            return Response.status(canal.getStatus())
                    .entity(ErrorResponse.of(canal.getMessage()))
                    .build();
        }
        if (exception instanceof ConstraintViolationException cve) {
            List<String> details = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            return Response.status(400).entity(ErrorResponse.of("Dados inválidos", details)).build();
        }
        return Response.status(500)
                .entity(ErrorResponse.of("Erro interno do servidor"))
                .build();
    }
}
