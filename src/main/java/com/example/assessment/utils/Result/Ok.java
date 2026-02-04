package com.example.assessment.utils.Result;

public record Ok<T, E>(T value) implements Result<T, E> {
    public boolean isOk() { return true; }
    public T unwrap() { return value; }
    public E unwrapErr() { throw new IllegalStateException(); }
}
