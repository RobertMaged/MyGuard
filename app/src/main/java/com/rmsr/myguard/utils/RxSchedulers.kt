package com.rmsr.myguard.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

interface RxSchedulers {
    fun io(): Scheduler
    fun computation(): Scheduler
    fun trampoline(): Scheduler
    fun mainThread(): Scheduler
    fun single(): Scheduler
}

class MainRxSchedulers2 @Inject constructor() : RxSchedulers {
    override fun io(): Scheduler = Schedulers.io()

    override fun computation(): Scheduler = Schedulers.computation()

    override fun trampoline(): Scheduler = Schedulers.trampoline()

    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()

    override fun single(): Scheduler = Schedulers.single()
}