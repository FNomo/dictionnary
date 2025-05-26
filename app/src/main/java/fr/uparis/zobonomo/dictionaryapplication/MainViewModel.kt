package fr.uparis.zobonomo.dictionaryapplication

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import fr.uparis.zobonomo.dictionaryapplication.database.Dictionary
import fr.uparis.zobonomo.dictionaryapplication.database.Word

/**
 * Définit le viewModel Principale de l' application
 * * Celui utiliser par l' activité principale
 * * Il maintient la cohérence dse données aussi lors de changement de configuration
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as DictionaryApplication).database.myDao() // Sert à passer les requête à la BD

    /* Variable pour la cohérence */
    /** Le dictionnaire courant sélectionné */
    val dictionarySelected: MutableLiveData<Dictionary?> = MutableLiveData()
    /** La recherche de dictionnaire */
    val dictionarySearch: MutableLiveData<String> = MutableLiveData()
    /** La recherche de mot */
    val wordSearch: MutableLiveData<String> = MutableLiveData()
    /** Liste de dictionnaire obtenu avec [dictionarySearch] */
    val dictionaryList: MutableLiveData< List<Dictionary> > = MutableLiveData()
    /** Liste de mots obtenu avec [wordSearch] */
    val wordList: MutableLiveData< List<Word> > = MutableLiveData()
    /** Résultat de la mise à jour d' un mot */
    val updateWordResult: MutableLiveData<Int> = MutableLiveData()
    /** Résultat de l' insertion d' un mot dans la BD */
    val insertWordResult: MutableLiveData< Pair<Long, Word> > = MutableLiveData()
    /** Résultat de la suppression d' un mot de la BD */
    val deleteWordResult: MutableLiveData< Int > = MutableLiveData()
    /** Résultat de la suppression complète de mots */
    val deleteAllWordsResult: MutableLiveData<Int> = MutableLiveData()
    /** Résultat de la suppression complète de mots */
    val deleteAllDicosResult: MutableLiveData<Int> = MutableLiveData()
    /** Résultat de la sélection de langues des mots */
    val languagesPerDialog: MutableLiveData< Pair<View, List<Word>> > = MutableLiveData()

    /* Gérer les Dictionnaires */

    /**
     * Charger tout les dictionnaires de la BD
     */
    fun loadAllDictionaries(){
        Thread{
            try {
                dictionaryList.postValue( dao.loadAllDictionaries() )
            }catch (error: Exception){ Log.e("Load Error", "Cannot load all Dictionaries.") }
        }.start()
    }

    /**
     * Charger les dictionnaires par leurs noms
     * @param name le filtre de chargement
     */
    fun loadDictionariesByName(name: String){
        Thread{
            try {
                dictionaryList.postValue( dao.loadDictionariesByName(name) )
            }catch (error: Exception){ Log.e("Load Error", "Cannot load Dictionaries by name.") }
        }.start()
    }

    /**
     * Insertion de plusieurs dicos dans la BD
     * @param dicos liste de dicos à insérer
     */
    fun insertDicos(dicos: List<Dictionary>){
        Thread{
            try{ dao.insertDico(*dicos.toTypedArray())}
            catch (err: Exception){ Log.e("Insert Error", "Cannot add all the Dictionaries in the database.") }
        }.start()
    }

    /**
     * Suppression de tous les dictionnaires de la base données
     */
    fun deleteAllDicos(){
        Thread{
            try{
                deleteAllDicosResult.postValue( dao.deleteAllDicos() )
            }catch (error: Exception){ Log.e("Delete Error", "Cannot delete all Dictionaries.") }
        }.start()
    }

    /* Gérer les Mots */

    /**
     * Charger toute les langue de la BD
     * mets le résultat dans [languagesPerDialog]
     */
    fun loadAllLanguages(dialog: View){
        Thread{
            try {
                val languagesList = dao.loadAllLanguages()
                val pair = Pair(dialog, languagesList)
                languagesPerDialog.postValue(pair)
            }catch (error: Exception){ Log.e("Load Error", "Cannot load all languages.") }
        }.start()
    }

    /**
     * Charger tout les mots de la BD
     */
    fun loadAllWords(){
        Thread{
            try {
                wordList.postValue( dao.loadAllWords() )
            }catch (error: Exception){ Log.e("Load Error", "Cannot load all words.") }
        }.start()
    }

    /**
     * Charger les mots avec un filtre
     * @param name le filtre de chargement
     */
    fun loadWordsByName(name: String){
        Thread{
            try {
                wordList.postValue( dao.loadWordsByName(name) )
            }catch (error: Exception){ Log.e("Load Error", "Cannot load words with the given name: \"$name\".") }
        }.start()
    }

    /**
     * Mettre à jour un mot
     * @param word le mot à mettre à jour
     */
    fun updateWord(word: Word){
        Thread{
            try {
                updateWordResult.postValue( dao.updateWord(word) )
            }catch (error: Exception){ Log.e("Update Error", "Cannot update the given word.") }
        }.start()
    }

    /**
     * Insertion d' un mot dans la BD
     * @param word le mot à insérer
     */
    fun insertWord(word: Word){
        Thread{
            try {
                val insertId = dao.insertWord(word)[0]
                insertWordResult.postValue( Pair( insertId, word ) )
            }catch (error: Exception){ Log.e("Insert Error", "Cannot insert the given word.") }
        }.start()
    }

    /**
     * Insertion de plusieurs mot dans la BD
     * @param words liste de mot à insérer
     */
    fun insertWords(words: List<Word>){
        Thread{
            try{dao.insertWord(*words.toTypedArray())}
            catch (err: Exception){ Log.e("Insert Error", "Cannot insert the given words.") }
        }.start()
    }

    /**
     * Suppression d' un mot de la BD
     * @param word le mot à supprimé
     */
    fun deleteWord(word: Word){
        Thread{
            try {
                deleteWordResult.postValue( dao.deleteWord(word) )
            }catch (error: Exception){ Log.e("Delete Error", "Cannot delete the given word.") }
        }.start()
    }

    /**
     * Suppression de tous les mots de la base données
     */
    fun deleteAllWords(){
        Thread{
            try{
                deleteAllWordsResult.postValue( dao.deleteAllWords() )
            }catch (error: Exception){ Log.e("Delete Error", "Cannot delete all Words.") }
        }.start()
    }
}