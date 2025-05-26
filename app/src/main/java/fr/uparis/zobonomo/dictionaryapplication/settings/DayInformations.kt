package fr.uparis.zobonomo.dictionaryapplication.settings

/**
 * Cette classe représente les informations à passer pour le ViewBinding
 * @property day le jour du paramètre
 * @property language la langue pour le jour day
 */
data class DayInformations(
    val day: String,
    val language: String
)