package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.errors.ErrorEntity

interface ErrorHandler {
    fun getError(throwable: Throwable): ErrorEntity
}