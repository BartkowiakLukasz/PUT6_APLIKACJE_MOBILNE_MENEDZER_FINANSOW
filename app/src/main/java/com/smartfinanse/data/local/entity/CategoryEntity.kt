package com.smartfinanse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String, // np. "ic_food", "ic_car" do siatki ikon
    val isExpense: Boolean // true dla wydatków, false dla przychodów
)
