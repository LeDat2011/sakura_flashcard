package com.example.sakura_flashcard.data.sync

sealed class SyncResult {
    object Success : SyncResult()
    data class Failure(val error: String) : SyncResult()
}
