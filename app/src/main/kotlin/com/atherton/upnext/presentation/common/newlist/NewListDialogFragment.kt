package com.atherton.upnext.presentation.common.newlist

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.presentation.base.BaseDialogFragment
import com.atherton.upnext.presentation.common.ContentType
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.extension.getActivityViewModel
import com.atherton.upnext.util.extension.getAppComponent
import com.atherton.upnext.util.extension.getViewModel
import com.atherton.upnext.util.extension.isVisible
import kotlinx.android.synthetic.main.dialog_new_list.*
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_discover_content.progressBar
import javax.inject.Inject
import javax.inject.Named

class NewListDialogFragment
    : BaseDialogFragment<NewListAction, NewListState, NewListViewEffect, NewListViewModel>() {

    override val layoutResId: Int = com.atherton.upnext.R.layout.dialog_new_list
    override val stateBundleKey: String by lazy { "bundle_key_new_list_${contentType}_state" }

    private val contentId: Long? by lazy {
        val id: Long? = arguments?.getLong(BUNDLE_CONTENT_ID)
        return@lazy if (id != null && id != 0L) id else null
    }

    private val contentType: ContentType by lazy {
        arguments?.getParcelable(BUNDLE_CONTENT_TYPE) as ContentType
    }

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(NewListViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: NewListViewModel by lazy { getViewModel<NewListViewModel>(vmFactory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
    }

    override fun onResume() {
        super.onResume()
        initDialogSize()
    }

    private fun initDialogSize() {
        dialog?.window?.let {
            val size = Point()
            val display = it.windowManager.defaultDisplay
            display.getSize(size)

            val widthPercent: Int = (size.x * 0.9).toInt()
            it.setLayout(widthPercent, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun renderState(state: NewListState) {
        when (state) {
            is NewListState.Loading -> {
                errorLayout.isVisible = false
                progressBar.isVisible = true
            }
            is NewListState.Content -> {
                progressBar.isVisible = false
                errorLayout.isVisible = false
            }
            is NewListState.Error -> {
                progressBar.isVisible = false
                errorLayout.isVisible = true
                retryButton.isVisible = false
                errorTextView.text = getString(com.atherton.upnext.R.string.error_message_generic)
            }
        }
    }

    override fun processViewEffects(viewEffect: NewListViewEffect) {
        when (viewEffect) {
            is NewListViewEffect.ShowSuccessState -> {
                sharedViewModel.dispatch(
                    MainAction.ListCreated(viewEffect.message, viewEffect.listId, contentType)
                )
                dismiss()
            }
            NewListViewEffect.CloseScreen -> dismiss()
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initClickListeners() {
        newListCancelButton.setOnClickListener {
            viewModel.dispatch(NewListAction.CancelClicked)
        }
        newListCreateButton.setOnClickListener {
            val titleText = listTitleEditText.text.toString()
            val listTitle: String = if (titleText.isNotBlank()) {
                titleText
            } else {
                getString(com.atherton.upnext.R.string.new_list_title_untitled)
            }
            viewModel.dispatch(NewListAction.CreateClicked(contentId, contentType, listTitle))
        }
    }

    override fun initInjection(initialState: NewListState?) {
        DaggerNewListComponent.builder()
            .newListModule(NewListModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance(contentId: Long, contentType: ContentType): NewListDialogFragment {
            return NewListDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(BUNDLE_CONTENT_ID, contentId)
                    putParcelable(BUNDLE_CONTENT_TYPE, contentType)
                }
            }
        }

        private const val BUNDLE_CONTENT_ID = "bundle_content_id"
        private const val BUNDLE_CONTENT_TYPE = "bundle_content_type"

        const val FRAGMENT_TAG = "fragment_dialog_new_list"
    }
}
