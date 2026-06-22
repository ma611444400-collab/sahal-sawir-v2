package com.sahalsawir.app.model

import android.content.Context
import kotlinx.coroutines.flow.Flow

class EnhancementRepository(private val dao: EnhancementDao) {

    val allRecords: Flow<List<EnhancementRecord>> = dao.getAllRecords()

    suspend fun saveRecord(record: EnhancementRecord) {
        dao.insertRecord(record)
    }

    suspend fun deleteRecord(id: Int) {
        dao.deleteById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }

    companion object {
        private var INSTANCE: EnhancementRepository? = null

        fun getInstance(context: Context): EnhancementRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val repo = EnhancementRepository(db.enhancementDao())
                INSTANCE = repo
                repo
            }
        }
    }
}
