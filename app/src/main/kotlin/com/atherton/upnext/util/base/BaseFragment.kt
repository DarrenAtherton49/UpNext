package com.atherton.upnext.util.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.ui.setupWithNavController
import com.atherton.upnext.presentation.main.MainActivity
import com.atherton.upnext.presentation.main.MainModule
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.util.extensions.observe
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

abstract class BaseFragment<Action : BaseAction,
    State,
    ViewEffect : BaseViewEffect,
    ViewModel : UpNextViewModel<Action, State, ViewEffect>>
    : Fragment()
    where State : BaseState,
          State : Parcelable {

    protected abstract val layoutResId: Int
    protected abstract val stateBundleKey: String
    protected abstract val viewModel: ViewModel
    protected abstract val sharedViewModel: MainViewModel

    protected abstract val toolbarOptions: ToolbarOptions?
    private var toolbar: Toolbar? = null

    private val mainActivity: MainActivity by lazy { activity as MainActivity }

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        // support process death by re-supplying last state to ViewModel
        val lastState: State? = savedInstanceState?.getParcelable(stateBundleKey)
        initInjection(lastState)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbarOptions)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.observableState.observe(viewLifecycleOwner) { state ->
            state?.let { renderState(state) }
        }
    }

    override fun onResume() {
        super.onResume()
        disposables += viewModel.viewEffects().subscribe {
            processViewEffects(it)
        }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    // support process death by saving last ViewModel state in bundle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(stateBundleKey, viewModel.observableState.value)
    }

    protected abstract fun initInjection(initialState: State?)

    protected abstract fun renderState(state: State)

    protected abstract fun processViewEffects(viewEffect: ViewEffect)

    protected val mainModule: MainModule by lazy { MainModule(null) }

    private fun setupToolbar(toolbarOptions: ToolbarOptions?) {
        toolbarOptions?.let {
            if (it.toolbarResId != null) {
                toolbar = it.toolbarResId.let { id ->
                    view?.findViewById(id)
                }
                toolbar?.let { toolbar ->
                    toolbar.setupWithNavController(mainActivity.navController, mainActivity.appBarConfiguration)
                    toolbarOptions.titleResId?.let { titleResId ->
                        toolbar.title = getString(titleResId)
                    }

                    toolbarOptions.menuResId?.let { menuResId ->
                        toolbar.inflateMenu(menuResId)
                    }

                    val menu = toolbar.menu
                    for (i in 0 until menu.size()) {
                        menu.getItem(i).setOnMenuItemClickListener { menuItem -> onMenuItemClicked(menuItem) }
                    }
                }
            }
        }
    }

    fun editMenuItem(menuItemId: Int, editBlock: MenuItem.() -> Unit) {
        val menu = toolbar?.menu ?: throw IllegalStateException("Toolbar has not been set so cannot edit")
        val menuItem = menu.findItem(menuItemId) ?: throw IllegalArgumentException("Could not find menu item")
        menuItem.editBlock()
    }

    abstract fun onMenuItemClicked(menuItem: MenuItem): Boolean
}
