package com.atherton.tmdb.util.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected abstract val layoutResId: Int

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        initInjection()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       return inflater.inflate(layoutResId, container, false)
    }

    protected abstract fun initInjection()
}