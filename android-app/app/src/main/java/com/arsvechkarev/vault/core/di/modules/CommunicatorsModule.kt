package com.arsvechkarev.vault.core.di.modules

import com.arsvechkarev.vault.core.communicators.FlowCommunicator
import com.arsvechkarev.vault.core.communicators.FlowCommunicatorImpl
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingCommunicator
import com.arsvechkarev.vault.features.creating_password.PasswordCreatingEvents
import com.arsvechkarev.vault.features.password_checking.PasswordCheckingCommunicator
import com.arsvechkarev.vault.features.password_checking.PasswordCheckingEvents
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Singleton

@Module
object CommunicatorsModule {

    @Provides
    @Singleton
    @JvmStatic
    @PasswordCreatingCommunicator
    fun providePasswordCreatingCommunicator(): FlowCommunicator<PasswordCreatingEvents> {
        return FlowCommunicatorImpl(MutableSharedFlow(replay = 1))
    }

    @Provides
    @Singleton
    @JvmStatic
    @PasswordCheckingCommunicator
    fun providePasswordCheckingCommunicator(): FlowCommunicator<PasswordCheckingEvents> {
        return FlowCommunicatorImpl()
    }
}