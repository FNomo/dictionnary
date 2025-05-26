package fr.uparis.zobonomo.dictionaryapplication

import android.app.Application
import fr.uparis.zobonomo.dictionaryapplication.database.DictionaryDatabase

class DictionaryApplication: Application() {
    val database by lazy{
        DictionaryDatabase.getDatabase(this)
    }
}