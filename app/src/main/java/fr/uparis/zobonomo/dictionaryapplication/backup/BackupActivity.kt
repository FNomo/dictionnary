package fr.uparis.zobonomo.dictionaryapplication.backup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import fr.uparis.zobonomo.dictionaryapplication.ScreenSlidePagerAdapter
import fr.uparis.zobonomo.dictionaryapplication.databinding.ActivityBackupBinding

class BackupActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBackupBinding.inflate(layoutInflater) }
    private val model by lazy { ViewModelProvider(this)[BackUpViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        linkPagerToTabLayout()
        loadTextFromIntent()

        val wordFragment = BackupWordFragment.newInstance()
        val dictionaryFragment = BackupDictionaryFragment.newInstance()
        binding.pager.adapter = ScreenSlidePagerAdapter(this, mutableListOf(wordFragment, dictionaryFragment) )
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.pager.currentItem == 0) finish()
        else binding.pager.currentItem = binding.pager.currentItem - 1
    }

    /**
     * Sert à lié le ViewPager et le TabLayout
     */
    private fun linkPagerToTabLayout(){
        binding.pager.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) { binding.tabLayout.getTabAt(position)!!.select() }
        })

        binding.tabLayout.addOnTabSelectedListener( object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { binding.pager.currentItem = tab.position }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadTextFromIntent(){
        if( intent.action.equals( "android.intent.action.SEND" ) ){
            val txt = intent.extras?.getString( "android.intent.extra.TEXT" )
            model.url.postValue(txt)
            Log.d("Fun loadTextFromIntent", "Text: $txt")
        }
    }
}