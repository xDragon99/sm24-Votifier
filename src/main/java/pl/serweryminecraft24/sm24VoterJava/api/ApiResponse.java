package pl.serweryminecraft24.sm24VoterJava.api;

import java.util.Objects;

/**
 * Reprezentuje standardową odpowiedź z API.
 * Jest to klasyczna klasa (POJO), odpowiednik rekordu dla Javy 8.
 */
public final class ApiResponse {

    private final boolean success;
    private final String message;


    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse that = (ApiResponse) o;
        return success == that.success && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}