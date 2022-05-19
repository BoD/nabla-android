package com.nabla.sdk.core.domain.boundary

import com.benasher44.uuid.Uuid
import com.nabla.sdk.core.domain.entity.MimeType
import com.nabla.sdk.core.domain.entity.Uri

internal interface FileUploadRepository {
    suspend fun uploadFile(localPath: Uri, fileName: String?, mimeType: MimeType): Uuid
}
