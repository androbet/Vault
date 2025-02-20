package com.arsvechkarev.vault.features.creating_password

import buisnesslogic.*
import buisnesslogic.PasswordStatus.EMPTY
import buisnesslogic.generator.PasswordGenerator
import buisnesslogic.model.PasswordCharacteristics
import buisnesslogic.model.PasswordCharacteristics.*
import com.arsvechkarev.vault.core.BasePresenter
import com.arsvechkarev.vault.core.Dispatchers
import com.arsvechkarev.vault.core.communicators.FlowCommunicator
import com.arsvechkarev.vault.core.di.FeatureScope
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingActions.*
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingActions.ConfigureMode.EditPassword
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingActions.ConfigureMode.NewPassword
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingReactions.OnNewPasswordAccepted
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingReactions.OnSavePasswordButtonClicked
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingState.*
import kotlinx.coroutines.launch
import navigation.Router
import java.util.*
import javax.inject.Inject

@FeatureScope
class PasswordCreatingPresenter @Inject constructor(
    @PasswordCreatingCommunicator
    private val passwordCreatingCommunicator: FlowCommunicator<PasswordCreatingEvents>,
    private val passwordChecker: PasswordChecker,
    private val passwordGenerator: PasswordGenerator,
    private val router: Router,
    dispatchers: Dispatchers
) : BasePresenter<PasswordCreatingView>(dispatchers) {

    private var passwordCharacteristics = EnumSet.of(UPPERCASE_SYMBOLS, NUMBERS, SPECIAL_SYMBOLS)
    private var passwordLength = DEFAULT_PASSWORD_LENGTH
    private var password = ""
    private var state = INITIAL

    init {
        subscribeToPasswordCreatingEvents()
    }

    fun showInitialGeneratedPassword() {
        passwordLength = DEFAULT_PASSWORD_LENGTH
        onGeneratePasswordClicked()
    }

    fun onGeneratePasswordClicked() {
        password = passwordGenerator.generatePassword(passwordLength, passwordCharacteristics)
        showPasswordInfo()
        viewState.showGeneratedPassword(password)
    }

    fun onPasswordChanged(password: String) {
        this.password = password.trim()
        showPasswordInfo()
    }

    fun onPasswordLengthChanged(seekBarProgress: Int) {
        val length = seekBarProgress + MIN_PASSWORD_LENGTH
        passwordLength = length
        viewState.showChangePasswordLength(length)
    }

    fun onCheckmarkClicked(characteristics: PasswordCharacteristics) {
        if (passwordCharacteristics.contains(characteristics)) {
            passwordCharacteristics.remove(characteristics)
        } else {
            passwordCharacteristics.add(characteristics)
        }
        viewState.showPasswordCharacteristics(passwordCharacteristics)
    }

    fun onSavePasswordClicked() {
        when (passwordChecker.validate(password)) {
            EMPTY -> viewState.showPasswordIsEmpty()
            else -> launch { passwordCreatingCommunicator.send(OnSavePasswordButtonClicked(password)) }
        }
    }

    private fun showPasswordInfo() {
        fillPasswordCharacteristics()
        viewState.showPasswordCharacteristics(passwordCharacteristics)
        viewState.showPasswordStrength(passwordChecker.checkStrength(password))
    }

    private fun fillPasswordCharacteristics() {
        passwordCharacteristics.clear()
        if (password.hasUppercaseLetters) passwordCharacteristics.add(UPPERCASE_SYMBOLS)
        if (password.hasNumbers) passwordCharacteristics.add(NUMBERS)
        if (password.hasSpecialSymbols) passwordCharacteristics.add(SPECIAL_SYMBOLS)
    }

    fun onCloseClicked() {
        if (!handleBackPress()) router.goBack(releaseCurrentScreen = false)
    }

    fun handleBackPress(): Boolean {
        return when (state) {
            INITIAL -> false
            SHOWING_ACCEPT_DIALOG -> {
                viewState.hidePasswordAcceptingDialog()
                true
            }
            LOADING -> true
        }
    }

    fun onHideAcceptPasswordDialog() {
        state = INITIAL
        viewState.hidePasswordAcceptingDialog()
    }

    fun acceptPassword() {
        launch { passwordCreatingCommunicator.send(OnNewPasswordAccepted(password)) }
    }

    private fun subscribeToPasswordCreatingEvents() {
        passwordCreatingCommunicator.events.collectInPresenterScope { event ->
            when (event) {
                NewPassword -> {
                    viewState.showCreatingPasswordMode()
                    showInitialGeneratedPassword()
                }
                is EditPassword -> {
                    this.password = event.password
                    viewState.showEditingPasswordMode(password)
                    onPasswordChanged(password)
                }
                ShowAcceptPasswordDialog -> {
                    state = SHOWING_ACCEPT_DIALOG
                    viewState.showPasswordAcceptingDialog()
                }
                ShowLoading -> {
                    state = LOADING
                    viewState.hidePasswordAcceptingDialog()
                    viewState.showLoadingDialog()
                }
                ExitScreen -> {
                    state = INITIAL
                    viewState.hidePasswordAcceptingDialog()
                    viewState.hideLoadingDialog()
                }
            }
        }
    }
}