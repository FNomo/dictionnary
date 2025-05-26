package fr.uparis.zobonomo.dictionaryapplication.backup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import fr.uparis.zobonomo.dictionaryapplication.R
import fr.uparis.zobonomo.dictionaryapplication.database.Word
import fr.uparis.zobonomo.dictionaryapplication.databinding.FragmentBackupWordBinding
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil.Companion.clearFields
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil.Companion.checkFields
import fr.uparis.zobonomo.dictionaryapplication.backup.BackUpViewModel.Companion.NO_ACTION as NO_ACTION

/**
 * Sert a construire le partie permettant de sauvegarde un mot dans notre base de donnée
 */
class BackupWordFragment : Fragment(R.layout.fragment_backup_word) {
    private lateinit var binding: FragmentBackupWordBinding
    private val model: BackUpViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBackupWordBinding.bind(view)

        if (model.url.value != null) binding.txtUrl.text = model.url.value
        binding.btnSave.setOnClickListener { onSave() }
        observeInsertedWord()
        observeDeletedWord()
    }

    /**
     * Sert à implémenter un listener pour le bouton de sauvegarde
     */
    private fun onSave(){
        val checkFields = checkFields(arrayOf(
            binding.edOrigin,
            binding.edTranslation,
            binding.edTranslationDest,
            binding.edTranslationSrc)
            , resources)
        if (checkFields){
            val origin = binding.edOrigin.text.toString().trim().lowercase()
            val translation = binding.edTranslation.text.toString().trim().lowercase()
            val dest = binding.edTranslationDest.text.toString().trim().lowercase()
            val src = binding.edTranslationSrc.text.toString().trim().lowercase()
            val url = binding.txtUrl.text.toString()
            val word = Word(origin, translation, src, dest, url)
            model.insertWordBySource(word)
        }
    }

    /**
     * Observe lorsque qu' un mot à été insérer
     */
    private fun observeInsertedWord(){
        model.insertedWordInfo.observe(activity as BackupActivity){
            val insertId = it.first
            val word = it.second

            if (insertId == NO_ACTION) displayInsertProblem()
            else displayInsertOk(word)
        }
    }

    /**
     * Observe lorsqu' un mot est supprimer
     */
    private fun observeDeletedWord(){
        model.deleteWordResult.observe(activity as BackupActivity){
            if (it > NO_ACTION){
                val snackMessage = resources.getString(R.string.snack_word_removed)
                Snackbar.make(binding.root, snackMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Crée et afficher un nouveau snackBar(avec UNDO) affichant que l' insertion est OK
     * * Le bouton UNDO supprime le mot dans la BD
     * * Le TimeOut du SnackBar termine l' activité
     */
    private fun displayInsertOk(word: Word){
        clearFields(binding.edOrigin, binding.edTranslation, binding.edTranslationSrc, binding.edTranslationDest)
        val snackMessage = resources.getString(R.string.snack_word_added)
        val undoButton = resources.getString(R.string.snack_undo)

        Snackbar.make(binding.root, snackMessage, Snackbar.LENGTH_LONG)
            .setAction(undoButton) {
               model.deleteWord(word)
            }.addCallback(object: Snackbar.Callback(){
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event == DISMISS_EVENT_TIMEOUT) (activity as BackupActivity).finish()
                }
            })
            .show()
    }

    /**
     * Crée et afficher une nouvelle snackBar avec problem
     */
    private fun displayInsertProblem(){
        val snackMessage = resources.getString(R.string.error_snack_word_added)
        Snackbar.make(binding.root, snackMessage, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BackupWordFragment()
    }
}