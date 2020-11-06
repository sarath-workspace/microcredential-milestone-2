package com.cognizant.microcredential.emailotpservice.model;

public class MailResponse {

    private String success;

    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Builder {
        private final MailResponse mailResponse = new MailResponse();

        public Builder success(String success) {
            this.mailResponse.success = success;
            return this;
        }

        public Builder message(String message) {
            this.mailResponse.message = message;
            return this;
        }

        public MailResponse build() {
            return mailResponse;
        }
    }
}
