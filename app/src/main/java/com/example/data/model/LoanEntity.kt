package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: LoanType,
    val totalAmount: Double,
    val remainingAmount: Double,
    val interestRate: Double,
    val monthlyEmi: Double,
    val dueDateEpochMs: Long,
    val lenderOrBorrower: String = ""
)
