package com.example.abhay.address

/**
 * The members of ths class will represent the possible replies from the server if the requested operation is a DELETE operation.
 */
data class DeleteActionReply(
        var errors: String? = null,
        var message: String? = null
)