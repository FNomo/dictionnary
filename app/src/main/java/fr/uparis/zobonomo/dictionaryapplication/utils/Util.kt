package fr.uparis.zobonomo.dictionaryapplication.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import java.util.*

class Util {
    companion object{
        @JvmStatic
        fun capitalize(str: String): String{
            return str.replaceFirstChar{ it.titlecase(Locale.ROOT) }
        }

        @JvmStatic
        fun prefixHighlight(str: String, prefix: String, colorHex: String): SpannableString =
             if (prefix.isNotEmpty() && str.lowercase().startsWith(prefix) ){
                val span = SpannableString(str.replaceFirstChar { it.titlecase(Locale.ROOT) })
                 val color = Color.parseColor(colorHex)
                span.setSpan(BackgroundColorSpan(color), 0, prefix.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                 span.setSpan(ForegroundColorSpan(Color.WHITE), 0, prefix.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                span
            }else {
                SpannableString( str.replaceFirstChar{ it.titlecase(Locale.ROOT) } )
            }
        
        @JvmStatic
        fun encodeArray(array: Array<String>): String{
            var ret = ""
            for (index in array.indices) {
                ret += array[index]
                if (index < array.size - 1) ret += "//"
            }
            return ret
        }

        @JvmStatic
        fun decodeArray(str: String?): Array<String>?= str?.split(Regex("//"))?.toTypedArray()

    }
}