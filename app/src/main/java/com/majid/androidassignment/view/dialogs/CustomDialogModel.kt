package com.majid.androidassignment.view.dialogs

enum class BUTTON {
    NEGATIVE, POSITIVE
}



class ConfirmDialogModel {
    var title = ""
    var message = ""
    var isCancellable = false
    var positiveButtonText = ""
    var negativeButtonText = ""
    var isAutoHide = false
    var autoHideDuration = 1500L
    var isDeleteDialog = false
    lateinit var listener: IDialogListener



    interface IDialogListener {

        fun onButtonClicked(model: Any, clickedButton: BUTTON)

    }
}