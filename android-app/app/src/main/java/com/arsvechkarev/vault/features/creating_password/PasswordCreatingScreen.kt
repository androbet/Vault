package com.arsvechkarev.vault.features.creating_password

import android.content.Context
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import buisnesslogic.DEFAULT_PASSWORD_LENGTH
import buisnesslogic.MAX_PASSWORD_LENGTH
import buisnesslogic.MIN_PASSWORD_LENGTH
import buisnesslogic.PasswordStrength
import buisnesslogic.model.PasswordCharacteristics
import buisnesslogic.model.PasswordCharacteristics.*
import com.arsvechkarev.vault.R
import com.arsvechkarev.vault.core.di.CoreComponent
import com.arsvechkarev.vault.core.extensions.moxyPresenter
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewbuilding.Dimens
import com.arsvechkarev.vault.viewbuilding.Dimens.ImageBackMargin
import com.arsvechkarev.vault.viewbuilding.Dimens.MarginSmall
import com.arsvechkarev.vault.viewbuilding.Styles
import com.arsvechkarev.vault.viewbuilding.Styles.BoldTextView
import com.arsvechkarev.vault.viewbuilding.TextSizes
import com.arsvechkarev.vault.viewdsl.*
import com.arsvechkarev.vault.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.vault.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.vault.views.CheckmarkAndTextViewGroup
import com.arsvechkarev.vault.views.CheckmarkAndTextViewGroup.Companion.CheckmarkAndTextViewGroup
import com.arsvechkarev.vault.views.PasswordStrengthMeterWithText
import com.arsvechkarev.vault.views.dialogs.InfoDialog.Companion.InfoDialog
import com.arsvechkarev.vault.views.dialogs.InfoDialog.Companion.infoDialog
import com.arsvechkarev.vault.views.dialogs.LoadingDialog
import com.arsvechkarev.vault.views.dialogs.loadingDialog
import navigation.BaseScreen

class PasswordCreatingScreen : BaseScreen(), PasswordCreatingView {

    override fun buildLayout(context: Context) = context.withViewBuilder {
        RootFrameLayout {
            tag(DialogPassword)
            backgroundColor(Colors.Background)
            VerticalLayout(MatchParent, MatchParent) {
                FrameLayout(MatchParent, WrapContent) {
                    margins(top = MarginSmall + StatusBarHeight)
                    TextView(WrapContent, WrapContent, style = BoldTextView) {
                        tag(DialogPasswordTitle)
                        textSize(TextSizes.H1)
                        layoutGravity(CENTER)
                    }
                    ImageView(WrapContent, WrapContent) {
                        image(R.drawable.ic_cross)
                        circleRippleBackground()
                        margins(end = ImageBackMargin, top = MarginSmall, bottom = MarginSmall)
                        layoutGravity(CENTER or Gravity.END)
                        padding(Dimens.IconPadding)
                        onClick { presenter.onCloseClicked() }
                    }
                }
                TextView(WrapContent, WrapContent, style = Styles.BaseTextView) {
                    tag(DialogPasswordTextError)
                    layoutGravity(CENTER)
                    gravity(CENTER)
                    drawablePadding(Dimens.MarginDefault)
                    textColor(Colors.Error)
                }
                EditText(MatchParent, WrapContent, style = Styles.BaseEditText) {
                    tag(DialogPasswordEditText)
                    gravity(CENTER)
                    margin(MarginSmall)
                    setMaxLength(MAX_PASSWORD_LENGTH)
                    isSingleLine = false
                    onSubmit { presenter.onSavePasswordClicked() }
                }
                child<PasswordStrengthMeterWithText>(MatchParent, WrapContent) {
                    classNameTag()
                    margin(Dimens.MarginDefault)
                }
                val commonBlock: CheckmarkAndTextViewGroup.() -> Unit = {
                    onClick {
                        val characteristics = when (id) {
                            R.string.text_uppercase_symbols -> UPPERCASE_SYMBOLS
                            R.string.text_numbers -> NUMBERS
                            R.string.text_special_symbols -> SPECIAL_SYMBOLS
                            else -> throw IllegalStateException("Unknown tag")
                        }
                        presenter.onCheckmarkClicked(characteristics)
                    }
                }
                CheckmarkAndTextViewGroup(R.string.text_uppercase_symbols, commonBlock)
                CheckmarkAndTextViewGroup(R.string.text_numbers, commonBlock)
                CheckmarkAndTextViewGroup(R.string.text_special_symbols, commonBlock)
                TextView(WrapContent, WrapContent, style = BoldTextView) {
                    tag(DialogPasswordTextLength)
                    margins(start = Dimens.MarginDefault, top = Dimens.MarginBig)
                    textSize(TextSizes.H3)
                    text(context.getString(R.string.text_password_length, DEFAULT_PASSWORD_LENGTH))
                }
                child<SeekBar>(MatchParent, WrapContent) {
                    classNameTag()
                    margin(Dimens.MarginDefault)
                    max = MAX_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH
                    progress = DEFAULT_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH
                    onProgressChanged { presenter.onPasswordLengthChanged(it) }
                }
                TextView(WrapContent, WrapContent, style = Styles.ClickableTextView()) {
                    margins(top = Dimens.MarginDefault)
                    layoutGravity(Gravity.CENTER_HORIZONTAL)
                    gravity(CENTER)
                    drawables(start = R.drawable.ic_generate, color = Colors.AccentLight)
                    drawablePadding(MarginSmall)
                    textColor(Colors.AccentLight)
                    text(context.getString(R.string.text_generate_password))
                    onClick { presenter.onGeneratePasswordClicked() }
                }
            }
            TextView(WrapContent, WrapContent, style = Styles.ClickableButton()) {
                layoutGravity(CENTER or Gravity.BOTTOM)
                margin(Dimens.MarginDefault)
                textSize(TextSizes.H3)
                text(R.string.text_save_password)
                onClick { presenter.onSavePasswordClicked() }
            }
            InfoDialog()
            LoadingDialog()
        }
    }

    private val presenter: PasswordCreatingPresenter by moxyPresenter {
        CoreComponent.instance.getPasswordCreatingComponentFactory().create().getPresenter()
    }

    override fun onInit() {
        editText(DialogPasswordEditText).onTextChanged { presenter.onPasswordChanged(it) }
    }

    override fun onAppearedOnScreenAfterAnimation() {
        contextNonNull.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        contextNonNull.hideKeyboard()
    }

    override fun onDisappearedFromScreen() {
        contextNonNull.hideKeyboard()
    }

    override fun onDisappearedFromScreenAfterAnimation() {
        contextNonNull.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun showCreatingPasswordMode() {
        viewAs<TextView>(DialogPasswordTitle).text(R.string.text_password)
        viewAs<SeekBar>().progress = DEFAULT_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH
    }

    override fun showEditingPasswordMode(password: String) {
        textView(DialogPasswordTitle).text(R.string.text_edit_password)
        editText(DialogPasswordEditText).text(password)
        editText(DialogPasswordEditText).setSelection(password.length)
        viewAs<SeekBar>().progress = password.length - MIN_PASSWORD_LENGTH
    }

    override fun showChangePasswordLength(progress: Int) {
        val text = contextNonNull.getString(R.string.text_password_length, progress)
        textView(DialogPasswordTextLength).text(text)
    }

    override fun showPasswordStrength(strength: PasswordStrength?) {
        val textResId = when (strength) {
            null, PasswordStrength.WEAK -> R.string.text_weak
            PasswordStrength.MEDIUM -> R.string.text_medium
            PasswordStrength.STRONG -> R.string.text_strong
            PasswordStrength.VERY_STRONG -> R.string.text_secure
        }
        viewAs<PasswordStrengthMeterWithText>().setText(textResId)
        viewAs<PasswordStrengthMeterWithText>().setStrength(strength ?: PasswordStrength.WEAK)

    }

    override fun showPasswordCharacteristics(characteristics: Collection<PasswordCharacteristics>) {
        val checkmark: (Int) -> CheckmarkAndTextViewGroup = { textResId -> viewAs(textResId) }
        checkmark(R.string.text_uppercase_symbols).isChecked = characteristics.contains(
            UPPERCASE_SYMBOLS
        )
        checkmark(R.string.text_numbers).isChecked = characteristics.contains(NUMBERS)
        checkmark(R.string.text_special_symbols).isChecked =
            characteristics.contains(SPECIAL_SYMBOLS)
    }

    override fun showGeneratedPassword(password: String) {
        contextNonNull.hideKeyboard()
        editText(DialogPasswordEditText).clearFocus()
        editText(DialogPasswordEditText).text(password)
        editText(DialogPasswordEditText).setSelection(password.length)
    }

    override fun showPasswordIsEmpty() {
        textView(DialogPasswordTextError).text(R.string.text_password_cannot_be_empty)
    }

    override fun showPasswordAcceptingDialog() {
        contextNonNull.hideKeyboard()
        infoDialog.onHide = { presenter.onHideAcceptPasswordDialog() }
        infoDialog.showWithOkOption(
            R.string.text_saving_password,
            R.string.text_do_you_want_to_save_password,
            R.string.text_yes,
            onOkClicked = { presenter.acceptPassword() }
        )
    }

    override fun hidePasswordAcceptingDialog() {
        infoDialog.onHide = {}
        infoDialog.hide()
    }

    override fun showLoadingDialog() {
        loadingDialog.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog.hide()
    }

    override fun handleBackPress(): Boolean {
        return presenter.handleBackPress()
    }

    private companion object {

        const val DialogPassword = "DialogPassword"
        const val DialogPasswordTextLength = "DialogPasswordTextLength"
        const val DialogPasswordTitle = "DialogPasswordTitle"
        const val DialogPasswordTextError = "DialogPasswordTextError"
        const val DialogPasswordEditText = "DialogPasswordEditText"
    }
}