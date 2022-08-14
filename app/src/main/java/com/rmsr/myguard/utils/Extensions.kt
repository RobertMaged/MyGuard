package com.rmsr.myguard.utils

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Timed
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

/**
 * Check if response List is empty then call [Maybe.OnComplete()],
 * else complete stream as it is with [Maybe.OnSuccess(T)].
 *
 * This is useful with Room library, because if the DB Query return type is List<T>,
 * when no result found Room just call [Maybe.OnSuccess(T)] with an empty list '[ ]'
 * and did not fire [Maybe.OnComplete()].
 */
fun <T : Iterable<*>> Maybe<T>.onEmptyListComplete(): Maybe<T> =
    mapOptional { if (it.any()) Optional.of(it) else Optional.empty() }


fun <T : Map<*, *>> Maybe<T>.onEmptyMapComplete(): Maybe<T> =
    flatMap { if (it.any()) Maybe.just(it) else Maybe.empty() }

fun <T : Any> Maybe<T>.logTimeInterval(tag: String, msg: String): Maybe<T> =
    timeInterval()
        .map {
            MyLog.d(tag, "$msg= ${it.time()}", logThreadName = true)
            return@map it.value()
        }

fun <T : Any> Flowable<T>.logTimeInterval(tag: String, msg: String): Flowable<T> =
    timeInterval()
        .map {
            MyLog.d(tag, "$msg= ${it.time()}", logThreadName = true)
            return@map it.value()
        }

operator fun <T : Any> AtomicReference<T>.getValue(thisRef: Any?, property: KProperty<*>): T =
    this.get()

operator fun <T : Any> AtomicReference<T>.setValue(
    thisRef: Any?,
    property: KProperty<*>,
    value: T,
) =
    this.set(value)


operator fun AtomicBoolean.getValue(thisRef: Any?, property: KProperty<*>): Boolean =
    this.get()

operator fun AtomicBoolean.setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) =
    this.set(value)

operator fun <T> Timed<T>.component1(): Long = time()
operator fun <T> Timed<T>.component2(): T = value()