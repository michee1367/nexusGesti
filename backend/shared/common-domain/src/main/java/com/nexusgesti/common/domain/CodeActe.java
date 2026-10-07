package com.nexusgesti.common.domain;

public record CodeActe(String code) {

    public CodeActe {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("codeActe is required");
        }
    }

    public static CodeActe of(String code) {
        return new CodeActe(code);
    }

    @Override
    public String toString() {
        return code;
    }
}