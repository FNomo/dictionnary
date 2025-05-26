package fr.uparis.zobonomo.dictionaryapplication.database

import androidx.room.*

@Entity(
    primaryKeys = ["origin", "url"],
    tableName = "words",
    foreignKeys = [ ForeignKey(
        entity = Dictionary::class,
        parentColumns = ["id"],
        childColumns = [ "dictionary_id" ],
        deferred = true,
        onDelete = ForeignKey.CASCADE
    ) ],
    indices = [ Index( value = [ "origin", "url" ], unique = true) , Index( value = [ "origin", "url",  "dictionary_id" ], unique = true) ]
)
data class Word (
    /** Le mot d' origine */
    @JvmField val origin: String
){
    /** lien direct vers la Page web du mot */
    @JvmField var url: String = ""
    /** Niveau de maître du mot */
    @JvmField var level: String = Level.NEW.toString()
    /** Date d' ajout du mot */
    @JvmField var date: Long = java.util.Date().time
    /** Traduction du mot en dure ( si le mot est un mot ajouté par le l' activité de sauvegarde) */
    @JvmField var translation: String? = null
    /** Langue source du mot en dure ( si le mot est un mot ajouté par le l' activité de sauvegarde) */
    @JvmField var source: String? = null
    /** Langue de traduction du mot en dure ( si le mot est un mot ajouté par le l' activité de sauvegarde) */
    @JvmField var destination: String? = null
    @JvmField @ColumnInfo(name = "dictionary_id") var dictionaryId: Long? = null

    /**
     * Constructeur pour construire un mot via l' activité de sauvegarde
     * @param origin mot d' origine
     * @param translation mot traduit
     * @param source langue source
     * @param destination langue de traduction
     * @param url lien vers la page Web contenant la traduction
     */
    constructor(origin: String, translation: String, source: String, destination: String, url: String): this(origin){
        this.url = url
        this.translation = translation
        this.source = source
        this.destination = destination
    }

    /**
     * Constructeur pour construire un mot à l' aide d 'un dictionnaire
     * @param origin mot d' origine
     * @param dictionary le dictionnaire
     */
    constructor(origin: String, dictionary: Dictionary): this(origin){
        this.dictionaryId = dictionary.id
        this.source = dictionary.source
        this.destination = dictionary.destination
        this.url = dictionary.url + "/" + origin
    }

    companion object{
        /**
         * Définit la liste des niveaux
         * @property NEW le mot viens d' être créer
         * @property VIEWED TODO: à implémenter
         * @property TO_TRAIN Demande d' ajout au quiz
         * @property TRAINING Le mot est en cours d' entraînement
         * @property COMPLETE Le mot est compléter
         * @property MASTERY Le mot est maîtrisé
         */
        enum class Level{
            @JvmStatic NEW,
            VIEWED,
            TO_TRAIN,
            TRAINING,
            COMPLETE,
            @JvmStatic MASTERY
        }
    }
}