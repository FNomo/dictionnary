package fr.uparis.zobonomo.dictionaryapplication.utils

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import fr.uparis.zobonomo.dictionaryapplication.R


class ViewUtil {
    companion object{
        /** Affiche momentanément un message dans l' activité */
        fun toastMessage(context: Context, message: String){
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
        }

        fun checkFields(edits: Array<TextInputEditText>, resources: Resources):Boolean{
            var result  = true

            for(edit in edits){
                val txt = edit.text.toString()
                val isNotEmpty = txt.isNotEmpty()
                result = result && isNotEmpty

                if (!isNotEmpty) edit.error = resources.getString(R.string.error_field_empty)
            }
            Log.d("Fun checkFields", "Result : $result")
            return result
        }

        fun clearFields(vararg edits: TextInputEditText){
            for (edit in edits){
                edit.setText("")
            }
        }

        fun beginAutoTransition(layout: ViewGroup){
            val transition = AutoTransition()
            transition.duration = 500
            transition.ordering = TransitionSet.ORDERING_TOGETHER
            TransitionManager.beginDelayedTransition(layout, transition)
        }

        /**
         * Gere lorsque l' utilisateur annule l' action du dialog
         */
        fun addDialogCancelListener(view: View, dialog: AlertDialog){
            view.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss()}
        }
    }
}