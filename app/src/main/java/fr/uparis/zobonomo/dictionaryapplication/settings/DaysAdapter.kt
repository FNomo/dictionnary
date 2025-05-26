package fr.uparis.zobonomo.dictionaryapplication.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.uparis.zobonomo.dictionaryapplication.MainActivity
import fr.uparis.zobonomo.dictionaryapplication.MainViewModel
import fr.uparis.zobonomo.dictionaryapplication.R
import fr.uparis.zobonomo.dictionaryapplication.databinding.SettingsDayBinding
import fr.uparis.zobonomo.dictionaryapplication.utils.Util
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil
import fr.uparis.zobonomo.dictionaryapplication.settings.SettingsFragment.Companion.SETTINGS_DAYS

/**
 * Sert à adapter la list de jour au recycle view correspondant
 * @property context le context qui se sert de l' adapter
 * @property preferences Les paramètres de l' application
 */
class DaysAdapter(private val context: SettingsFragment, private val model: MainViewModel) : RecyclerView.Adapter<DaysAdapter.ViewHolder>(){
    private val days: Array<String> = context.resources.getStringArray(R.array.pref_days)
    private val daysLanguages: Array<String>
    private val contextActivity: MainActivity = context.activity as MainActivity
    private val preferences: SharedPreferences = contextActivity.getSharedPreferences(SettingsFragment.PREF_NAME, Context.MODE_PRIVATE)

    /********************/  /********************/
    /***** init *****/
    init {
        val daysValueStr = preferences.getString(SETTINGS_DAYS, null)
        val arr = Util.decodeArray(daysValueStr)

        if (arr == null){
            daysLanguages = arrayOf("", "", "", "", "", "", "")
            for (indice in daysLanguages.indices) daysLanguages[indice] = context.resources.getString(R.string.pref_all_language)
        }
        else daysLanguages = arr

        observeLanguagesPerDialog()
    }


    /********************/  /********************/
    /***** Fonctions servant pour le binding *****/

    inner class ViewHolder(val binding: SettingsDayBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SettingsDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        binding.dayInfo = DayInformations(days[position], daysLanguages[position])
        binding.root.setOnClickListener{ createChooseDialog(position) }
    }

    override fun getItemCount(): Int = days.size

    /********************/  /********************/
    /***** Fonctions servant de listeners *****/

    /**
     * Fonction servant à observer lorsque l' on terminer de charger tout les languages d' un dialogue
     */
    private fun observeLanguagesPerDialog(){
        model.languagesPerDialog.observe(contextActivity){
            val list = it.second
            if (list.isNotEmpty()){
                val radioGroup: RadioGroup = it.first.findViewById(R.id.radio_list)
                for (word in list){
                    val radio: RadioButton = context.layoutInflater.inflate(R.layout.settings_language, radioGroup, false) as RadioButton
                    val language = Util.capitalize(word.source!!) + " - " + Util.capitalize(word.destination!!)
                    radio.id = View.generateViewId() // Sinon le radioGroup ne fonctionne pas bien
                    radio.text = language
                    radioGroup.addView(radio)
                }
            }
        }
    }

    /********************/  /********************/
    /***** Fonctions servant de listeners *****/

    /**
     * Crée un dialog permettant de choisir une langue
     */
    private fun createChooseDialog(pos: Int){
        val dialogContent = context.resources.getString(R.string.pref_dialog_languages_content, days[pos])
        val view: View = context.layoutInflater.inflate(R.layout.dialog_language_selection, null)
        view.findViewById<TextView>(R.id.dialog_content).text = dialogContent
        val dialog = AlertDialog.Builder(contextActivity)
            .setView(view)
            .show()

        model.loadAllLanguages(view)
        addDialogChooseListener(pos, view, dialog)
        ViewUtil.addDialogCancelListener(view, dialog)
    }

    /**
     * Click sue le bouton choose
     * Quand on clique dessus on change la valeur donné à l' indice pos
     * Puis on l' a met à jour
     */
    private fun addDialogChooseListener(pos: Int, view: View,  dialog: Dialog){
        view.findViewById<Button>(R.id.btn_choose).setOnClickListener {
            val checkedId = view.findViewById<RadioGroup>(R.id.radio_list).checkedRadioButtonId
            if (checkedId > -1){
                val language = view.findViewById<RadioButton>(checkedId).text.toString()
                daysLanguages[pos] = language
                updateDaysLanguage()
                notifyItemChanged(pos)
                dialog.dismiss()
            }
        }
    }

    /**
     * On met à jour les sharedPreference [preferences]
     */
    private fun updateDaysLanguage(){
        val languages = Util.encodeArray(daysLanguages)
        preferences.edit().putString(SETTINGS_DAYS, languages)
            .apply()
    }
}