package com.example.data.local

import com.example.data.model.CreationEntity
import com.example.data.model.MediaType
import kotlinx.coroutines.flow.Flow

class CreationRepository(private val dao: CreationDao) {
    val allCreations: Flow<List<CreationEntity>> = dao.getAllCreations()
    val imageCreations: Flow<List<CreationEntity>> = dao.getCreationsByType(MediaType.IMAGE)
    val videoCreations: Flow<List<CreationEntity>> = dao.getCreationsByType(MediaType.VIDEO)
    val favoriteCreations: Flow<List<CreationEntity>> = dao.getFavorites()
    val totalCount: Flow<Int> = dao.getCreationsCount()

    suspend fun insert(creation: CreationEntity): Long = dao.insertCreation(creation)

    suspend fun update(creation: CreationEntity) = dao.updateCreation(creation)

    suspend fun delete(creation: CreationEntity) = dao.deleteCreation(creation)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun toggleFavorite(id: Long, isFav: Boolean) = dao.updateFavorite(id, isFav)

    suspend fun getById(id: Long): CreationEntity? = dao.getById(id)
}
