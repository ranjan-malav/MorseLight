package com.ranjan.malav.morselight_flashlightwithmorsecode.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import kotlinx.android.synthetic.main.fragment_send_info.view.*

class InfoDialog : BottomSheetDialogFragment() {

    companion object {
        const val LAYOUT_RES = "layout_res"

        @JvmStatic
        fun newInstance(@LayoutRes layoutRes: Int) = InfoDialog().apply {
            arguments = Bundle().apply {
                putInt(LAYOUT_RES, layoutRes)
            }
        }
    }

    private var layoutRes: Int = R.layout.fragment_send_info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { layoutRes = it.getInt(LAYOUT_RES) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.close_button.setOnClickListener {
            dismiss()
        }

    }
}
