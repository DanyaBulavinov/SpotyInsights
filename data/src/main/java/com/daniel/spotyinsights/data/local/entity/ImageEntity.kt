package com.daniel.spotyinsights.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val height: Int?,
    val width: Int?
) 