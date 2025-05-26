package fr.uparis.zobonomo.dictionaryapplication.memory_exercice

import android.app.Application
import androidx.lifecycle.MutableLiveData
import fr.uparis.zobonomo.dictionaryapplication.DictionaryApplication
import fr.uparis.zobonomo.dictionaryapplication.database.Word

class QuizModel(application: Application){
    private val dao = (application as DictionaryApplication).database.myDao()

    /* Propriétés */
    val listOfWords: MutableLiveData< List<Word> > = MutableLiveData()

    /* Chargements */

    fun loadListOfWords(count: Int){
        Thread{
            try {
                Thread{
                    listOfWords.postValue(dao.loadWordsByLevel(count))
                }.start()
            }catch (error: Exception){}
        }.start()
    }

    /* Modification */
}