package com.atherton.upnext.presentation.common.addtolists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.presentation.base.RoundedBottomSheetDialogFragment
import com.atherton.upnext.presentation.common.ContentType
import com.atherton.upnext.util.extension.getAppComponent
import com.atherton.upnext.util.extension.getViewModel
import com.atherton.upnext.util.extension.isVisible
import kotlinx.android.synthetic.main.dialog_add_to_lists.*
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_discover_content.progressBar
import kotlinx.android.synthetic.main.fragment_discover_content.recyclerView
import javax.inject.Inject
import javax.inject.Named

class AddToListsDialogFragment
    : RoundedBottomSheetDialogFragment<AddToListsAction, AddToListsState, AddToListsViewEffect, AddToListsViewModel>() {

    override val layoutResId: Int = R.layout.dialog_add_to_lists
    override val stateBundleKey: String by lazy { "bundle_key_add_to_lists_${contentType}_state" }

    private val contentId: Long by lazy {
        arguments?.getLong(BUNDLE_CONTENT_ID) as Long
    }

    private val contentType: ContentType by lazy {
        arguments?.getParcelable(BUNDLE_CONTENT_TYPE) as ContentType
    }

    @Inject @field:Named(AddToListsViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val viewModel: AddToListsViewModel by lazy { getViewModel<AddToListsViewModel>(vmFactory) }

    private val recyclerViewAdapter: AddToListsAdapter by lazy {
        AddToListsAdapter(
            onListClickListener = { contentList ->
                viewModel.dispatch(
                    AddToListsAction.ToggleContentListStatus(
                        contentId = contentId,
                        contentType = contentType,
                        listId = contentList.listId
                    )
                )
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTitleText()
        initClickListeners()
        initRecyclerView()

        if (savedInstanceState == null) {
            viewModel.dispatch(AddToListsAction.Load(contentId, contentType))
        }
    }

    override fun renderState(state: AddToListsState) {
        when (state) {
            is AddToListsState.Loading -> {
                errorLayout.isVisible = false
                progressBar.isVisible = true

                if (state.results != null && state.results.isNotEmpty()) {
                    // show a loading state with cached data
                    recyclerView.isVisible = true
                    recyclerViewAdapter.submitList(state.results)
                } else {
                    recyclerView.isVisible = false
                }
            }
            is AddToListsState.Content -> {
                progressBar.isVisible = false
                if (state.results.isEmpty()) {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = getString(R.string.error_no_results_found)
                } else {
                    recyclerView.isVisible = true
                    errorLayout.isVisible = false
                    recyclerViewAdapter.submitList(state.results)
                }
            }
            is AddToListsState.Error -> {
                progressBar.isVisible = false

                if (state.fallbackResults != null && state.fallbackResults.isNotEmpty()) {
                    recyclerView.isVisible = true
                    recyclerViewAdapter.submitList(state.fallbackResults)
                } else {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = state.message
                    retryButton.isVisible = state.canRetry
                }
            }
        }
    }

    override fun processViewEffects(viewEffect: AddToListsViewEffect) {
        when (viewEffect) {
            is AddToListsViewEffect.CloseScreen -> dismiss()
            is AddToListsViewEffect.ShowNewListScreen -> {
                fragmentManager?.let { manager ->
                    navigator.showNewListScreen(
                        contentId = viewEffect.contentId,
                        contentType = viewEffect.contentType,
                        fragmentManager = manager
                    )
                    dismiss()
                }
            }
        }
    }

    private fun initTitleText() {
        val title = when (contentType) {
            is ContentType.TvShow -> getString(R.string.add_to_lists_save_show_title)
            is ContentType.Movie -> getString(R.string.add_to_lists_save_movie_title)
        }
        addToListsSaveToTitleTextView.text = title
    }

    private fun initClickListeners() {
        addToListsDoneButton.setOnClickListener {
            viewModel.dispatch(AddToListsAction.DoneClicked)
        }

        addToListsNewListButton.setOnClickListener {
            viewModel.dispatch(AddToListsAction.NewListClicked(contentId, contentType))
        }
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
    }

    override fun initInjection(initialState: AddToListsState?) {
        DaggerAddToListsComponent.builder()
            .addToListsModule(AddToListsModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance(contentId: Long, contentType: ContentType): AddToListsDialogFragment {
            return AddToListsDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(BUNDLE_CONTENT_ID, contentId)
                    putParcelable(BUNDLE_CONTENT_TYPE, contentType)
                }
            }
        }

        private const val BUNDLE_CONTENT_ID = "bundle_content_id"
        private const val BUNDLE_CONTENT_TYPE = "bundle_content_type"

        const val FRAGMENT_TAG = "fragment_dialog_add_to_lists"
    }
}
