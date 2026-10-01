package com.elanor.common.response;

import java.time.Instant;
import java.util.Map;

public class ApiErrorResponse {
    private boolean success = false;
    private ErrorDetail error;
    private String traceId;
    private Instant timestamp;

    public ApiErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ApiErrorResponse(boolean success, ErrorDetail error, String traceId, Instant timestamp) {
        this.success = success;
        this.error = error;
        this.traceId = traceId;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static class ErrorDetail {
        private String code;
        private String message;
        private Map<String, Object> details;

        public ErrorDetail() {
        }

        public ErrorDetail(String code, String message, Map<String, Object> details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getDetails() {
            return details;
        }

        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }
    }

    public static ApiErrorResponse of(String code, String message, Map<String, Object> details, String traceId) {
        return new ApiErrorResponse(
                false,
                new ErrorDetail(code, message, details),
                traceId,
                Instant.now()
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ErrorDetail getError() {
        return error;
    }

    public void setError(ErrorDetail error) {
        this.error = error;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
