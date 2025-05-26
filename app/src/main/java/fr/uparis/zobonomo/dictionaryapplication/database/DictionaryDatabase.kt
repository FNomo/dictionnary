package fr.uparis.zobonomo.dictionaryapplication.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database(entities = [Word::class, Dictionary::class], version = 7)
abstract  class DictionaryDatabase: RoomDatabase() {
    abstract fun myDao(): MyDao

    companion object{
        @Volatile
        private var instance: DictionaryDatabase? = null

        fun getDatabase( context : Context): DictionaryDatabase{
            if( instance != null )
                return instance!!
            val database = Room.databaseBuilder( context.applicationContext,
                DictionaryDatabase::class.java , "DictionaryDatabase")
                .fallbackToDestructiveMigration()
                .build()
            instance = database
            return instance!!
        }

    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper = super.getOpenHelper()
    override fun createInvalidationTracker(): InvalidationTracker = super.getInvalidationTracker()
    override fun clearAllTables() {}
}