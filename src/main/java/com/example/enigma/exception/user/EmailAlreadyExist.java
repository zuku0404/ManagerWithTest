package com.example.enigma.exception.user;


public class EmailAlreadyExist extends RuntimeException{
    public EmailAlreadyExist(String message){
        super(message);
    }
}
