package com.atherton.upnext.util.threading

import io.reactivex.Scheduler

data class RxSchedulers(val io: Scheduler, val main: Scheduler)