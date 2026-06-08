package com.smartfinanse.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Ignore
import java.time.Period

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("date")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long?,
    val amount: Long,
    val description: String,
    val date: Long,
    val isCash: Boolean,
    val isRecurring: Boolean = false,
    val location: String?,
    val receiptImageUri: String?
) {
    @Ignore
    var recurringInterval: Period = Period.ofMonths(1)
}
