package fr.uparis.zobonomo.dictionaryapplication.utils

import fr.uparis.zobonomo.dictionaryapplication.database.Dictionary
import fr.uparis.zobonomo.dictionaryapplication.database.Word

/**
 * Cette classe sert à générer des éléments de la Base de données par défaut pour pouvoir la tester lors de l' exam
 */
class Default {
    companion object{
        /**
         * Cette fonction génère une liste de mots que l' on va ajouter à la BD
         */
        @JvmStatic
        fun generateListOfWords(): List<Word>{
            return listOf(
                Word("bonjour", "hello", "french", "english", "https://www.larousse.fr/dictionnaires/francais-anglais/bonjour"),
                Word("merci", "thank you", "french", "english", "https://www.larousse.fr/dictionnaires/francais-anglais/merci"),
                Word("manger", "food", "french", "english", "https://www.larousse.fr/dictionnaires/francais-anglais/manger"),
                Word("hello", "bonjour", "english", "french", "https://www.larousse.fr/dictionnaires/anglais-francais/hello"),
                Word("thank you", "merci", "english", "french", "https://www.larousse.fr/dictionnaires/anglais-francais/thank_you"),
                Word("food", "manger", "english", "french", "https://www.larousse.fr/dictionnaires/anglais-francais/food"),
                Word("bonjour", "guten tag", "french", "german", "https://www.larousse.fr/dictionnaires/francais-allemand/bonjour"),
                Word("merci", "dank", "french", "german", "https://www.larousse.fr/dictionnaires/francais-allemand/merci"),
                Word("manger", "essen", "french", "german", "https://www.larousse.fr/dictionnaires/francais-allemand/manger"),
                Word("bonjour", "こんにちは", "french", "japanese", "http://dictionnaire.reverso.net/francais-japonais/bonjour"),
                Word("panier", "バスケット", "french", "japanese", "http://dictionnaire.reverso.net/francais-japonais/panier")
            )
        }

        @JvmStatic
        fun generateListOfDico(): List<Dictionary>{
            return listOf(
                Dictionary("larousse", "french", "english", "https://www.larousse.fr/dictionnaires/francais-anglais"),
                Dictionary("larousse", "english", "french", "https://www.larousse.fr/dictionnaires/anglais-francais"),
                Dictionary("larousse", "french", "german", "https://www.larousse.fr/dictionnaires/francais-allemand"),
                Dictionary("larousse", "german", "french", "https://www.larousse.fr/dictionnaires/allemand-francais"),
                Dictionary("larousse", "french", "spanish", "https://www.larousse.fr/dictionnaires/francais-espagnol"),
                Dictionary("larousse", "spanish", "french", "https://www.larousse.fr/dictionnaires/espagnol-francais"),
                Dictionary("reverso", "french", "english", "http://dictionnaire.reverso.net/francais-anglais"),
                Dictionary("reverso", "french", "spanish", "http://dictionnaire.reverso.net/francais-espagnol"),
                Dictionary("reverso", "french", "portuguese", "http://dictionnaire.reverso.net/francais-portugais"),
                Dictionary("reverso", "french", "german", "http://dictionnaire.reverso.net/francais-allemand"),
                Dictionary("reverso", "french", "japanese", "http://dictionnaire.reverso.net/francais-japonais"),
                Dictionary("cambridge", "french", "english", "https://dictionary.cambridge.org/fr/dictionnaire/francais-anglais")
            )
        }

    }
}