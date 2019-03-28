package com.atherton.upnext.presentation.features.settings.licenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.ui.setupWithNavController
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainActivity
import kotlinx.android.synthetic.main.base_app_bar.*

class LicensesFragment : Fragment() {

    private val mainActivity: MainActivity by lazy { activity as MainActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_licenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.setupWithNavController(mainActivity.navController, mainActivity.appBarConfiguration)
        toolbar.title = getString(R.string.fragment_label_licenses)
    }
}
