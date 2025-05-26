package fr.uparis.zobonomo.dictionaryapplication.translation_search

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import fr.uparis.zobonomo.dictionaryapplication.MainActivity
import fr.uparis.zobonomo.dictionaryapplication.MainViewModel
import fr.uparis.zobonomo.dictionaryapplication.R
import fr.uparis.zobonomo.dictionaryapplication.backup.BackUpViewModel
import fr.uparis.zobonomo.dictionaryapplication.databinding.FragmentTranslationSearchBinding
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil.Companion.toastMessage
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class TranslationSearchFragment : Fragment(R.layout.fragment_translation_search) {
    private lateinit var binding: FragmentTranslationSearchBinding
    private lateinit var adapter: TranslationSearchRecyclerViewAdapter
    private val model: MainViewModel by activityViewModels ()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTranslationSearchBinding.bind(view)
        adapter = TranslationSearchRecyclerViewAdapter(activity as MainActivity, model)
        binding.list.adapter = adapter
        model.wordSearch.value = binding.searchBarInput.text.toString()

        // init observers
        observeDictionarySelected()
        observeWordSearch()
        observeDictionariesList()
        observeUpdateWord()
        observeDeletedWord()
        observeInsertedWord()

        // init listeners
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab_search)
        fab?.setOnClickListener { onGoogleSearch() }
        binding.chipDico.setOnClickListener { onChipDicoClick() }
        binding.searchBarInput.doOnTextChanged { inputText, _, _, _ ->
            onWordSearchChange( inputText.toString().lowercase() )
        }
    }

    /********************/  /********************/
    /***** Fonctions servant à observé le changement des données du ViewModel *****/

    /** Observe lorsque la recherche à été changer */
    private fun observeWordSearch(){
        model.wordSearch.observe(activity as MainActivity){
            Log.d("fun", "word search text : [$it]")
            if (it.isEmpty()) {
                model.loadAllWords()
            }
            else model.loadWordsByName(it)
        }
    }

    /** Observe lorsque la liste de mots a été modifier */
    private fun observeDictionariesList(){
        model.wordList.observe(activity as MainActivity){
            adapter.setNewListOfWord(it)
        }
    }

    /** Sert à observé lorsque le dictionnaire sélectionné est modifié */
    private fun observeDictionarySelected(){
        model.dictionarySelected.observe(activity as MainActivity){
            if (it == null) {
                binding.chipDico.setText(R.string.chip_dico)
                binding.chipDico.isSelected = false
            }
            else{
                val dico = model.dictionarySelected.value!!
                val spanDicoName = SpannableString( dico.name.replaceFirstChar { it.titlecase(Locale.ROOT) } )
                val txtSrcDest = "[ ${dico.source.substring(0,3)} - ${dico.destination.substring(0,3)} ]"
                val spanSrcDest = SpannableString(txtSrcDest)

                spanSrcDest.setSpan(ForegroundColorSpan(Color.parseColor("#8A1538")) ,0, txtSrcDest.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                spanSrcDest.setSpan(RelativeSizeSpan(0.9f) ,0, txtSrcDest.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                spanSrcDest.setSpan(StyleSpan(Typeface.ITALIC) ,0, txtSrcDest.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

                val span = TextUtils.concat(spanDicoName, " ", spanSrcDest)
                binding.chipDico.text = span
                binding.chipDico.isSelected = true
            }
            adapter.notifyDataSetChanged()
        }
    }

    /** Observe lorsque l' on met à jour mot de la BD
     * * Dans le cas présent on l' ajoute au quiz
     */
    private fun observeUpdateWord(){
        val context = activity as MainActivity
        model.updateWordResult.observe(context){
            if (it > 0) toastMessage(context, resources.getString(R.string.quiz_add))
        }
    }

    /** Observe lorsque l' on supprime un mot */
    private fun observeDeletedWord(){
        model.deleteWordResult.observe(activity as MainActivity){
            if (it > BackUpViewModel.NO_ACTION){
                val snackMessage = resources.getString(R.string.snack_word_removed)
                Snackbar.make(binding.root, snackMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Observe lorsqu' un mot a été ajouter à la BD.
     * Dans le cas présent par la fonction [TranslationSearchRecyclerViewAdapter.onQuizWordWithDico]
     */
    private fun observeInsertedWord(){
        val context = activity as MainActivity
        model.insertWordResult.observe(context){
            if (it.first > 0){
                val snackMessage = context.resources.getString(R.string.snack_word_added)
                val undoButton = context.resources.getString(R.string.snack_undo)
                val word = it.second
                Snackbar.make(context.getRoot(), snackMessage, Snackbar.LENGTH_LONG)
                    .setAction(undoButton) {
                        model.deleteWord(word)
                    }.addCallback(object: Snackbar.Callback(){
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT) adapter.onQuizWord(word)
                        }
                    })
                    .show()
            }
        }
    }

    /********************/  /********************/
    /***** Fonctions gère les listeners *****/

    /**
     * Sert à gérer lorsque le texte de recherche change
     */
    private fun onWordSearchChange(text: String){
        Log.d("fun", "word search change : [$text]")
        adapter.setNewPrefix(text)
        model.wordSearch.value = text
    }

    /**
     * Sert à gere lorsque l' on click sur la recherche google
     * * Vérifie si la barre de recherche est non vide ( sinon affiche une erreur avec [ViewUtil.toastMessage])
     * * Lance un dialogue pour sélectionné une langue et lancé la recherche (avec [createSearchDialog])
     */
    private  fun onGoogleSearch(){
        val word = binding.searchBarInput.text.toString().trim()
        if (word.isEmpty()) toastMessage(activity as MainActivity, resources.getString(R.string.error_no_word))
        else createSearchDialog()
    }

    /**
     * Sert à lancer un dialogue permettant de rentrer la langue de destination
     * * Si les informations sont incorrect on affiche une erreur [ViewUtil.toastMessage]
     * * Sinon on lance la recherche Google
     */
    private fun createSearchDialog(){
        val view: View = layoutInflater.inflate(R.layout.dialog_google_search, null)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .show()

        addSearchDialogButtonListener(view, dialog)
    }

    /**
     * Sert à ajouter un listener au bouton
     */
    private fun addSearchDialogButtonListener(view: View, dialog: AlertDialog){
        view.findViewById<Button>(R.id.btn_search).setOnClickListener {
            val translationTarget = view.findViewById<EditText>(R.id.ed_translation_target).text.toString()
            if (translationTarget.isEmpty()) toastMessage(activity as MainActivity, resources.getString(R.string.error_no_translation_target))
            else {
                startGoogleSearch(translationTarget)
                dialog.dismiss()
            }
        }
    }

    private fun onChipDicoClick(){
        val fragment = DictionaryDialogFragment()
        fragment.show((activity as MainActivity).supportFragmentManager, "dialog")
    }

    /**
     * Sert à lancer la recherche Google
     * * Lance une nouvelle intent Internet
     */
    private fun startGoogleSearch(target: String){
        val prefix = "http://www.google.fr/search?q="
        val suffix: String = binding.searchBarInput.text.toString() + " ${resources.getString(R.string.dialog_search_to).trim()} " + target

        val webIntent = Intent(Intent.ACTION_VIEW)
        webIntent.data = Uri.parse( "$prefix$suffix" )
        startActivity( webIntent )
    }

    companion object {
        @JvmStatic
        fun newInstance() = TranslationSearchFragment()
    }
}