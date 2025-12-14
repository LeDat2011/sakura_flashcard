package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.VocabularyTopic
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 3: Complete Vocabulary Topic Display
 * Validates: Requirements 1.3
 */
class VocabularyTopicCompletenessTest : StringSpec({
    
    "vocabulary topics should contain all required categories" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val allTopics = VocabularyTopic.values().toList()
            val displayNames = VocabularyTopic.getAllDisplayNames()
            
            // All required topics should be present
            val requiredTopics = listOf(
                VocabularyTopic.ANIME,
                VocabularyTopic.BODY_PARTS,
                VocabularyTopic.FOOD,
                VocabularyTopic.DAILY_LIFE,
                VocabularyTopic.ANIMALS,
                VocabularyTopic.SCHOOL,
                VocabularyTopic.TRAVEL,
                VocabularyTopic.WEATHER,
                VocabularyTopic.FAMILY,
                VocabularyTopic.TECHNOLOGY,
                VocabularyTopic.CLOTHES,
                VocabularyTopic.COLORS,
                VocabularyTopic.NUMBERS,
                VocabularyTopic.COMMON_EXPRESSIONS
            )
            
            allTopics shouldContainAll requiredTopics
            
            // Each topic should have a display name
            requiredTopics.forEach { topic ->
                topic.displayName.isNotBlank() shouldBe true
                displayNames shouldContain topic.displayName
            }
        }
    }
    
    "vocabulary topic display names should be user-friendly" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val displayNames = VocabularyTopic.getAllDisplayNames()
            
            displayNames.forEach { displayName ->
                // Display names should be readable (not all caps enum names)
                displayName.isNotBlank() shouldBe true
                displayName.contains("_") shouldBe false // Should not contain underscores
                
                // Should be able to find topic by display name
                val topic = VocabularyTopic.fromDisplayName(displayName)
                topic shouldBe VocabularyTopic.values().find { it.displayName == displayName }
            }
        }
    }
    
    "vocabulary topics should be consistently accessible" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            VocabularyTopic.values().forEach { topic ->
                // Each topic should have consistent display name mapping
                val displayName = topic.displayName
                val foundTopic = VocabularyTopic.fromDisplayName(displayName)
                
                foundTopic shouldBe topic
                
                // Display name should be in the list of all display names
                val allDisplayNames = VocabularyTopic.getAllDisplayNames()
                allDisplayNames shouldContain displayName
            }
        }
    }
    
    "vocabulary topics should cover all specified learning categories" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val topicNames = VocabularyTopic.values().map { it.name }
            
            // Verify all required categories are present
            val requiredCategories = listOf(
                "ANIME", "BODY_PARTS", "FOOD", "DAILY_LIFE", "ANIMALS",
                "SCHOOL", "TRAVEL", "WEATHER", "FAMILY", "TECHNOLOGY",
                "CLOTHES", "COLORS", "NUMBERS", "COMMON_EXPRESSIONS"
            )
            
            topicNames shouldContainAll requiredCategories
            
            // Each category should map to exactly one topic
            requiredCategories.forEach { categoryName ->
                val topic = VocabularyTopic.valueOf(categoryName)
                topic.name shouldBe categoryName
            }
        }
    }
    
    "vocabulary topic validation should work correctly" {
        checkAll(iterations = 100, Arb.string()) { randomString ->
            val validTopicNames = VocabularyTopic.values().map { it.name }
            val validDisplayNames = VocabularyTopic.getAllDisplayNames()
            
            // Valid topic names should be recognized
            validTopicNames.forEach { topicName ->
                val isValid = try {
                    VocabularyTopic.valueOf(topicName)
                    true
                } catch (e: IllegalArgumentException) {
                    false
                }
                isValid shouldBe true
            }
            
            // Valid display names should be found
            validDisplayNames.forEach { displayName ->
                val topic = VocabularyTopic.fromDisplayName(displayName)
                (topic != null) shouldBe true
            }
            
            // Random strings should generally not be valid topic names
            if (randomString.isNotBlank() && randomString !in validTopicNames) {
                val isValidTopic = try {
                    VocabularyTopic.valueOf(randomString.uppercase())
                    true
                } catch (e: IllegalArgumentException) {
                    false
                }
                
                // Should be false unless the random string happens to match a valid topic
                if (randomString.uppercase() !in validTopicNames) {
                    isValidTopic shouldBe false
                }
            }
        }
    }
    
    "vocabulary topics should maintain count consistency" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val allTopics = VocabularyTopic.values()
            val allDisplayNames = VocabularyTopic.getAllDisplayNames()
            
            // Number of topics should match number of display names
            allTopics.size shouldBe allDisplayNames.size
            
            // Should have exactly 14 topics as specified in requirements
            allTopics.size shouldBe 14
            
            // All display names should be unique
            allDisplayNames.distinct().size shouldBe allDisplayNames.size
            
            // All topic names should be unique (this is guaranteed by enum, but let's verify)
            allTopics.map { it.name }.distinct().size shouldBe allTopics.size
        }
    }
})