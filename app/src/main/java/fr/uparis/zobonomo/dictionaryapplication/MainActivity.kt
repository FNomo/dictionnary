package fr.uparis.zobonomo.dictionaryapplication

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import fr.uparis.zobonomo.dictionaryapplication.databinding.ActivityMainBinding
import fr.uparis.zobonomo.dictionaryapplication.settings.SettingsFragment
import fr.uparis.zobonomo.dictionaryapplication.translation_search.TranslationSearchFragment
import fr.uparis.zobonomo.dictionaryapplication.utils.Default

/**
 * Activité principale de notre application
 * @property binding Le lien entre le layout et l' activité
 * @property model le line entre l' activité et la Base de donnée
 */
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val model by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val settingsFragment = SettingsFragment(getSharedPreferences("myPreferences", MODE_PRIVATE))
        val translationSearchFragment = TranslationSearchFragment.newInstance()

        // init link
        binding.pager.adapter = ScreenSlidePagerAdapter(this, mutableListOf(translationSearchFragment, settingsFragment) )
        linkPagerToBottomNavigationBar()

        //defaultDatabase()
    }

    /**
     * Sert à retourné le root de l' activité
     */
    fun getRoot(): View{
        return binding.root
    }

    /********************/  /********************/
    /***** Fonctions servant modifié le comportement par défaut *****/

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.pager.currentItem == 0) finish()
        else binding.pager.currentItem = binding.pager.currentItem - 1
    }

    /**
     * Fonction servant à redémarrer complètement l' activité
     */
    fun restart(){
        finish()
        startActivity(intent)
    }

    /**
     * Sert à retirer le focus de l'EditText quand on click hors de celui-ci
     * url : https://www.codegrepper.com/code-examples/java/how+to+remove+focus+of+edittext+in+android+when+click+outside
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }

        }
        return super.dispatchTouchEvent(event)
    }

    /********************/  /********************/
    /***** Fonctions servant à lié 2 éléments *****/

    /** Sert à lié le ViewPager et le TabLayout */
    private fun linkPagerToBottomNavigationBar(){
        binding.pager.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        binding.bottomNavigation.selectedItemId = R.id.page_translation
                        binding.fabSearch.show()
                    }
                    1 -> {
                        binding.bottomNavigation.selectedItemId = R.id.page_settings
                        binding.fabSearch.hide()
                    }
                }
            }
        })

        @Suppress("Deprecated in Java")
        binding.bottomNavigation.setOnNavigationItemSelectedListener{ item ->
            when(item.itemId) {
                R.id.page_translation -> {
                    binding.pager.currentItem = 0
                    true
                }
                R.id.page_settings -> {
                    binding.pager.currentItem = 1
                    true
                }
                else -> false
            }
        }
    }

    /********************/  /********************/
    /***** Fonctions servant de test *****/

    /**
     * Cette fonction sert à initialisé la Base de donnée avec des éléments par défaut
     */
    private fun defaultDatabase(){
        val listOfWords = Default.generateListOfWords()
        val listOfDicos = Default.generateListOfDico()
        model.insertWords(listOfWords)
        model.insertDicos(listOfDicos)
    }
}