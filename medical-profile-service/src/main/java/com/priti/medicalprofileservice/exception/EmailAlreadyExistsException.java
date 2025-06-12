package com.priti.medicalprofileservice.exception;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String message) { super(message); }
}
