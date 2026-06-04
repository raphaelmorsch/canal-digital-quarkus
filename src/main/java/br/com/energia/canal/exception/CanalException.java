package br.com.energia.canal.exception;

public class CanalException extends RuntimeException {

    private final int status;

    public CanalException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static CanalException unauthorized(String message) {
        return new CanalException(message, 401);
    }

    public static CanalException notFound(String message) {
        return new CanalException(message, 404);
    }

    public static CanalException badRequest(String message) {
        return new CanalException(message, 400);
    }
}
