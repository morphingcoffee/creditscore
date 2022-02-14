package com.morphingcoffee.credit_donut.core.model

/** Sealed class encapsulating successful or failed result **/
sealed class Result<out V, out E> {
    /** Successful result containing value of type [V] **/
    data class Value<out V>(val value: V) : Result<V, Nothing>()

    /** Failed result containing error of type [E] **/
    data class Error<out E>(val error: E) : Result<Nothing, E>()
}