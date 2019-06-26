package com.atherton.upnext.presentation.features.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atherton.upnext.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MovieAddToListsDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_modal_add_to_list, container, false)
    }

    companion object {
        fun newInstance(movieId: Long): MovieAddToListsDialogFragment {
            return MovieAddToListsDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(BUNDLE_MOVIE_ID, movieId)
                }
            }
        }

        private const val BUNDLE_MOVIE_ID = "bundle_movie_id"
    }
}
