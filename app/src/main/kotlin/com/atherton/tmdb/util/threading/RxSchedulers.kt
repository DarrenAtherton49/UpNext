package com.atherton.tmdb.util.threading

import io.reactivex.Scheduler

data class RxSchedulers(val io: Scheduler, val main: Scheduler)