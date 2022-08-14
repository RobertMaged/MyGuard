package com.rmsr.myguard.domain.utils


sealed class Validator<out T, out E> {
    object Valid : Validator<Nothing, Nothing>()

    data class AutoCorrected<T>(val correctedData: T) : Validator<T, Nothing>()

    data class Invalid<E>(val exception: E) : Validator<Nothing, E>()

}