package com.nabla.sdk.playground.injection

import com.nabla.sdk.messaging.core.injection.MessagingContainer
import com.nabla.sdk.messaging.ui.injection.MessagingUiContainer

class AppContainer {
    val messagingContainer = MessagingContainer()
    val messagingUiContainer = MessagingUiContainer(messagingContainer)
}

val appContainer by lazy { AppContainer() }
