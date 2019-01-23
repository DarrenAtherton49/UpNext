package com.atherton.tmdb.util.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseMvpActivity<View: BaseView, out Presenter : BasePresenter<View>> : AppCompatActivity() {

    protected abstract val passiveView: View
    protected abstract val presenter: Presenter
    protected abstract val layoutResId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initInjection()

        setContentView(layoutResId)

        presenter.viewAttached(passiveView)
    }

    override fun onDestroy() {
        presenter.viewDetached()
        super.onDestroy()
    }

    protected abstract fun initInjection()
}