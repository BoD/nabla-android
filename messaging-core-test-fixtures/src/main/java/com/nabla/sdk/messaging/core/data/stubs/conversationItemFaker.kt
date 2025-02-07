package com.nabla.sdk.messaging.core.data.stubs

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.nabla.sdk.core.data.stubs.StringFaker.randomText
import com.nabla.sdk.core.data.stubs.UriFaker
import com.nabla.sdk.core.domain.entity.BaseFileUpload
import com.nabla.sdk.core.domain.entity.EphemeralUrl
import com.nabla.sdk.core.domain.entity.FileUpload
import com.nabla.sdk.core.domain.entity.MimeType
import com.nabla.sdk.core.domain.entity.Provider
import com.nabla.sdk.core.domain.entity.SystemUser
import com.nabla.sdk.core.domain.entity.Uri
import com.nabla.sdk.messaging.core.domain.entity.BaseMessage
import com.nabla.sdk.messaging.core.domain.entity.Conversation
import com.nabla.sdk.messaging.core.domain.entity.ConversationActivity
import com.nabla.sdk.messaging.core.domain.entity.ConversationActivityContent
import com.nabla.sdk.messaging.core.domain.entity.ConversationId
import com.nabla.sdk.messaging.core.domain.entity.ConversationItems
import com.nabla.sdk.messaging.core.domain.entity.FileLocal
import com.nabla.sdk.messaging.core.domain.entity.FileSource
import com.nabla.sdk.messaging.core.domain.entity.Message
import com.nabla.sdk.messaging.core.domain.entity.MessageAuthor
import com.nabla.sdk.messaging.core.domain.entity.MessageId
import com.nabla.sdk.messaging.core.domain.entity.ProviderInConversation
import com.nabla.sdk.messaging.core.domain.entity.SendStatus
import com.nabla.sdk.messaging.core.domain.entity.toConversationActivityId
import com.nabla.sdk.messaging.core.domain.entity.toConversationId
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun ConversationItems.Companion.fake(
    conversation: Conversation = Conversation.fake(),
    messages: List<Message> = ((5 downTo 1).map { Message.Text.fake(sentAt = nowMinus(it.minutes)) }),
) = ConversationItems(
    conversationId = conversation.id,
    items = messages,
)

fun Message.Text.Companion.fake(
    id: MessageId = MessageId.Remote(uuid4(), uuid4()),
    sentAt: Instant = Clock.System.now().minus(20.minutes),
    author: MessageAuthor = MessageAuthor.Patient,
    status: SendStatus = SendStatus.Sent,
    text: String = randomText(),
    replyTo: Message? = null,
) = Message.Text(
    BaseMessage(
        id = id,
        createdAt = sentAt,
        author = author,
        sendStatus = status,
        conversationId = uuid4().toConversationId(),
        replyTo = replyTo,
    ),
    text = text,
)

fun Message.Media.Image.Companion.fake(
    id: MessageId = MessageId.Remote(uuid4(), uuid4()),
    sentAt: Instant = Clock.System.now().minus(20.minutes),
    author: MessageAuthor = MessageAuthor.Patient,
    status: SendStatus = SendStatus.Sent,
    conversationId: ConversationId = ConversationId(uuid4()),
    mediaSource: FileSource<FileLocal.Image, FileUpload.Image> = FileSource.Uploaded.fakeImage(),
) = Message.Media.Image(
    baseMessage = BaseMessage(
        id = id,
        createdAt = sentAt,
        author = author,
        sendStatus = status,
        conversationId = conversationId,
        replyTo = null,
    ),
    mediaSource = mediaSource,
)

fun Message.Media.Audio.Companion.fake(
    id: MessageId = MessageId.Remote(uuid4(), uuid4()),
    sentAt: Instant = Clock.System.now().minus(20.minutes),
    author: MessageAuthor = MessageAuthor.Patient,
    status: SendStatus = SendStatus.Sent,
    conversationId: ConversationId = ConversationId(uuid4()),
    mediaSource: FileSource<FileLocal.Audio, FileUpload.Audio> = FileSource.Uploaded.fakeAudio(),
) = Message.Media.Audio(
    baseMessage = BaseMessage(
        id = id,
        createdAt = sentAt,
        author = author,
        sendStatus = status,
        conversationId = conversationId,
        replyTo = null,
    ),
    mediaSource = mediaSource,
)

fun FileSource.Local.Companion.fakeImage(
    fileLocal: FileLocal.Image = FileLocal.Image.fake(),
) = FileSource.Local<FileLocal.Image, FileUpload.Image>(
    fileLocal = fileLocal,
)

fun FileSource.Uploaded.Companion.fakeImage(
    fileLocal: FileLocal.Image? = FileLocal.Image.fake(),
    fileUpload: FileUpload.Image = FileUpload.Image.fake(),
) = FileSource.Uploaded(
    fileLocal = fileLocal,
    fileUpload = fileUpload,
)

fun FileSource.Uploaded.Companion.fakeAudio(
    fileLocal: FileLocal.Audio? = null,
    fileUpload: FileUpload.Audio = FileUpload.Audio.fake(),
) = FileSource.Uploaded(
    fileLocal = fileLocal,
    fileUpload = fileUpload,
)

fun FileLocal.Image.Companion.fake(
    uri: Uri = Uri("contentprovider:image.png"),
) = FileLocal.Image(
    uri = uri,
    fileName = "image.png",
    mimeType = MimeType.Image.Jpeg
)

fun FileUpload.Image.Companion.fake(
    ephemeralUrl: EphemeralUrl = EphemeralUrl.fake(url = Uri("https://i.pravatar.cc/900")),
    mimeType: MimeType = MimeType.Image.Jpeg,
    fileName: String = "filename.jpg",
) = FileUpload.Image(
    size = null,
    fileUpload = BaseFileUpload(
        id = uuid4(),
        url = ephemeralUrl,
        mimeType = mimeType,
        fileName = fileName,
    )
)

fun FileUpload.Audio.Companion.fake(
    ephemeralUrl: EphemeralUrl =
        EphemeralUrl.fake(url = Uri("https://commondatastorage.googleapis.com/codeskulptor-assets/sounddogs/thrust.mp3?uniqueness=${uuid4()}")),
    mimeType: MimeType = MimeType.Audio.Mp3,
    fileName: String = "audio.mp3",
    durationMs: Long = 27_000L,
) = FileUpload.Audio(
    durationMs = durationMs,
    fileUpload = BaseFileUpload(
        id = uuid4(),
        url = ephemeralUrl,
        mimeType = mimeType,
        fileName = fileName,
    )
)

fun Message.Media.Document.Companion.fake(
    id: MessageId = MessageId.Remote(uuid4(), uuid4()),
    sentAt: Instant = Clock.System.now().minus(20.minutes),
    author: MessageAuthor = MessageAuthor.Patient,
    status: SendStatus = SendStatus.Sent,
    conversationId: ConversationId = ConversationId(uuid4()),
    ephemeralUrl: EphemeralUrl = EphemeralUrl.fake(url = Uri("https://www.orimi.com/pdf-test.pdf")),
    mimeType: MimeType = MimeType.Application.Pdf,
    fileName: String = "filename.pdf",
    thumbnail: FileUpload.Image? = null,
) = Message.Media.Document(
    baseMessage = BaseMessage(
        id = id,
        createdAt = sentAt,
        author = author,
        sendStatus = status,
        conversationId = conversationId,
        replyTo = null,
    ),
    mediaSource = FileSource.Uploaded(
        fileLocal = null,
        fileUpload = FileUpload.Document(
            fileUpload = BaseFileUpload(
                id = uuid4(),
                url = ephemeralUrl,
                mimeType = mimeType,
                fileName = fileName,
            ),
            thumbnail = thumbnail
        ),
    ),
)

fun Provider.Companion.fake(
    id: Uuid = uuid4(),
    firstName: String = "Véronique",
    lastName: String = "Cayol",
    prefix: String? = "Dr",
    avatar: EphemeralUrl? = EphemeralUrl(
        expiresAt = Instant.DISTANT_FUTURE,
        url = Uri("https://i.pravatar.cc/300"),
    ),
) = Provider(
    id = id,
    avatar = avatar,
    firstName = firstName,
    lastName = lastName,
    prefix = prefix,
)

fun SystemUser.Companion.fake() = SystemUser(
    name = randomText(),
    avatar = EphemeralUrl.fake()
)

fun ProviderInConversation.Companion.fake(
    provider: Provider = Provider.fake(),
    typingAt: Instant? = null,
    seenUntil: Instant = Clock.System.now(),
) = ProviderInConversation(
    provider = provider,
    typingAt = typingAt,
    seenUntil = seenUntil,
)

fun Conversation.Companion.fake(
    id: Uuid = uuid4(),
    title: String = randomText(maxWords = 10),
    inboxPreviewTitle: String = randomText(maxWords = 10),
    subtitle: String = listOf("", "You: oh great!", "You: image", "Doctor is typing...").random(),
    lastModified: Instant = Clock.System.now().minus(2.minutes),
    lastMessagePreview: String = randomText(maxWords = 10),
    patientUnreadMessageCount: Int = 0,
    providersInConversation: List<ProviderInConversation> = listOf(ProviderInConversation.fake()),
) = Conversation(
    id = id.toConversationId(),
    lastModified = lastModified,
    title = title,
    inboxPreviewTitle = inboxPreviewTitle,
    subtitle = subtitle,
    lastMessagePreview = lastMessagePreview,
    patientUnreadMessageCount = patientUnreadMessageCount,
    providersInConversation = providersInConversation,
)

fun EphemeralUrl.Companion.fake(
    expiresAt: Instant = Instant.DISTANT_FUTURE,
    url: Uri = UriFaker.remote(),
) = EphemeralUrl(
    expiresAt = expiresAt,
    url = url,
)

fun ConversationActivity.Companion.fakeProviderJoined() = ConversationActivity(
    id = uuid4().toConversationActivityId(),
    conversationId = uuid4().toConversationId(),
    createdAt = Clock.System.now(),
    activityTime = Clock.System.now(),
    content = ConversationActivityContent.ProviderJoinedConversation(
        maybeProvider = Provider.fake()
    )
)

private fun nowMinus(duration: Duration): Instant = Clock.System.now().minus(duration)
