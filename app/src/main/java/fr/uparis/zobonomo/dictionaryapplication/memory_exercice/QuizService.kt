package fr.uparis.zobonomo.dictionaryapplication.memory_exercice

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder

/**
 * Service servant gérer la fonctionnalité de quiz de notre application
 * * A une fréquence donné on propose un nombre de mot donnéé
 * * On fait cela jusqu à ce que l' utilisateur à ecarter tout les mots.
 * @property model Objet servant à interagir avec la BD
 * @property pendingIntent Le prochain intent que le service à invoqué
 */
class QuizService : Service() {
    /****************************************************************************************************/
    /********** Propriété **********/
    /****************************************************************************************************/

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private lateinit var sharedPreferences: SharedPreferences
    private val alarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private val model: QuizModel = QuizModel(application)
    private var pendingIntent: PendingIntent? = null

    /****************************************************************************************************/
    /********** Constantes **********/
    /****************************************************************************************************/

    companion object{
        /* notifications */
        @JvmStatic val ACTION_DEFAULT = 1
        @JvmStatic val DEFAULT_CHANNEL_ID = "myQuizChannel"
        @JvmStatic val DEFAULT_CHANNEL_NAME = "Quiz Channel"
        @JvmStatic val DEFAULT_NOTIFICATION_ID = 1
        @JvmStatic val DEFAULT_NOTIFICATION_CODE = 1

        /* actions */
        @JvmStatic val ACTION_START = "MyQuizService.action.start"
        @JvmStatic val ACTION_CONTINUE = "MyQuizService.action.restart"
        @JvmStatic val ACTION_STOP = "MyQuizService.action.stop"

        /* états */
        enum class State{
            STOPPED, RUNNING
        }

        /* défaut */
        @JvmStatic val DEFAULT_QUIZ_LENGTH = 10
    }

    /****************************************************************************************************/
    /********** init **********/
    /****************************************************************************************************/

    /**
     * Création du service
     */
    override fun onCreate() {
        super.onCreate()
        sharedPreferences = this.getSharedPreferences("MyDictionaryPreferences" , Context.MODE_PRIVATE)
        //initMessagesArray()
        createNotificationChannel()
    }

    /**
     * Cree le channel de notification
     */
    private fun createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val myDescription = "This Channel contains a list of messages."
            val channel = NotificationChannel(DEFAULT_CHANNEL_ID , DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply { description = myDescription }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /****************************************************************************************************/
    /********** Observers **********/
    /****************************************************************************************************/


    /****************************************************************************************************/
    /********** Start **********/
    /****************************************************************************************************/

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}