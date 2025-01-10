package com.example.enigma.authentication;

public record
AuthenticateRequest(
        String email,
        String password) {
}
