package com.example.sakura_flashcard.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant

class Converters {
    
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.epochSecond
    }
    
    @TypeConverter
    fun toInstant(epochSecond: Long?): Instant? {
        return epochSecond?.let { Instant.ofEpochSecond(it) }
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromStrokeList(value: List<Stroke>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStrokeList(value: String): List<Stroke> {
        val listType = object : TypeToken<List<Stroke>>() {}.type
        return Gson().fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromFlashcardSide(value: FlashcardSide): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toFlashcardSide(value: String): FlashcardSide {
        return Gson().fromJson(value, FlashcardSide::class.java)
    }
    
    @TypeConverter
    fun fromVocabularyTopic(value: VocabularyTopic): String {
        return value.name
    }
    
    @TypeConverter
    fun toVocabularyTopic(value: String): VocabularyTopic {
        return VocabularyTopic.valueOf(value)
    }
    
    @TypeConverter
    fun fromJLPTLevel(value: JLPTLevel): String {
        return value.name
    }
    
    @TypeConverter
    fun toJLPTLevel(value: String): JLPTLevel {
        return JLPTLevel.valueOf(value)
    }
    
    @TypeConverter
    fun fromCharacterScript(value: CharacterScript): String {
        return value.name
    }
    
    @TypeConverter
    fun toCharacterScript(value: String): CharacterScript {
        return CharacterScript.valueOf(value)
    }
    
    @TypeConverter
    fun fromStrokeDirection(value: StrokeDirection): String {
        return value.name
    }
    
    @TypeConverter
    fun toStrokeDirection(value: String): StrokeDirection {
        return StrokeDirection.valueOf(value)
    }
    
    @TypeConverter
    fun fromJLPTLevelProgressMap(value: Map<JLPTLevel, Float>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toJLPTLevelProgressMap(value: String): Map<JLPTLevel, Float> {
        val mapType = object : TypeToken<Map<JLPTLevel, Float>>() {}.type
        return Gson().fromJson(value, mapType) ?: emptyMap()
    }
    
    @TypeConverter
    fun fromReviewQuality(value: ReviewQuality): Int {
        return value.value
    }
    
    @TypeConverter
    fun toReviewQuality(value: Int): ReviewQuality {
        return ReviewQuality.fromValue(value) ?: ReviewQuality.CORRECT_HARD
    }
    
    @TypeConverter
    fun fromPointList(value: List<Point>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toPointList(value: String): List<Point> {
        val listType = object : TypeToken<List<Point>>() {}.type
        return Gson().fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromQuizAnswerList(value: List<QuizAnswer>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toQuizAnswerList(value: String): List<QuizAnswer> {
        val listType = object : TypeToken<List<QuizAnswer>>() {}.type
        return Gson().fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromGameType(value: GameType): String {
        return value.name
    }
    
    @TypeConverter
    fun toGameType(value: String): GameType {
        return GameType.valueOf(value)
    }
}