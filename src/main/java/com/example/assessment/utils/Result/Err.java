package com.example.assessment.utils.Result;

public record Err<T, E>(E error) implements Result<T, E> {
    public boolean isOk() { return false; }
    public T unwrap() { throw new IllegalStateException(); }
    public E unwrapErr() { return error; }
}
