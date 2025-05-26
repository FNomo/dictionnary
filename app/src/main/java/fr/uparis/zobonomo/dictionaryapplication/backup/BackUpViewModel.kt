package fr.uparis.zobonomo.dictionaryapplication.backup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import fr.uparis.zobonomo.dictionaryapplication.DictionaryApplication
import fr.uparis.zobonomo.dictionaryapplication.database.Dictionary
import fr.uparis.zobonomo.dictionaryapplication.database.Word

class BackUpViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as DictionaryApplication).database.myDao() // Sert à passer les requête à la BD

    val url: MutableLiveData<String> = MutableLiveData()
    val insertedWordInfo : MutableLiveData< Pair<Long,Word> > = MutableLiveData()
    val insertedDicoInfo : MutableLiveData< Pair<Long,Dictionary> > = MutableLiveData()
    val deleteWordResult: MutableLiveData<Int> = MutableLiveData()
    val deleteDicoResult: MutableLiveData<Int> = MutableLiveData()

    /* **************************************** */
    /* Action d' insertion */

    fun insertWordBySource(word: Word){
        Thread {
            try {
                val insertedId = dao.insertWord( word )[0]
                insertedWordInfo.postValue( Pair(insertedId, word) )
            }catch (error: java.lang.Exception){
                error.printStackTrace()
                insertedWordInfo.postValue( Pair(NO_ACTION, word) )
            }
        }.start()
    }

    fun insertDicoBySource(dico: Dictionary){
        Thread {
            try {
                val insertedId = dao.insertDico( dico )[0]
                insertedDicoInfo.postValue( Pair(insertedId, dico) )
            }catch (error: java.lang.Exception){
                insertedDicoInfo.postValue( Pair(NO_ACTION, dico) )
            }
        }.start()

    }

    /* **************************************** */
    /* Action de suppression */

    fun deleteWord(word: Word){
        Thread{
            deleteWordResult.postValue(dao.deleteWord(word))
        }.start()
    }

    fun deleteDico(dico: Dictionary){
        Thread{
            deleteDicoResult.postValue(dao.deleteDico(dico))
        }.start()
    }

    companion object{
        const val NO_ACTION = -1L
    }
}