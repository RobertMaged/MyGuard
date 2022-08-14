package com.rmsr.myguard.data.mapper

/**
 * Useful Util interface can be extended to convert one specific object of type [T],
 * to new object of type [R].
 * @see ReversibleMapper
 */

interface Mapper<T, R> {

    /**
     * Map [T] by converting it to [R].
     * @param input source to convert of type [T].
     * @return input after converted to new [R].
     */
    fun map(input: T): R

    /**
     * Map [Iterable] collection of type [T] by converting it to [List] of [R].
     * @param input source to convert of type [Iterable]<[T]>.
     * @return input after converted to new [List]<[R]>.
     */
    fun map(input: Iterable<T>): List<R> = input.map { this.map(it) }
}