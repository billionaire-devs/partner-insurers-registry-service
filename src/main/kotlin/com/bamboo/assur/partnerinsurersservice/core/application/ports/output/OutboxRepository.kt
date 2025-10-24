package com.bamboo.assur.partnerinsurersservice.core.application.ports.output

import com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.OutboxMessagesTable
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import java.util.UUID
import kotlin.time.ExperimentalTime
import java.time.Instant
import kotlin.uuid.ExperimentalUuidApi

/**
 * Repository for accessing and updating outbox messages using Kotlin coroutines.
 */
@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
interface OutboxRepository : CoroutineCrudRepository<OutboxMessagesTable, UUID> {

    /**
     * Returns a flow of unprocessed messages in creation order, limited by [batchSize].
     * Uses "FOR UPDATE SKIP LOCKED" to avoid concurrent processors working on the same rows.
     */
    @Query(
        """
        SELECT * FROM outbox
        WHERE processed = false 
        ORDER BY created_at
        LIMIT :batchSize
        """
    )
    suspend fun findUnprocessedMessages(@Param("batchSize") batchSize: Int): Flow<OutboxMessagesTable>

    /**
     * Marks a message as processed, optionally storing an error message.
     * Must be called within a transaction context.
     *
     * @return number of rows updated (0 or 1)
     */
    @Modifying
    @Query(
        """
        UPDATE outbox 
        SET processed = true, 
            processed_at = :processedAt, 
            error = :error
        WHERE id = :id
        """
    )
    suspend fun markAsProcessed(
        @Param("id") id: UUID,
        @Param("processedAt") processedAt: Instant,
        @Param("error") error: String? = null
    ): Int
}
