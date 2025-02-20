package com.arsvechkarev.vault.viewbuilding

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.text.TextUtils
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.arsvechkarev.vault.R
import com.arsvechkarev.vault.viewbuilding.Dimens.CornerRadiusSmall
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginDefault
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginSmall
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginVerySmall
import com.arsvechkarev.vault.viewdsl.*

@Suppress("FunctionName")
object Styles {

    val BaseTextView: TextView.() -> Unit = {
        textSize(TextSizes.H5)
        font(Fonts.SegoeUi)
        textColor(Colors.TextPrimary)
        ellipsize = TextUtils.TruncateAt.END
    }

    val BoldTextView: TextView.() -> Unit = {
        apply(BaseTextView)
        font(Fonts.SegoeUiBold)
        textSize(TextSizes.H4)
    }

    val TitleTextView: TextView.() -> Unit = {
        apply(BoldTextView)
        textSize(TextSizes.H0)
    }

    fun ClickableTextView(
        rippleColor: Int = Colors.AccentRipple,
    ): TextView.() -> Unit = {
        textSize(TextSizes.H5)
        font(Typeface.DEFAULT_BOLD)
        paddingVertical(MarginSmall)
        textColor(Colors.AccentLight)
        paddingHorizontal(MarginDefault)
        rippleBackground(rippleColor, Colors.Transparent, CornerRadiusSmall)
    }

    val ClickableErrorTextView: TextView.() -> Unit = {
        apply(ClickableTextView(Colors.ErrorRipple))
        textColor(Colors.Error)
    }

    fun ClickableButton(
        colorStart: Int = Colors.Accent,
        colorEnd: Int = Colors.AccentLight,
    ): TextView.() -> Unit = {
        apply(BoldTextView)
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BL_TR, intArrayOf(
                colorStart, colorEnd
            )
        )
        val r = 120.dp.toFloat()
        val outerRadii = floatArrayOf(r, r, r, r, r, r, r, r)
        gradientDrawable.cornerRadii = outerRadii
        val roundRectShape = RoundRectShape(outerRadii, null, null)
        val maskRect = ShapeDrawable().apply {
            shape = roundRectShape
            paint.color = Colors.Ripple
        }
        val colorStateList = ColorStateList.valueOf(Colors.Ripple)
        background(RippleDrawable(colorStateList, gradientDrawable, maskRect))
        paddingVertical(MarginVerySmall)
        paddingHorizontal(MarginDefault)
        textSize(TextSizes.H3)
        gravity(Gravity.CENTER)
        isClickable = true
        isFocusable = true
    }

    val ImageBack: ImageView.() -> Unit = {
        image(R.drawable.ic_back)
        padding(Dimens.IconPadding)
        circleRippleBackground()
    }

    val BaseEditText: EditText.() -> Unit = {
        font(Fonts.SegoeUi)
        textSize(TextSizes.H3)
        paddingVertical(MarginSmall)
        setSingleLine()
        inputType = TYPE_TEXT_VARIATION_PASSWORD
        imeOptions = EditorInfo.IME_ACTION_DONE
    }
}