package pl.serweryminecraft24.sm24VoterJava.api;

public record ApiResponse(boolean success, String message) {
    public ApiResponse {
        message = message != null ? message : "";
    }
}
