package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType {
    IMAGE,
    VIDEO
}

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val urduPrompt: String = "",
    val mediaType: MediaType = MediaType.IMAGE,
    val mediaUrl: String, // file path, content uri, or network URL
    val thumbnailUri: String = "",
    val aspectRatio: String = "1:1",
    val style: String = "Photorealistic",
    val motionType: String = "Cinematic Drone",
    val durationSeconds: Int = 5,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
