package com.caloshape.app.data.notifications.repo

import com.caloshape.app.data.notifications.api.NotificationInboxApi
import com.caloshape.app.data.notifications.api.NotificationItemDto
import com.caloshape.app.data.notifications.api.NotificationMarkReadResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationInboxRepository @Inject constructor(
    private val api: NotificationInboxApi
) {
    suspend fun list(): List<NotificationItemDto> = api.list()

    suspend fun markRead(id: Long): NotificationMarkReadResponseDto = api.markRead(id)
}
