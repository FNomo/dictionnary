package fr.uparis.zobonomo.dictionaryapplication.database

import androidx.room.*

@Dao
interface MyDao {
    /* **************************************** */
    /* INSERTION */

    /**
     * Insert un mot et retourne la liste des identifiant insérer
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertWord(vararg word: Word):List<Long>

    /**
     * Insert un dictionnaire et retourne la liste des identifiant insérer
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertDico(vararg dico: Dictionary):List<Long>

    /* **************************************** */
    /* SUPPRESSION */

    /**
     * Supprime un mot dans la table
     */
    @Delete
    fun deleteWord(word: Word): Int

    /**
     * Supprime tous les mots de la base de donnée
     */
    @Query("DELETE FROM words")
    fun deleteAllWords(): Int

    /**
     * Supprime un dictionnaire dans la table
     */
    @Delete
    fun deleteDico(dico: Dictionary): Int

    /**
     * Supprime tous les dictionnaires de la base de donnée
     */
    @Query("DELETE FROM dictionaries")
    fun deleteAllDicos(): Int

    /* **************************************** */
    /* SELECTION */

    /**
     * Selection de dictionnaires
     * @return LiveData de la liste de tous les dictionnaires.
     */
    @Query("SELECT * FROM dictionaries")
    fun loadAllDictionaries(): List<Dictionary>

    /**
     * Selection de dictionnaires
     * @return liste de tous les dictionnaires dont le nom commence par nom
     */
    @Query("SELECT * FROM dictionaries WHERE name LIKE :name|| '%' OR source LIKE :name|| '%' OR destination LIKE :name|| '%' ")
    fun loadDictionariesByName(name: String): List<Dictionary>

    /**
     * Selection de mots
     * @return LiveData de la liste de tous les mots.
     */
    @Query("SELECT * FROM words WHERE dictionary_id IS NULL")
    fun loadAllWords(): List<Word>

    /**
     * Selection de mots
     * @return liste de tous les mots dont le nom commence par nom
     */
    @Query("SELECT * FROM words WHERE origin = :name AND dictionary_id IS NULL")
    fun loadWordsByName(name: String): List<Word>

    /**
     * Selection les mots utilisés pour le quiz
     * @param limit le nombre maximum de mot dans la liste
     * @return liste de mots ( avec en priorité les mots à questionné, puis choisit aléatoirement)
     */
    @Query(
        "SELECT * FROM words WHERE level <> \"MASTERY\" ORDER BY CASE " +
                "WHEN level = \"TO_TRAIN\" THEN 1 " +
                "WHEN level = \"TRAINING\" THEN RANDOM() % 100 + 5 " +
                "ELSE  RANDOM() % 100 + 25 " +
        "END ASC LIMIT :limit"
    )
    fun loadWordsByLevel(limit: Int): List<Word>

    /**
     * Sélectionne tous les languages des mots de la base donnée
     * @return la liste des languages
     */
    @Query("SELECT * FROM words GROUP BY source, destination")
    fun loadAllLanguages(): List<Word>

    /* **************************************** */
    /* MIS A JOUR */

    /**
     * Met à jour un mot dans la BD
     * @return le nombre de mot mis à jours
     */
    @Update
    fun updateWord(word: Word): Int

}