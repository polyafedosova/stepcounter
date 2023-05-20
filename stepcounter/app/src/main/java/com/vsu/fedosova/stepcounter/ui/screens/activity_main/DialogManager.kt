package com.vsu.fedosova.stepcounter.ui.screens.activity_main

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import com.vsu.fedosova.stepcounter.R

object DialogManager {
    fun locationSettingsDialog(context: Context, listener: Listener){
        var builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Включить GPS?")
        dialog.setMessage("GPS выключен, хотите включить GPS?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,"Да"){_,_->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Нет"){_,_->
            dialog.dismiss()
        }
        dialog.show()
    }
    fun searchByNameDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context, R.style.MyDialogTheme)
        val edName = EditText(context)
        builder.setView(edName)
        val dialog = builder.create()
        dialog.setTitle("Город: ")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,"Да"){_,_->
            listener.onClick(edName.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Нет"){_,_->
            dialog.dismiss()
        }
        dialog.show()
    }
    interface Listener{
        fun onClick(name: String?)
    }
}