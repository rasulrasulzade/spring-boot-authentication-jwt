package com.company.springbootauthenticationjwt.exception;

public class EmailAlreadyInUseException extends Exception{
    private int status;

    public EmailAlreadyInUseException(String message, int status){
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
