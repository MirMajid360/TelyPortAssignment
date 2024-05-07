package com.majid.androidassignment.view.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.majid.androidassignment.databinding.FragmentCustomDialogBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomDialog() : DialogFragment() {
    private lateinit var binding: FragmentCustomDialogBinding


    private var dialogModel = ConfirmDialogModel()
    private var activity: Activity? = null

    constructor(dialogModel: ConfirmDialogModel, activity: Activity?) : this() {

        try {


            this.dialogModel = dialogModel
            this.activity = activity

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        try {


            initViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initViews() {

        try {

            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//            binding.btnSendNotification.text = dialogModel.positiveButtonText
//            binding.btnViewNotification.text = dialogModel.negativeButtonText
//

            binding.btnSendNotification.setOnClickListener {
                dialogModel.listener.onButtonClicked("", BUTTON.POSITIVE)
                dismiss()
            }

            binding.btnViewNotification.setOnClickListener {
                dialogModel.listener.onButtonClicked("", BUTTON.NEGATIVE)
                dismiss()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = CustomDialog()

        @JvmStatic
        fun newInstance(model: ConfirmDialogModel, activity: Activity?) =
            CustomDialog(model, activity)


    }
}
