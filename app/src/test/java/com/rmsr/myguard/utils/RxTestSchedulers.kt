package com.rmsr.myguard.utils

import io.reactivex.rxjava3.core.Scheduler

class RxTestSchedulers(val scheduler: Scheduler) : RxSchedulers {
    override fun io(): Scheduler = scheduler

    override fun computation(): Scheduler = scheduler

    override fun trampoline(): Scheduler = scheduler

    override fun mainThread(): Scheduler = scheduler

    override fun single(): Scheduler = scheduler
}