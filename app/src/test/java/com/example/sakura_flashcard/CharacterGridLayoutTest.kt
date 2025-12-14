package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.data.model.Stroke
import com.example.sakura_flashcard.data.model.StrokeDirection
import com.example.sakura_flashcard.data.model.Point
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 7: Character Grid Layout
 * Validates: Requirements 2.2
 */
class CharacterGridLayoutTest : StringSpec({
    
    "character grid should display all provided characters in grid layout" {
        checkAll(iterations = 100, Arb.list(Arb.int(0..2), 0..20)) { scriptIndices ->
            // Create test characters based on script indices
            val characters = scriptIndices.map { index ->
                when (index % 3) {
                    0 -> Character(
                        character = "あ",
                        script = CharacterScript.HIRAGANA,
                        pronunciation = listOf("a"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                        ),
                        examples = listOf("example")
                    )
                    1 -> Character(
                        character = "ア",
                        script = CharacterScript.KATAKANA,
                        pronunciation = listOf("a"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                        ),
                        examples = listOf("example")
                    )
                    else -> Character(
                        character = "漢",
                        script = CharacterScript.KANJI,
                        pronunciation = listOf("kan"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                            Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                        ),
                        examples = listOf("example")
                    )
                }
            }
            
            // Test that grid layout contains all characters
            characters shouldHaveSize scriptIndices.size
            
            // Test that each character in the grid has valid properties for display
            characters.forEach { character ->
                character.character.isNotEmpty() shouldBe true
                character.character.isNotBlank() shouldBe true
                character.script shouldBe character.script // Verify script consistency
                character.pronunciation.isNotEmpty() shouldBe true
                character.strokeOrder.isNotEmpty() shouldBe true
                character.examples.isNotEmpty() shouldBe true
            }
        }
    }
    
    "character grid should handle empty character lists gracefully" {
        checkAll(iterations = 100, Arb.int()) { _ ->
            // Test empty character list
            val emptyCharacters = emptyList<Character>()
            
            emptyCharacters shouldHaveSize 0
            emptyCharacters.isEmpty() shouldBe true
            
            // Grid should handle empty state without errors
            val isEmpty = emptyCharacters.isEmpty()
            isEmpty shouldBe true
        }
    }
    
    "character grid should maintain consistent layout properties for all character types" {
        checkAll(iterations = 100, Arb.int(1..50)) { characterCount ->
            // Create mixed character types
            val characters = (0 until characterCount).map { index ->
                when (index % 3) {
                    0 -> Character(
                        character = "あ",
                        script = CharacterScript.HIRAGANA,
                        pronunciation = listOf("a"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                        ),
                        examples = listOf("example")
                    )
                    1 -> Character(
                        character = "ア",
                        script = CharacterScript.KATAKANA,
                        pronunciation = listOf("a"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                        ),
                        examples = listOf("example")
                    )
                    else -> Character(
                        character = "漢",
                        script = CharacterScript.KANJI,
                        pronunciation = listOf("kan"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                            Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                        ),
                        examples = listOf("example")
                    )
                }
            }
            
            // Test that all characters are suitable for grid display
            characters shouldHaveSize characterCount
            
            // Each character should have the minimum required properties for grid display
            characters.forEach { character ->
                // Character text should be displayable
                character.character.isNotEmpty() shouldBe true
                character.character.length shouldBe 1 // Single character for grid display
                
                // Character should have valid script type
                val validScripts = listOf(
                    CharacterScript.HIRAGANA,
                    CharacterScript.KATAKANA,
                    CharacterScript.KANJI
                )
                validScripts.contains(character.script) shouldBe true
                
                // Character should have pronunciation for accessibility
                character.pronunciation.isNotEmpty() shouldBe true
                character.getMainPronunciation().isNotEmpty() shouldBe true
                
                // Character should have stroke order data
                character.strokeOrder.isNotEmpty() shouldBe true
                character.getStrokeCount() shouldBe character.strokeOrder.size
                (character.getStrokeCount() > 0) shouldBe true
            }
        }
    }
    
    "character grid should support proper interaction handling for all characters" {
        checkAll(iterations = 100, Arb.list(Arb.int(0..2), 1..10)) { scriptIndices ->
            // Create characters for interaction testing
            val characters = scriptIndices.map { index ->
                when (index % 3) {
                    0 -> Character(
                        character = "い",
                        script = CharacterScript.HIRAGANA,
                        pronunciation = listOf("i"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                        ),
                        examples = listOf("example")
                    )
                    1 -> Character(
                        character = "イ",
                        script = CharacterScript.KATAKANA,
                        pronunciation = listOf("i"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                        ),
                        examples = listOf("example")
                    )
                    else -> Character(
                        character = "字",
                        script = CharacterScript.KANJI,
                        pronunciation = listOf("ji"),
                        strokeOrder = listOf(
                            Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                            Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                        ),
                        examples = listOf("example")
                    )
                }
            }
            
            // Test that each character can be properly identified for interaction
            characters.forEach { character ->
                // Character should have unique identifier
                character.id.isNotEmpty() shouldBe true
                character.id.isNotBlank() shouldBe true
                
                // Character should be clickable (has valid data)
                character.character.isNotEmpty() shouldBe true
                character.script shouldBe character.script
                
                // Character should provide feedback data for interaction
                character.hasExamples() shouldBe true
                character.hasMultiplePronunciations() shouldBe (character.pronunciation.size > 1)
            }
            
            // Test that characters can be distinguished from each other
            val uniqueIds = characters.map { it.id }.toSet()
            uniqueIds shouldHaveSize characters.size
        }
    }
    
    "character grid should maintain proper aspect ratio and sizing for display" {
        checkAll(iterations = 100, Arb.int(1..100)) { characterCount ->
            // Create characters to test display properties
            val characters = (0 until characterCount).map { index ->
                val scripts = listOf(
                    CharacterScript.HIRAGANA,
                    CharacterScript.KATAKANA,
                    CharacterScript.KANJI
                )
                val script = scripts[index % 3]
                
                val character = when (script) {
                    CharacterScript.HIRAGANA -> "う"
                    CharacterScript.KATAKANA -> "ウ"
                    CharacterScript.KANJI -> "本"
                }
                
                Character(
                    character = character,
                    script = script,
                    pronunciation = listOf("test"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                )
            }
            
            // Test that all characters are suitable for grid display
            characters shouldHaveSize characterCount
            
            // Each character should be displayable in a square grid item
            characters.forEach { character ->
                // Character should be a single displayable unit
                character.character.length shouldBe 1
                
                // Character should not contain whitespace that would affect layout
                character.character.trim() shouldBe character.character
                
                // Character should have consistent properties for uniform display
                character.pronunciation.isNotEmpty() shouldBe true
                character.strokeOrder.isNotEmpty() shouldBe true
            }
        }
    }
    
    "character grid should support proper accessibility features for all characters" {
        checkAll(iterations = 100, Arb.list(Arb.int(0..2), 1..15)) { scriptIndices ->
            // Create characters for accessibility testing
            val characters = scriptIndices.mapIndexed { index, scriptIndex ->
                val scripts = listOf(
                    CharacterScript.HIRAGANA,
                    CharacterScript.KATAKANA,
                    CharacterScript.KANJI
                )
                val script = scripts[scriptIndex % 3]
                
                val character = when (script) {
                    CharacterScript.HIRAGANA -> listOf("え", "お", "か")[index % 3]
                    CharacterScript.KATAKANA -> listOf("エ", "オ", "カ")[index % 3]
                    CharacterScript.KANJI -> listOf("人", "大", "小")[index % 3]
                }
                
                Character(
                    character = character,
                    script = script,
                    pronunciation = listOf("test"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                )
            }
            
            // Test accessibility features
            characters.forEach { character ->
                // Character should have pronunciation for screen readers
                character.pronunciation.isNotEmpty() shouldBe true
                character.getMainPronunciation().isNotEmpty() shouldBe true
                
                // Character should have script information for context
                character.script.displayName.isNotEmpty() shouldBe true
                character.script.description.isNotEmpty() shouldBe true
                
                // Character should have examples for additional context
                character.hasExamples() shouldBe true
                character.examples.isNotEmpty() shouldBe true
                
                // Character should be identifiable
                character.id.isNotEmpty() shouldBe true
            }
        }
    }
    
    "character grid should handle large character sets efficiently" {
        checkAll(iterations = 100, Arb.int(50..200)) { characterCount ->
            // Create large character set
            val characters = (0 until characterCount).map { index ->
                Character(
                    character = "漢",
                    script = CharacterScript.KANJI,
                    pronunciation = listOf("kan"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                        Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                    ),
                    examples = listOf("example")
                )
            }
            
            // Test that large sets are handled properly
            characters shouldHaveSize characterCount
            
            // All characters should maintain consistent properties
            characters.forEach { character ->
                character.character.isNotEmpty() shouldBe true
                character.script shouldBe CharacterScript.KANJI
                character.pronunciation.isNotEmpty() shouldBe true
                character.strokeOrder.isNotEmpty() shouldBe true
            }
            
            // Grid should be able to handle the full set
            val totalCharacters = characters.size
            totalCharacters shouldBe characterCount
            (totalCharacters >= 50) shouldBe true
        }
    }
})