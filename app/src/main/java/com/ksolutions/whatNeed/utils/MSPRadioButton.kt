package com.shopper.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.res.ResourcesCompat
import com.ksolutions.whatNeed.R

/**
 * This class will be used for Custom font text using the Radio Button which inherits the AppCompatRadioButton class.
 */
class MSPRadioButton(context: Context, attrs: AttributeSet) :
    AppCompatRadioButton(context, attrs) {

    /**
     * The init block runs every time the class is instantiated.
     */
    init {
        // Call the function to apply the font to the components.
        applyFont()
    }

    /**
     * Applies a font to a Radio Button.
     */
    private fun applyFont() {

        // This is used to get the file from the assets folder and set it to the title textView.
        val typeface: Typeface? = ResourcesCompat.getFont(context, R.font.allerta_stencil)
        setTypeface(typeface)
    }
}