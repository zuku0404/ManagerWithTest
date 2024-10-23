package com.example.enigma.task_2.exception.user;


public class EmailAlreadyExist extends RuntimeException{
    public EmailAlreadyExist(String message){
        super(message);
    }
}
