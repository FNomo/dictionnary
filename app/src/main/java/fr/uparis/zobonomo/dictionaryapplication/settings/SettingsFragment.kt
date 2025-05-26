package fr.uparis.zobonomo.dictionaryapplication.settings

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import fr.uparis.zobonomo.dictionaryapplication.MainActivity
import fr.uparis.zobonomo.dictionaryapplication.MainViewModel
import fr.uparis.zobonomo.dictionaryapplication.R
import fr.uparis.zobonomo.dictionaryapplication.databinding.FragmentSettingBinding
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil.Companion.addDialogCancelListener
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil.Companion.toastMessage

/**
 * Représente la partie paramétrage de notre application
 * @property preferences les paramètres de l' application
 * @property binding le lien entre la vue et le fragment
 * @property model le ViewModel pour faire la liaison entre les donnée et
 */
class SettingsFragment(private val preferences: SharedPreferences) : Fragment(R.layout.fragment_setting) {
    private  lateinit var binding: FragmentSettingBinding
    private lateinit var adapter: DaysAdapter
    private val settings by lazy { (activity as MainActivity).getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    private val model: MainViewModel by activityViewModels ()

    /********************/  /********************/
    /***** init *****/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // init
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingBinding.bind(view)
        adapter = DaysAdapter(this, model)
        binding.listOfDays.adapter = adapter

        val userName = settings.getString(SETTINGS_USERNAME, null)
        val words = settings.getInt(SETTINGS_WORDS, DEFAULT_WORDS)
        val frequency = settings.getInt(SETTINGS_FREQUENCY, DEFAULT_FREQUENCY)

        if (userName != null) binding.edPrefUserName.setText(userName)
        binding.edPrefQuizWords.setText(words.toString())
        binding.edPrefQuizFrequency.setText(frequency.toString())

        // observers
        observeAllDicosDeletion()
        observeAllWordsDeletion()

        // listeners
        binding.btnClnDicos.setOnClickListener { createCleanDicosDialog(MODE_DICO) }
        binding.btnClnWords.setOnClickListener { createCleanDicosDialog(MODE_WORD) }

        // text watchers
        binding.edPrefUserName.doAfterTextChanged {
            updateSetting(SETTINGS_USERNAME, it.toString())
        }

        binding.edPrefQuizWords.doAfterTextChanged { 
            if (it == null || it.toString().isEmpty()) binding.edPrefQuizWords.setText(DEFAULT_WORDS.toString())
            else  updateSetting(SETTINGS_WORDS, it.toString())
        }

        binding.edPrefQuizFrequency.doAfterTextChanged {
            if (it == null || it.toString().isEmpty()) binding.edPrefQuizFrequency.setText(DEFAULT_FREQUENCY.toString())
            else  updateSetting(SETTINGS_FREQUENCY, it.toString())
        }
    }
    /********************/  /********************/
    /***** Fonctions servant à observé *****/

    /**
     * Observe quand on a supprimer tout les mots de la base données
     * On affiche un Toast
     */
    private  fun observeAllWordsDeletion(){
        model.deleteAllWordsResult.observe(activity as MainActivity){
            toastMessage(activity as MainActivity, resources.getString(R.string.pref_cln_words))
            (activity as MainActivity).restart()
        }
    }

    /**
     * Observe quand on a supprimer tout les dictionnaires de la base données
     * On affiche un Toast
     */
    private  fun observeAllDicosDeletion(){
        model.deleteAllDicosResult.observe(activity as MainActivity){
            toastMessage(activity as MainActivity, resources.getString(R.string.pref_cln_dicos))
            (activity as MainActivity).restart()
        }
    }

    /********************/  /********************/
    /***** Fonctions gère les listeners *****/

    /**
     * Creation et affichage d' un dialog permettant à l' utilisateur de confirmer la suppression des mots ou des dictionnaires
     * @param mode Le type de donné que l' on doit nettoyer
     */
    private fun createCleanDicosDialog(mode: Int){
        val layout: Int = if (mode == MODE_DICO) R.layout.dialog_clear_dicos else R.layout.dialog_clear_words
        val view: View = layoutInflater.inflate(layout, null)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .show()

        addDialogCleanActionListener(view, dialog, mode)
        addDialogCancelListener(view, dialog)
    }

    /**
     * Gere lorsque l' utilisateur confirme la suppression des mots
     */
    private fun addDialogCleanActionListener(view: View, dialog: AlertDialog, mode: Int){
        if (mode == MODE_DICO) view.findViewById<Button>(R.id.btn_clean).setOnClickListener {
            model.deleteAllDicos()
            dialog.dismiss()
        }
        else if (mode == MODE_WORD) view.findViewById<Button>(R.id.btn_clean).setOnClickListener {
            model.deleteAllWords()
            dialog.dismiss()
        }
    }

    /**
     * Met à jour les paramètre
     * @param key La clé de [preferences]
     * @param value La nouvelle valeur de la clé
     */
    private  fun updateSetting(key: String, value: Any){
        when (value) {
            is Int -> preferences.edit().putInt(key, value).apply()
            is String -> preferences.edit().putString(key, value).apply()
        }
    }

    companion object{
        // constante
        const val MODE_DICO: Int = 0
        const val MODE_WORD: Int = 1

        // default
        @JvmStatic val PREF_NAME = "myPrefs"
        @JvmStatic val DEFAULT_WORDS = 10
        @JvmStatic val DEFAULT_FREQUENCY = 1

        // setting key
        @JvmStatic val SETTINGS_USERNAME = "user_name"
        @JvmStatic val SETTINGS_WORDS = "words_count"
        @JvmStatic val SETTINGS_FREQUENCY = "quiz_frequency"
        @JvmStatic val SETTINGS_DAYS = "language_per_day"
    }
}