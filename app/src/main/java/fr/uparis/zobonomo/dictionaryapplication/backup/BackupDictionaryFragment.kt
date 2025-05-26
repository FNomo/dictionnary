package fr.uparis.zobonomo.dictionaryapplication.backup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import fr.uparis.zobonomo.dictionaryapplication.R
import fr.uparis.zobonomo.dictionaryapplication.database.Dictionary
import fr.uparis.zobonomo.dictionaryapplication.databinding.FragmentBackupDictionaryBinding
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil

/**
 * Sert a construire le partie permettant de sauvegarde un dictionnaire dans notre base de donnée
 */
class BackupDictionaryFragment : Fragment(R.layout.fragment_backup_dictionary) {
    private lateinit var binding: FragmentBackupDictionaryBinding
    private val model: BackUpViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBackupDictionaryBinding.bind(view)

        if (model.url.value != null) binding.txtUrl.text = model.url.value
        binding.btnSave.setOnClickListener { onSave() }
        observeInsertedDictionary()
        observeDeletedDictionary()
    }

    /**
     * Sert à implémenter un listener pour le bouton de sauvegarde
     */
    private fun onSave(){
        val checkFields = ViewUtil.checkFields(
            arrayOf(
                binding.edTargetDico,
                binding.edTranslationDest,
                binding.edTranslationSrc
            ), resources
        )
        if (checkFields){
            val name = binding.edTargetDico.text.toString().trim().lowercase()
            val dest = binding.edTranslationDest.text.toString().trim().lowercase()
            val src = binding.edTranslationSrc.text.toString().trim().lowercase()
            val url = binding.txtUrl.text.toString()
            val dico = Dictionary(name, src, dest, url)
            model.insertDicoBySource(dico)
        }
    }

    /**
     * Observe lorsque qu' un dictionnaire à été insérer
     */
    private fun observeInsertedDictionary(){
        model.insertedDicoInfo.observe(activity as BackupActivity){
            val insertId = it.first
            val dico = it.second

            if (insertId == BackUpViewModel.NO_ACTION) displayInsertProblem()
            else displayInsertOk(dico)
        }
    }

    /**
     * Observe lorsqu' un dictionnaire est supprimer
     */
    private fun observeDeletedDictionary(){
        model.deleteDicoResult.observe(activity as BackupActivity){
            if (it > BackUpViewModel.NO_ACTION){
                val snackMessage = resources.getString(R.string.snack_dico_removed)
                Snackbar.make(binding.root, snackMessage, Snackbar.LENGTH_LONG)
                    .addCallback(object: Snackbar.Callback(){
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT) (activity as BackupActivity).finish()
                        }
                    })
                    .show()
            }
        }
    }

    /**
     * Crée et afficher une nouvelle snackBar avec UNDO
     */
    private fun displayInsertOk(dico: Dictionary){
        ViewUtil.clearFields(
            binding.edTargetDico,
            binding.edTranslationSrc,
            binding.edTranslationDest
        )
        val snackMessage = resources.getString(R.string.snack_dico_added)
        val undoButton = resources.getString(R.string.snack_undo)

        Snackbar.make(binding.root, snackMessage, Snackbar.LENGTH_LONG)
            .setAction(undoButton) {
                model.deleteDico(dico)
            }
            .show()
    }

    /**
     * Crée et afficher une nouvelle snackBar avec problem
     */
    private fun displayInsertProblem(){
        val snackMessage = resources.getString(R.string.error_snack_dico_added)
        Snackbar.make(binding.root, snackMessage, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BackupDictionaryFragment()
    }
}