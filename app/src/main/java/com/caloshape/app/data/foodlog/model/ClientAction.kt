package com.caloshape.app.data.foodlog.model

import kotlinx.serialization.Serializable

@Serializable
enum class ClientAction {
    RETRY_LATER,
    RETAKE_PHOTO,
    SCAN_AGAIN,
    TRY_PHOTO,
    TRY_BARCODE,
    TRY_LABEL,
    ENTER_MANUALLY,
    CHECK_NETWORK,
    CONTACT_SUPPORT
}
