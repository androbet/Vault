package com.arsvechkarev.vault.features.password_checking

import android.content.Context
import android.view.ViewGroup
import com.arsvechkarev.vault.R
import com.arsvechkarev.vault.core.di.CoreComponent.Companion.instance
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewbuilding.Dimens.CornerRadiusDefault
import com.arsvechkarev.vault.viewbuilding.Dimens.IconSize
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginBig
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginDefault
import com.arsvechkarev.vault.viewbuilding.Styles.BoldTextView
import com.arsvechkarev.vault.viewbuilding.Styles.ClickableButton
import com.arsvechkarev.vault.viewbuilding.TextSizes
import com.arsvechkarev.vault.viewdsl.*
import com.arsvechkarev.vault.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.vault.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.vault.views.EditTextPassword
import com.arsvechkarev.vault.views.MaterialProgressBar
import com.arsvechkarev.vault.views.MaterialProgressBar.Thickness.NORMAL
import com.arsvechkarev.vault.views.SimpleDialog
import navigation.BaseScreen

class PasswordCheckingDialog(
    private val presenter: PasswordCheckingPresenter,
    context: Context
) : SimpleDialog(context), PasswordCheckingView {

    init {
        onOutsideClick = { context.hideKeyboard() }
        withViewBuilder {
            VerticalLayout(MatchParent, WrapContent) {
                marginHorizontal(MarginBig)
                padding(MarginDefault)
                backgroundRoundRect(CornerRadiusDefault, Colors.Dialog)
                TextView(WrapContent, WrapContent, style = BoldTextView) {
                    textSize(TextSizes.H3)
                    text(R.string.text_enter_master_password)
                }
                val textError = TextView(WrapContent, WrapContent) {
                    tag(TextErrorTag)
                    textColor(Colors.Error)
                    margins(top = MarginDefault)
                }
                val editTextPassword = child<EditTextPassword>(MatchParent, WrapContent) {
                    classNameTag()
                    setHint(R.string.hint_enter_password)
                    onTextChanged { textError.text("") }
                    onSubmit { text -> presenter.checkPassword(text) }
                }
                addView {
                    MaterialProgressBar(context, thickness = NORMAL).apply {
                        classNameTag()
                        size(IconSize, IconSize)
                        invisible()
                    }
                }
                TextView(MatchParent, WrapContent, style = ClickableButton()) {
                    tag(ContinueButtonTag)
                    text(R.string.text_continue)
                    margins(top = MarginDefault)
                    onClick { presenter.checkPassword(editTextPassword.getText()) }
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
    }

    override fun showDialog() {
        show()
        viewAs<EditTextPassword>().requestEditTextFocus()
        postDelayed({
            context.showKeyboard()
        }, 100)
    }

    override fun hideDialog() {
        hide()
        context.hideKeyboard()
        post {
            viewAs<EditTextPassword>().text("")
        }
    }

    override fun showPasswordCheckingLoading() {
        viewAs<EditTextPassword>().isEnabled = false
        textView(ContinueButtonTag).isEnabled = false
        viewAs<MaterialProgressBar>().animateVisible()
    }

    override fun showPasswordCheckingFinished() {
        viewAs<EditTextPassword>().isEnabled = true
        textView(ContinueButtonTag).isEnabled = true
        viewAs<MaterialProgressBar>().animateInvisible()
    }

    override fun showPasswordIsIncorrect() {
        textView(TextErrorTag).text(context.getString(R.string.text_password_is_incorrect))
    }

    companion object {

        private const val TextErrorTag = "TextError"
        private const val ContinueButtonTag = "ContinueButton"

        val BaseScreen.passwordCheckingDialog get() = viewAs<PasswordCheckingDialog>()

        fun ViewGroup.PasswordCheckingDialog(block: PasswordCheckingDialog.() -> Unit = {}) =
            withViewBuilder {
                val presenter =
                    instance.getPasswordCheckingComponentFactory().create().providePresenter()
                val infoDialog = PasswordCheckingDialog(presenter, context)
                infoDialog.size(MatchParent, MatchParent)
                infoDialog.classNameTag()
                addView(infoDialog)
                infoDialog.apply(block)
            }
    }
}