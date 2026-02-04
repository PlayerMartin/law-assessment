package com.example.assessment.utils.Result;

public sealed interface Result<T, E> permits Ok, Err {
    boolean isOk();
    T unwrap();
    E unwrapErr();
}