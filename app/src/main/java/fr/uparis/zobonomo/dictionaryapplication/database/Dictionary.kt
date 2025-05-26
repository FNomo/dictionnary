package fr.uparis.zobonomo.dictionaryapplication.database

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "dictionaries",
    indices = [
        Index( value = [ "name", "source", "destination" ], unique = true),
        Index( value = [ "url" ], unique = true)
    ] )
data class Dictionary (
    @JvmField val name: String,
    @JvmField val source: String,
    @JvmField val destination: String,
    @JvmField val url: String,
        ){
    @JvmField @PrimaryKey(autoGenerate = true) var id: Long = 0
    @JvmField var date: Long = Date().time
}