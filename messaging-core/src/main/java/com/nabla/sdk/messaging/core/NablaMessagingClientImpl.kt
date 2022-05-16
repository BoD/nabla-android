package com.nabla.sdk.messaging.core

import androidx.annotation.CheckResult
import com.nabla.sdk.core.data.exception.catchAndRethrowAsNablaException
import com.nabla.sdk.core.data.exception.mapFailureAsNablaException
import com.nabla.sdk.core.domain.boundary.Logger
import com.nabla.sdk.core.domain.entity.NablaException
import com.nabla.sdk.core.injection.CoreContainer
import com.nabla.sdk.core.kotlin.runCatchingCancellable
import com.nabla.sdk.messaging.core.domain.boundary.ConversationRepository
import com.nabla.sdk.messaging.core.domain.boundary.MessageRepository
import com.nabla.sdk.messaging.core.domain.entity.Conversation
import com.nabla.sdk.messaging.core.domain.entity.ConversationId
import com.nabla.sdk.messaging.core.domain.entity.ConversationMessages
import com.nabla.sdk.messaging.core.domain.entity.Message
import com.nabla.sdk.messaging.core.domain.entity.MessageId
import com.nabla.sdk.messaging.core.domain.entity.WatchPaginatedResponse
import com.nabla.sdk.messaging.core.injection.MessagingContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NablaMessagingClientImpl internal constructor(
    coreContainer: CoreContainer,
) : NablaMessagingClient {

    private val messagingContainer = MessagingContainer(
        coreContainer.logger,
        coreContainer.apolloClient,
        coreContainer.fileUploadRepository,
        coreContainer.exceptionMapper,
        coreContainer.sessionClient,
    )

    private val conversationRepository: ConversationRepository by lazy {
        messagingContainer.conversationRepository
    }
    private val messageRepository: MessageRepository by lazy {
        messagingContainer.messageRepository
    }

    override val logger: Logger = coreContainer.logger

    override fun watchConversations(): Flow<WatchPaginatedResponse<List<Conversation>>> {
        ensureAuthenticatedOrThrow()

        val loadMoreCallback = suspend {
            runCatchingCancellable {
                conversationRepository.loadMoreConversations()
            }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
        }

        return conversationRepository.watchConversations()
            .map { paginatedConversations ->
                WatchPaginatedResponse(
                    content = paginatedConversations.items,
                    loadMore = if (paginatedConversations.hasMore) {
                        loadMoreCallback
                    } else {
                        null
                    },
                )
            }
            .catchAndRethrowAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    @CheckResult
    override suspend fun createConversation(): Result<Conversation> {
        ensureAuthenticatedOrThrow()

        return runCatchingCancellable {
            conversationRepository.createConversation()
        }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    override fun watchConversation(conversationId: ConversationId): Flow<Conversation> {
        ensureAuthenticatedOrThrow()

        return conversationRepository.watchConversation(conversationId)
            .catchAndRethrowAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    override fun watchConversationMessages(conversationId: ConversationId): Flow<WatchPaginatedResponse<ConversationMessages>> {
        ensureAuthenticatedOrThrow()

        val loadMoreCallback = suspend {
            runCatchingCancellable {
                messageRepository.loadMoreMessages(conversationId)
            }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
        }

        return messageRepository.watchConversationMessages(conversationId)
            .map { paginatedConversationMessages ->
                WatchPaginatedResponse(
                    content = paginatedConversationMessages.conversationMessages,
                    loadMore = if (paginatedConversationMessages.hasMore) {
                        loadMoreCallback
                    } else null
                )
            }
            .catchAndRethrowAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    @CheckResult
    override suspend fun sendMessage(message: Message): Result<Unit> {
        ensureAuthenticatedOrThrow()

        return runCatchingCancellable {
            messageRepository.sendMessage(message)
        }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    @CheckResult
    override suspend fun retrySendingMessage(localMessageId: MessageId.Local, conversationId: ConversationId): Result<Unit> {
        ensureAuthenticatedOrThrow()

        return runCatchingCancellable {
            messageRepository.retrySendingMessage(conversationId, localMessageId)
        }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    @CheckResult
    override suspend fun setTyping(conversationId: ConversationId, isTyping: Boolean): Result<Unit> {
        ensureAuthenticatedOrThrow()

        return runCatchingCancellable {
            messageRepository.setTyping(conversationId, isTyping)
        }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    @CheckResult
    override suspend fun markConversationAsRead(conversationId: ConversationId): Result<Unit> {
        ensureAuthenticatedOrThrow()

        return runCatchingCancellable {
            conversationRepository.markConversationAsRead(conversationId)
        }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    @CheckResult
    override suspend fun deleteMessage(conversationId: ConversationId, id: MessageId): Result<Unit> {
        ensureAuthenticatedOrThrow()

        return runCatchingCancellable {
            messageRepository.deleteMessage(conversationId, id)
        }.mapFailureAsNablaException(messagingContainer.nablaExceptionMapper)
    }

    private fun ensureAuthenticatedOrThrow() {
        if (!messagingContainer.sessionClient.isSessionInitialized()) {
            throw NablaException.Authentication.NotAuthenticated
        }
    }
}
