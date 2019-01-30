package com.atherton.upnext.util.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AlertDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            val dialog = AlertDialog.Builder(it).apply {
                arguments?.let { bundle ->
                    setTitle(bundle.getString(BUNDLE_TITLE))
                    setMessage(bundle.getString(BUNDLE_MESSAGE))

                    val negativeButtonText = bundle.getString(BUNDLE_NEGATIVE_BUTTON_TEXT)
                    val negativeOnClick = bundle.getParcelable(BUNDLE_NEGATIVE_ON_CLICK) as OnClickListener?
                    if (negativeButtonText != null && negativeOnClick != null) {
                        setNegativeButton(negativeButtonText) { _, _ -> negativeOnClick.onClick() }
                    }

                    val positiveButtonText = bundle.getString(BUNDLE_POSITIVE_BUTTON_TEXT)
                    val positiveOnClick = bundle.getParcelable(BUNDLE_POSITIVE_ON_CLICK) as OnClickListener?
                    if (positiveButtonText != null && positiveOnClick != null) {
                        setPositiveButton(positiveButtonText) { _, _ -> positiveOnClick.onClick() }
                    }
                }
            }
            isCancelable = arguments?.getBoolean(BUNDLE_CANCELLABLE) ?: true

            return dialog.create()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    interface OnClickListener : Parcelable {
        fun onClick()

        override fun writeToParcel(dest: Parcel?, flags: Int) {}
        override fun describeContents(): Int = 0
    }

    companion object {
        fun newInstance(
            title: String,
            message: String,
            negativeButtonText: String? = null,
            negativeOnClick: OnClickListener? = null,
            positiveButtonText: String? = null,
            positiveOnClick: OnClickListener? = null,
            cancellable: Boolean = true
        ): AlertDialogFragment {
            return AlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_TITLE, title)
                    putString(BUNDLE_MESSAGE, message)
                    putString(BUNDLE_NEGATIVE_BUTTON_TEXT, negativeButtonText)
                    putParcelable(BUNDLE_NEGATIVE_ON_CLICK, negativeOnClick)
                    putString(BUNDLE_POSITIVE_BUTTON_TEXT, positiveButtonText)
                    putParcelable(BUNDLE_POSITIVE_ON_CLICK, positiveOnClick)
                    putBoolean(BUNDLE_CANCELLABLE, cancellable)
                }
            }
        }

        private const val BUNDLE_TITLE = "bundle_title"
        private const val BUNDLE_MESSAGE = "bundle_message"
        private const val BUNDLE_NEGATIVE_BUTTON_TEXT = "bundle_negative_button_text"
        private const val BUNDLE_NEGATIVE_ON_CLICK = "bundle_negative_on_click"
        private const val BUNDLE_POSITIVE_BUTTON_TEXT = "bundle_positive_button_text"
        private const val BUNDLE_POSITIVE_ON_CLICK = "bundle_positive_on_click"
        private const val BUNDLE_CANCELLABLE = "bundle_cancellable"
    }
}