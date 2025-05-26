package fr.uparis.zobonomo.dictionaryapplication.translation_search

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import fr.uparis.zobonomo.dictionaryapplication.MainActivity
import fr.uparis.zobonomo.dictionaryapplication.MainViewModel
import fr.uparis.zobonomo.dictionaryapplication.database.Word
import fr.uparis.zobonomo.dictionaryapplication.databinding.TranslationWordBinding
import fr.uparis.zobonomo.dictionaryapplication.databinding.TranslationWordDicoBinding
import fr.uparis.zobonomo.dictionaryapplication.databinding.TranslationWordNoBinding
import fr.uparis.zobonomo.dictionaryapplication.translation_search.DictionaryDialogRecyclerViewAdapter.Companion.VIEW_TYPE_EMPTY
import fr.uparis.zobonomo.dictionaryapplication.utils.ViewUtil
import fr.uparis.zobonomo.dictionaryapplication.database.Word.Companion.Level


/**
 *
 */
class TranslationSearchRecyclerViewAdapter
    (
    private  val context: MainActivity,
    private val model: MainViewModel,
    )
    : RecyclerView.Adapter<TranslationSearchRecyclerViewAdapter.ViewHolder>() {
    private var items: List<Word> = listOf()
    private var prefix: String = ""

    /********************/  /********************/
    /***** Constructeurs *****/
    init {

    }

    /********************/  /********************/
    /***** Setters *****/

    /**
     * Change la liste des dictionnaires
     */
    fun setNewListOfWord(words: List<Word>){
        items = words
        notifyDataSetChanged()
    }

    /**
     * Changer le prefix de l' adapter
     */
    fun setNewPrefix(newPrefix: String){
        prefix = newPrefix.lowercase()
    }

    /********************/  /********************/
    /***** Fonctions servant pour le binding *****/

    /**
     * Servant à crée un ViewHolder en fonction de son type
     * @param viewType type de VH à générer
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("fun", "items: ${items.size}, viewType: $viewType")
        val holder: ViewHolder = when(viewType) {
            VIEW_TYPE_EMPTY -> ViewHolder(TranslationWordNoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_DICO -> ViewHolder(TranslationWordDicoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> ViewHolder(TranslationWordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        return holder
    }

    /**
     * Servant à lié un ViewHolder à une position
     * * On obtient le type du VH à la position
     * * On déduit sa View en fonction de sa position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        if (viewType == VIEW_TYPE_DICO){
            val binding = holder.binding as TranslationWordDicoBinding
            binding.dico = model.dictionarySelected.value!!
            val word = Word(prefix, model.dictionarySelected.value!!)
            binding.btnDownUp.setOnClickListener { onDropDownUpClick(binding.layout, binding.dropdownMenu) }
            binding.btnVisualize.setOnClickListener { onVisualizeWord(word.url) }
            binding.btnQuiz.setOnClickListener { onQuizWordWithDico(word) }
        }else if (viewType == VIEW_TYPE_WORD){
            val binding = holder.binding as TranslationWordBinding
            binding.prefix = prefix
            val index = if (itemCount == items.size) position else position - 1
            val word = items[index]
            binding.word = word
            binding.btnDownUp.setOnClickListener { onDropDownUpClick(binding.layout, binding.dropdownMenu) }
            binding.btnVisualize.setOnClickListener { onVisualizeWord(word.url) }
            binding.btnQuiz.setOnClickListener { onQuizWord(word) }
        }
    }

    /**
     * Avoir le compte des items à créer
     * @return Si la liste est vide 1 sinon la taille de la liste (+ 1 si un dictionnaire à été sélectionné)
     */
    override fun getItemCount(): Int =
         if  ( items.isEmpty() )1
        else { if (model.dictionarySelected.value != null && prefix.isNotEmpty()) items.size + 1 else items.size }

    /**
     * Sélectionner le type de View
     * @return [VIEW_TYPE_EMPTY] si la liste est vide sinon [VIEW_TYPE_WORD]
     */
    override fun getItemViewType(position: Int): Int =
         if (model.dictionarySelected.value != null && prefix.isNotEmpty()) {
            if (position == 0) VIEW_TYPE_DICO // Emplacement du dictionnaire
            else VIEW_TYPE_WORD
        }
         else if (items.isEmpty()) VIEW_TYPE_EMPTY
         else VIEW_TYPE_WORD


    inner class ViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root)

    /********************/  /********************/
    /***** Observers *****/

    /********************/  /********************/
    /***** Listeners *****/

    /**
     * Gere le click sur le drop down/up
     * On montre ou cache le sous menu
     */
    private fun onDropDownUpClick(layout: ViewGroup, dropdownMenu: LinearLayout){
        ViewUtil.beginAutoTransition(layout)
        if (dropdownMenu.isVisible){
            dropdownMenu.visibility = View.GONE
            layout.isSelected = false
        }else{
            dropdownMenu.visibility = View.VISIBLE
            layout.isSelected = true
        }
    }

    /**
     * Gere l' appuie sur le bouton visualize
     * * Redirige vers une page web avec un Intent
     */
    private fun onVisualizeWord(url: String){
        val webIntent = Intent(Intent.ACTION_VIEW)
        webIntent.data = Uri.parse( url )
        context.startActivity( webIntent )
    }

    /**
     * Gere le click sur le bouton d' ajout au quiz (pour un mot normal)
     * * dans ce cas, le champs level prend la valeur: [Level.TO_TRAIN]
     * * puis on met à jour le mot dans la BD
     */
    fun onQuizWord(word: Word){
        word.level = Level.TO_TRAIN.toString()
        model.updateWord(word)
    }

    /**
     * Gere le click sur le bouton quiz d' un dico mot (style dico)
     */
    private fun onQuizWordWithDico(word: Word){
        model.insertWord(word)
    }

    companion object{
        @JvmStatic val VIEW_TYPE_EMPTY = 0
        @JvmStatic val VIEW_TYPE_WORD = 1
        @JvmStatic val VIEW_TYPE_DICO = 2
    }

}