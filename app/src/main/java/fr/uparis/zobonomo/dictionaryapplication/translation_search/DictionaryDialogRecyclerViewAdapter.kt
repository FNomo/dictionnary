package fr.uparis.zobonomo.dictionaryapplication.translation_search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import fr.uparis.zobonomo.dictionaryapplication.database.Dictionary
import fr.uparis.zobonomo.dictionaryapplication.databinding.TranslationDicoBinding
import fr.uparis.zobonomo.dictionaryapplication.databinding.TranslationDicoNoBinding
import fr.uparis.zobonomo.dictionaryapplication.databinding.TranslationDicoSelectedBinding


class DictionaryDialogRecyclerViewAdapter
    (var selectedDico: MutableLiveData<Dictionary?>)
    : RecyclerView.Adapter<DictionaryDialogRecyclerViewAdapter.ViewHolder>()
{
    private var items: List<Dictionary> = listOf()
    private var prefix: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder: DictionaryDialogRecyclerViewAdapter.ViewHolder = when(viewType){
            VIEW_TYPE_EMPTY     ->  ViewHolder( TranslationDicoNoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_DICO_SELECTED -> ViewHolder( TranslationDicoSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false) )
            else                ->  ViewHolder( TranslationDicoBinding.inflate(LayoutInflater.from(parent.context), parent, false) )
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        if (viewType != VIEW_TYPE_EMPTY){
            val dico = items[position]
            holder.binding.root.setOnClickListener{onDicoClick(dico)}

            if (viewType == VIEW_TYPE_DICO_SELECTED){
                val binding = holder.binding as TranslationDicoSelectedBinding
                binding.prefix = prefix
                binding.dico = dico

            }
            else{
                val binding = holder.binding as TranslationDicoBinding
                binding.prefix = prefix
                binding.dico = dico
            }

        }

    }
    override fun getItemCount(): Int = if (items.isEmpty()) 1 else items.size

    override fun getItemViewType(position: Int): Int =
        if (items.isEmpty())  VIEW_TYPE_EMPTY
        else {
            if (selectedDico.value != null && selectedDico.value!!.id == items[position].id ) VIEW_TYPE_DICO_SELECTED
            else VIEW_TYPE_DICO
        }


    inner class ViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root)

    /**
     * Change la liste des dictionnaires
     */
    fun setNewListOfDictionaries(dictionaries: List<Dictionary>){
        items = dictionaries
        notifyDataSetChanged()
    }

    fun setNewPrefix(newPrefix: String){
        prefix = newPrefix.lowercase()
    }

    /**
     * Ajout d' un listener à un dictionnaire View
     */
    private fun onDicoClick(dico: Dictionary){
        if (selectedDico.value != null && selectedDico.value!!.id == dico.id) selectedDico.setValue(null)
        else selectedDico.setValue(dico)
        notifyDataSetChanged()
    }

    companion object{
        @JvmStatic val VIEW_TYPE_EMPTY = 0
        @JvmStatic val VIEW_TYPE_DICO = 1
        @JvmStatic val VIEW_TYPE_DICO_SELECTED = 2
    }
}