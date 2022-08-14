package com.rmsr.myguard.data.mapper

/**
 * Useful Util interface extended [Mapper] to add reverse functionality,
 * witch make this TwoWay mapping interface.
 * @see Mapper
 */
interface ReversibleMapper<T, R> : Mapper<T, R> {

    /**
     * Map [R] by reversing it back to [T].
     * @param input source to convert of type [R].
     * @return input after reversed to new [T].
     */
    fun mapReversed(input: R): T

    /**
     * Map [Iterable] collection of type [R] by by reversing it back to [List] of [T].
     * @param input source to convert of type [Iterable]<[R]>.
     * @return input after reversed back to new [List]<[T]>.
     */
    fun mapReversed(input: Iterable<R>): List<T> = input.map { this.mapReversed(it) }
}
