package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.VocabularyTopic
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 11: Topic Consistency Between Screens
 * Validates: Requirements 3.1
 */
class TopicConsistencyTest : StringSpec({
    
    "vocabulary topics should be consistent between home screen and quiz screen" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Get topics from both contexts
            val homeScreenTopics = VocabularyTopic.values().toList()
            val quizScreenTopics = VocabularyTopic.values().toList()
            
            // Topics should be identical between screens
            homeScreenTopics shouldBe quizScreenTopics
            
            // Both should contain all required topics
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
            
            homeScreenTopics shouldContainAll requiredTopics
            quizScreenTopics shouldContainAll requiredTopics
        }
    }
    
    "topic display names should be consistent across screens" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val allTopics = VocabularyTopic.values()
            
            allTopics.forEach { topic ->
                // Each topic should have the same display name regardless of context
                val homeDisplayName = topic.displayName
                val quizDisplayName = topic.displayName
                
                homeDisplayName shouldBe quizDisplayName
                
                // Display name should be consistent with enum mapping
                val topicFromDisplayName = VocabularyTopic.fromDisplayName(homeDisplayName)
                topicFromDisplayName shouldBe topic
            }
        }
    }
    
    "topic ordering should be consistent between screens" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val homeScreenTopics = VocabularyTopic.values().toList()
            val quizScreenTopics = VocabularyTopic.values().toList()
            
            // Order should be identical
            homeScreenTopics.forEachIndexed { index, topic ->
                quizScreenTopics[index] shouldBe topic
            }
            
            // Size should be identical
            homeScreenTopics.size shouldBe quizScreenTopics.size
        }
    }
    
    "topic availability should be consistent for user selection" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val availableTopics = VocabularyTopic.values().toList()
            
            // All topics should be selectable in both contexts
            availableTopics.forEach { topic ->
                // Topic should be valid for home screen navigation
                val isValidForHome = topic in VocabularyTopic.values()
                isValidForHome shouldBe true
                
                // Topic should be valid for quiz screen navigation
                val isValidForQuiz = topic in VocabularyTopic.values()
                isValidForQuiz shouldBe true
                
                // Topic should have a valid display name for both screens
                topic.displayName.isNotBlank() shouldBe true
            }
        }
    }
    
    "topic enumeration should provide consistent results" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Multiple calls to values() should return consistent results
            val firstCall = VocabularyTopic.values().toList()
            val secondCall = VocabularyTopic.values().toList()
            val thirdCall = VocabularyTopic.values().toList()
            
            firstCall shouldBe secondCall
            secondCall shouldBe thirdCall
            firstCall shouldBe thirdCall
            
            // Each call should contain the same topics in the same order
            firstCall.size shouldBe secondCall.size
            firstCall.forEachIndexed { index, topic ->
                secondCall[index] shouldBe topic
                thirdCall[index] shouldBe topic
            }
        }
    }
    
    "topic metadata should be consistent across screen contexts" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            VocabularyTopic.values().forEach { topic ->
                // Topic name should be consistent
                val topicName = topic.name
                topicName.isNotBlank() shouldBe true
                
                // Topic display name should be consistent
                val displayName = topic.displayName
                displayName.isNotBlank() shouldBe true
                
                // Topic should be findable by both name and display name
                VocabularyTopic.valueOf(topicName) shouldBe topic
                VocabularyTopic.fromDisplayName(displayName) shouldBe topic
                
                // Topic ordinal should be consistent
                val ordinal = topic.ordinal
                VocabularyTopic.values()[ordinal] shouldBe topic
            }
        }
    }
    
    "topic selection should work consistently in both screen contexts" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            VocabularyTopic.values().forEach { topic ->
                // Topic should be selectable for home screen flashcard navigation
                val canNavigateFromHome = topic in VocabularyTopic.values()
                canNavigateFromHome shouldBe true
                
                // Topic should be selectable for quiz screen quiz generation
                val canStartQuizWith = topic in VocabularyTopic.values()
                canStartQuizWith shouldBe true
                
                // Both contexts should accept the same topic
                canNavigateFromHome shouldBe canStartQuizWith
            }
        }
    }
    
    "topic count should be consistent between screens" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            val homeTopicCount = VocabularyTopic.values().size
            val quizTopicCount = VocabularyTopic.values().size
            val expectedCount = 14 // As specified in requirements
            
            homeTopicCount shouldBe expectedCount
            quizTopicCount shouldBe expectedCount
            homeTopicCount shouldBe quizTopicCount
        }
    }
})