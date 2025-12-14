package com.example.sakura_flashcard.data.sync

data class SyncState(
    val pendingOperations: Int = 0,
    val isSyncing: Boolean = false,
    val lastError: String? = null,
    val lastSyncTime: java.time.Instant? = null
) {
    companion object {
        val Idle = SyncState()
    }
}
