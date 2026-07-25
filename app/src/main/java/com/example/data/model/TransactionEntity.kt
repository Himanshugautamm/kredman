package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val paymentMethod: PaymentMethod,
    val dateEpochMs: Long = System.currentTimeMillis(),
    val notes: String = "",
    val tags: String = "",
    val receiptUri: String? = null,
    val isRecurring: Boolean = false,
    val location: String = ""
)
