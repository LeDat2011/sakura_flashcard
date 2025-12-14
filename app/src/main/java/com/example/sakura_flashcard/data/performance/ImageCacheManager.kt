package com.example.sakura_flashcard.data.performance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages image caching and optimization for better performance
 */
@Singleton
class ImageCacheManager @Inject constructor(
    private val context: Context
) {
    
    // Memory cache for frequently accessed images
    private val memoryCache: LruCache<String, Bitmap>
    
    // Disk cache directory
    private val diskCacheDir: File
    
    init {
        // Calculate memory cache size (1/8 of available memory)
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
        
        // Set up disk cache directory
        diskCacheDir = File(context.cacheDir, "image_cache")
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs()
        }
    }
    
    /**
     * Loads an image with caching and optimization
     */
    suspend fun loadImage(
        url: String,
        maxWidth: Int = 800,
        maxHeight: Int = 600,
        quality: Int = 85
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val cacheKey = generateCacheKey(url, maxWidth, maxHeight)
            
            // Check memory cache first
            memoryCache.get(cacheKey)?.let { cachedBitmap ->
                return@withContext Result.success(cachedBitmap)
            }
            
            // Check disk cache
            val diskCacheFile = File(diskCacheDir, cacheKey)
            if (diskCacheFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(diskCacheFile.absolutePath)
                if (bitmap != null) {
                    // Add to memory cache
                    memoryCache.put(cacheKey, bitmap)
                    return@withContext Result.success(bitmap)
                }
            }
            
            // Download and process image
            val originalBitmap = downloadImage(url)
            val optimizedBitmap = optimizeBitmap(originalBitmap, maxWidth, maxHeight)
            
            // Save to disk cache
            saveToDiskCache(optimizedBitmap, diskCacheFile, quality)
            
            // Add to memory cache
            memoryCache.put(cacheKey, optimizedBitmap)
            
            Result.success(optimizedBitmap)
        } catch (e: Exception) {
            Result.failure(ImageCacheException("Failed to load image: ${e.message}", e))
        }
    }
    
    /**
     * Preloads images for better performance
     */
    suspend fun preloadImages(
        urls: List<String>,
        maxWidth: Int = 400,
        maxHeight: Int = 300
    ) = withContext(Dispatchers.IO) {
        urls.forEach { url ->
            try {
                loadImage(url, maxWidth, maxHeight)
            } catch (e: Exception) {
                // Log error but continue with other images
                println("Failed to preload image: $url - ${e.message}")
            }
        }
    }
    
    /**
     * Downloads image from URL
     */
    private suspend fun downloadImage(url: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection()
        connection.connectTimeout = 10000
        connection.readTimeout = 15000
        connection.connect()
        
        connection.getInputStream().use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
                ?: throw ImageCacheException("Failed to decode image from URL: $url")
        }
    }
    
    /**
     * Optimizes bitmap size and quality
     */
    private fun optimizeBitmap(
        original: Bitmap,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap {
        val originalWidth = original.width
        val originalHeight = original.height
        
        // Calculate scaling factor
        val scaleFactor = minOf(
            maxWidth.toFloat() / originalWidth,
            maxHeight.toFloat() / originalHeight,
            1.0f // Don't upscale
        )
        
        if (scaleFactor >= 1.0f) {
            return original
        }
        
        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()
        
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true).also {
            // Recycle original if it's different from the new one
            if (it != original) {
                original.recycle()
            }
        }
    }
    
    /**
     * Saves bitmap to disk cache
     */
    private suspend fun saveToDiskCache(
        bitmap: Bitmap,
        file: File,
        quality: Int
    ) = withContext(Dispatchers.IO) {
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
        } catch (e: Exception) {
            // If saving fails, delete the partial file
            if (file.exists()) {
                file.delete()
            }
            throw e
        }
    }
    
    /**
     * Generates cache key for image
     */
    private fun generateCacheKey(url: String, maxWidth: Int, maxHeight: Int): String {
        val input = "$url-$maxWidth-$maxHeight"
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Clears memory cache
     */
    fun clearMemoryCache() {
        memoryCache.evictAll()
    }
    
    /**
     * Clears disk cache
     */
    suspend fun clearDiskCache() = withContext(Dispatchers.IO) {
        try {
            diskCacheDir.listFiles()?.forEach { file ->
                file.delete()
            }
        } catch (e: Exception) {
            throw ImageCacheException("Failed to clear disk cache", e)
        }
    }
    
    /**
     * Gets cache statistics
     */
    fun getCacheStats(): ImageCacheStats {
        val diskCacheSize = diskCacheDir.listFiles()?.sumOf { it.length() } ?: 0L
        val diskCacheCount = diskCacheDir.listFiles()?.size ?: 0
        
        return ImageCacheStats(
            memoryCacheSize = memoryCache.size(),
            memoryCacheMaxSize = memoryCache.maxSize(),
            memoryCacheHitCount = memoryCache.hitCount().toInt(),
            memoryCacheMissCount = memoryCache.missCount().toInt(),
            diskCacheSize = diskCacheSize,
            diskCacheCount = diskCacheCount.toLong()
        )
    }
    
    /**
     * Trims memory cache to specified size
     */
    fun trimMemoryCache(level: Int) {
        when (level) {
            android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN,
            android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> {
                // Clear half of memory cache
                memoryCache.trimToSize(memoryCache.maxSize() / 2)
            }
            android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            android.content.ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                // Clear all memory cache
                memoryCache.evictAll()
            }
        }
    }
}

/**
 * Image cache statistics
 */
data class ImageCacheStats(
    val memoryCacheSize: Int,
    val memoryCacheMaxSize: Int,
    val memoryCacheHitCount: Int,
    val memoryCacheMissCount: Int,
    val diskCacheSize: Long,
    val diskCacheCount: Long
) {
    val memoryCacheHitRate: Float
        get() = if (memoryCacheHitCount + memoryCacheMissCount > 0) {
            memoryCacheHitCount.toFloat() / (memoryCacheHitCount + memoryCacheMissCount)
        } else 0f
    
    val diskCacheSizeMB: Float
        get() = diskCacheSize / (1024f * 1024f)
}

/**
 * Exception for image cache operations
 */
class ImageCacheException(message: String, cause: Throwable? = null) : Exception(message, cause)