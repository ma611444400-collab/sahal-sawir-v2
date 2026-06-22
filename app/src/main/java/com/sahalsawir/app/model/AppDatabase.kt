package com.sahalsawir.app.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream

@Entity(tableName = "enhancement_history")
data class EnhancementRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceName: String,
    val upscaleFactor: String,
    val denoisePercent: Int,
    val colorPercent: Int,
    val appliedFilter: String,
    val thumbnailBase64: String? = null
) {
    fun getThumbnailBitmap(): Bitmap? {
        if (thumbnailBase64.isNullOrEmpty()) return null
        return try {
            val bytes = Base64.decode(thumbnailBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) { null }
    }
}

@Dao
interface EnhancementDao {
    @Query("SELECT * FROM enhancement_history ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<EnhancementRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: EnhancementRecord)

    @Query("DELETE FROM enhancement_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM enhancement_history")
    suspend fun clearAll()
}

@Database(entities = [EnhancementRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun enhancementDao(): EnhancementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sahal_sawir_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object ThumbnailHelper {
    fun createThumbnailBase64(bmp: Bitmap): String {
        return try {
            val scale = 120f / Math.max(bmp.width, bmp.height).toFloat()
            val w = (bmp.width * scale).toInt().coerceAtLeast(10)
            val h = (bmp.height * scale).toInt().coerceAtLeast(10)
            val thumb = Bitmap.createScaledBitmap(bmp, w, h, true)
            val out = ByteArrayOutputStream()
            thumb.compress(Bitmap.CompressFormat.JPEG, 75, out)
            Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) { "" }
    }
}
