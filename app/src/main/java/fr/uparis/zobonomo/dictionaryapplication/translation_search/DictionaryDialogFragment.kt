package fr.uparis.zobonomo.dictionaryapplication.translation_search

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import fr.uparis.zobonomo.dictionaryapplication.MainActivity
import fr.uparis.zobonomo.dictionaryapplication.MainViewModel
import fr.uparis.zobonomo.dictionaryapplication.R
import fr.uparis.zobonomo.dictionaryapplication.databinding.DialogDictionarySearchBinding

class DictionaryDialogFragment: DialogFragment(R.layout.dialog_dictionary_search) {
    private lateinit var binding: DialogDictionarySearchBinding
    private lateinit var adapter: DictionaryDialogRecyclerViewAdapter
    private  val model: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_DictionaryApplication)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDictionarySearchBinding.bind(view)


        adapter = DictionaryDialogRecyclerViewAdapter(model.dictionarySelected)
        binding.list.adapter = adapter
        model.dictionarySearch.value = binding.searchBarInput.text.toString()

        // les observers
        observeDictionarySearch()
        observeDictionariesList()

        // les listeners
        binding.btnBack.setOnClickListener { back() }
        binding.searchBarInput.doOnTextChanged { inputText, _, _, _ ->
            onDictionarySearchChange( inputText.toString().lowercase() )
        }
    }

    /********************/  /********************/
    /***** Fonction servant à observer les variables *****/

    /**
     * Observe lorsque le text de recherche change
     */
    private fun observeDictionarySearch(){
        model.dictionarySearch.observe(activity as MainActivity){
            if (it.isEmpty()) model.loadAllDictionaries()
            else model.loadDictionariesByName(it)
        }
    }

    /**
     * Observe la liste des dictionnaires qui change
     */
    private fun observeDictionariesList(){
        model.dictionaryList.observe(activity as MainActivity){
            adapter.setNewListOfDictionaries(it)
        }
    }

    /**
     * Observe lorsque la recherche change
     */
    private fun onDictionarySearchChange(text: String){
        adapter.setNewPrefix(text)
        model.dictionarySearch.value = text
    }

    /**
     * Observe lorsque l' on clique sur le back button
     */
    private fun back(){
        dismiss()
    }
}