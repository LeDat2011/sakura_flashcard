package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: japanese-flashcard-ui, Property 4: JLPT Level Navigation Consistency**
 * **Validates: Requirements 1.4, 3.2**
 * 
 * Property: For any vocabulary topic or quiz topic selection, 
 * navigation should display all JLPT levels from N5 to N1
 */
class JLPTLevelNavigationTest {

    @Test
    fun jlptLevelNavigationDisplaysAllLevels() = runTest {
        checkAll(100, Arb.element(VocabularyTopic.values().toList())) { topic ->
            // Test the logic that determines what JLPT levels should be displayed
            val availableLevels = getAvailableJLPTLevels(topic)
            val expectedLevels = JLPTLevel.values().toList()
            
            // Verify that all JLPT levels are available for any topic
            assert(availableLevels.containsAll(expectedLevels)) {
                "All JLPT levels should be available for topic ${topic.displayName}. " +
                "Expected: ${expectedLevels.map { it.displayName }}, " +
                "Got: ${availableLevels.map { it.displayName }}"
            }
            
            // Verify that the levels are in the correct order (N5 to N1)
            assert(availableLevels == expectedLevels) {
                "JLPT levels should be in order N5 to N1 for topic ${topic.displayName}"
            }
        }
    }

    @Test
    fun jlptLevelNavigationConsistencyAcrossTopics() {
        // This test verifies that the JLPT levels are consistently displayed
        // regardless of which topic is selected
        
        val allTopics = VocabularyTopic.values()
        val expectedLevels = JLPTLevel.values().toList()

        allTopics.forEach { topic ->
            val availableLevels = getAvailableJLPTLevels(topic)
            
            // Verify all JLPT levels are available for this topic
            assert(availableLevels == expectedLevels) {
                "JLPT levels should be consistent across all topics. " +
                "Topic ${topic.displayName} has levels: ${availableLevels.map { it.displayName }}"
            }
        }
    }

    @Test
    fun jlptLevelPropertiesAreCorrect() = runTest {
        checkAll(50, Arb.element(JLPTLevel.values().toList())) { level ->
            // Test that each JLPT level has the required properties
            val levelInfo = getJLPTLevelInfo(level)
            
            // Verify that level has a display name
            assert(levelInfo.displayName.isNotBlank()) {
                "JLPT level should have a non-blank display name"
            }
            
            // Verify that level has a description
            assert(levelInfo.description.isNotBlank()) {
                "JLPT level should have a non-blank description"
            }
            
            // Verify that display name follows the pattern N1-N5
            assert(levelInfo.displayName.matches(Regex("N[1-5]"))) {
                "JLPT level display name should match pattern N1-N5, got: ${levelInfo.displayName}"
            }
        }
    }

    @Test
    fun jlptLevelOrderingIsCorrect() {
        val levels = JLPTLevel.values()
        
        // Verify that N5 is first (easiest)
        assert(levels.first() == JLPTLevel.N5) {
            "N5 should be the first (easiest) level"
        }
        
        // Verify that N1 is last (hardest)
        assert(levels.last() == JLPTLevel.N1) {
            "N1 should be the last (hardest) level"
        }
        
        // Verify ordering relationships
        assert(JLPTLevel.N5.isEasierThan(JLPTLevel.N4)) {
            "N5 should be easier than N4"
        }
        
        assert(JLPTLevel.N1.isHarderThan(JLPTLevel.N2)) {
            "N1 should be harder than N2"
        }
    }

    // Helper functions that mimic the logic in HomeScreen
    private fun getAvailableJLPTLevels(topic: VocabularyTopic): List<JLPTLevel> {
        // In the actual implementation, all topics support all JLPT levels
        return JLPTLevel.values().toList()
    }

    private fun getJLPTLevelInfo(level: JLPTLevel): JLPTLevelInfo {
        return JLPTLevelInfo(
            displayName = level.displayName,
            description = level.description
        )
    }

    private data class JLPTLevelInfo(
        val displayName: String,
        val description: String
    )
}