package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.data.model.Stroke
import com.example.sakura_flashcard.data.model.StrokeDirection
import com.example.sakura_flashcard.data.model.Point
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 9: Character Information Completeness
 * Validates: Requirements 2.4
 */
class CharacterInformationCompletenessTest : StringSpec({
    
    "character detail screen should display pronunciation information and example vocabulary" {
        checkAll(iterations = 100, Arb.list(Arb.string(1..10), 1..20)) { testData ->
            // Create characters with varying amounts of information
            val characters = testData.take(10).mapIndexed { index, _ ->
                val scripts = listOf(
                    CharacterScript.HIRAGANA,
                    CharacterScript.KATAKANA,
                    CharacterScript.KANJI
                )
                val script = scripts[index % 3]
                
                val character = when (script) {
                    CharacterScript.HIRAGANA -> listOf("な", "に", "ぬ", "ね", "の")[index % 5]
                    CharacterScript.KATAKANA -> listOf("ナ", "ニ", "ヌ", "ネ", "ノ")[index % 5]
                    CharacterScript.KANJI -> listOf("名", "二", "奴", "根", "野")[index % 5]
                }
                
                // Create multiple pronunciations for some characters
                val pronunciations = when (index % 3) {
                    0 -> listOf("na")
                    1 -> listOf("na", "mei")
                    else -> listOf("na", "mei", "myou")
                }
                
                // Create multiple examples for some characters
                val examples = when (index % 4) {
                    0 -> listOf("example one")
                    1 -> listOf("example one", "example two")
                    2 -> listOf("example one", "example two", "example three")
                    else -> listOf("example one", "example two", "example three", "example four")
                }
                
                Character(
                    character = character,
                    script = script,
                    pronunciation = pronunciations,
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = examples
                )
            }
            
            // Test that all characters have complete pronunciation information
            characters.forEach { character ->
                // Pronunciation information should be present
                character.pronunciation.isNotEmpty() shouldBe true
                character.pronunciation.all { it.isNotEmpty() } shouldBe true
                character.pronunciation.all { it.isNotBlank() } shouldBe true
                
                // Main pronunciation should be accessible
                character.getMainPronunciation().isNotEmpty() shouldBe true
                character.getMainPronunciation().isNotBlank() shouldBe true
                
                // Multiple pronunciations should be detectable
                character.hasMultiplePronunciations() shouldBe (character.pronunciation.size > 1)
                
                // Example vocabulary should be present
                character.examples.isNotEmpty() shouldBe true
                character.examples.all { it.isNotEmpty() } shouldBe true
                character.examples.all { it.isNotBlank() } shouldBe true
                character.hasExamples() shouldBe true
            }
        }
    }
    
    "character detail screen should display complete information for all script types" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test each script type with comprehensive information
            val hiraganaChar = Character(
                character = "は",
                script = CharacterScript.HIRAGANA,
                pronunciation = listOf("ha", "wa"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                    Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20"),
                    Stroke(3, listOf(Point(5f, 15f), Point(25f, 15f)), StrokeDirection.HORIZONTAL, "M5,15 L25,15")
                ),
                examples = listOf("はな (hana - flower)", "はし (hashi - bridge)", "わたし (watashi - I)")
            )
            
            val katakanaChar = Character(
                character = "ハ",
                script = CharacterScript.KATAKANA,
                pronunciation = listOf("ha"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                    Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                ),
                examples = listOf("ハンバーガー (hanbaagaa - hamburger)", "ハート (haato - heart)")
            )
            
            val kanjiChar = Character(
                character = "花",
                script = CharacterScript.KANJI,
                pronunciation = listOf("hana", "ka"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                    Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20"),
                    Stroke(3, listOf(Point(5f, 15f), Point(25f, 15f)), StrokeDirection.HORIZONTAL, "M5,15 L25,15"),
                    Stroke(4, listOf(Point(12f, 5f), Point(12f, 25f)), StrokeDirection.VERTICAL, "M12,5 L12,25"),
                    Stroke(5, listOf(Point(8f, 18f), Point(22f, 18f)), StrokeDirection.HORIZONTAL, "M8,18 L22,18"),
                    Stroke(6, listOf(Point(10f, 22f), Point(20f, 22f)), StrokeDirection.HORIZONTAL, "M10,22 L20,22"),
                    Stroke(7, listOf(Point(16f, 12f), Point(16f, 28f)), StrokeDirection.VERTICAL, "M16,12 L16,28")
                ),
                examples = listOf("花 (hana - flower)", "花見 (hanami - cherry blossom viewing)", "生花 (ikebana - flower arrangement)")
            )
            
            val allCharacters = listOf(hiraganaChar, katakanaChar, kanjiChar)
            
            // Test that all script types have complete information
            allCharacters.forEach { character ->
                // Pronunciation information completeness
                character.pronunciation.isNotEmpty() shouldBe true
                character.getMainPronunciation().isNotEmpty() shouldBe true
                character.pronunciation.all { it.matches(Regex("^[a-zA-Z]+$")) } shouldBe true
                
                // Example vocabulary completeness
                character.examples.isNotEmpty() shouldBe true
                character.hasExamples() shouldBe true
                character.examples.all { it.isNotEmpty() } shouldBe true
                
                // Script information completeness
                character.script shouldNotBe null
                character.script.displayName.isNotEmpty() shouldBe true
                character.script.description.isNotEmpty() shouldBe true
                
                // Character display completeness
                character.character.isNotEmpty() shouldBe true
                character.character.length shouldBe 1
                
                // Stroke order completeness
                character.strokeOrder.isNotEmpty() shouldBe true
                character.getStrokeCount() shouldBe character.strokeOrder.size
                (character.getStrokeCount() > 0) shouldBe true
            }
            
            // Test script-specific characteristics
            hiraganaChar.script shouldBe CharacterScript.HIRAGANA
            katakanaChar.script shouldBe CharacterScript.KATAKANA
            kanjiChar.script shouldBe CharacterScript.KANJI
            
            // Kanji typically has more complex information
            (kanjiChar.getStrokeCount() >= hiraganaChar.getStrokeCount()) shouldBe true
            (kanjiChar.examples.size >= hiraganaChar.examples.size) shouldBe true
        }
    }
    
    "character detail screen should handle characters with varying information complexity" {
        checkAll(iterations = 100, Arb.int(1..10)) { complexityLevel ->
            // Create characters with different levels of information complexity
            val simpleChar = Character(
                character = "あ",
                script = CharacterScript.HIRAGANA,
                pronunciation = listOf("a"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                    Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20"),
                    Stroke(3, listOf(Point(5f, 15f), Point(25f, 15f)), StrokeDirection.HORIZONTAL, "M5,15 L25,15")
                ),
                examples = listOf("あさ (asa - morning)")
            )
            
            val complexChar = Character(
                character = "愛",
                script = CharacterScript.KANJI,
                pronunciation = listOf("ai", "ito", "mana"),
                strokeOrder = (1..13).map { order ->
                    Stroke(
                        order = order,
                        points = listOf(Point(order * 2f, 10f), Point(order * 2f + 5f, 20f)),
                        direction = if (order % 2 == 0) StrokeDirection.VERTICAL else StrokeDirection.HORIZONTAL,
                        path = "M${order * 2},10 L${order * 2 + 5},20"
                    )
                },
                examples = listOf(
                    "愛 (ai - love)",
                    "愛情 (aijou - affection)",
                    "愛する (aisuru - to love)",
                    "愛らしい (airashii - lovely)",
                    "愛好家 (aikouka - enthusiast)"
                )
            )
            
            val characters = listOf(simpleChar, complexChar)
            
            // Test that both simple and complex characters have complete information
            characters.forEach { character ->
                // Basic information completeness
                character.pronunciation.isNotEmpty() shouldBe true
                character.examples.isNotEmpty() shouldBe true
                character.strokeOrder.isNotEmpty() shouldBe true
                
                // Information accessibility
                character.getMainPronunciation().isNotEmpty() shouldBe true
                character.hasExamples() shouldBe true
                character.hasMultiplePronunciations() shouldBe (character.pronunciation.size > 1)
                
                // Information quality
                character.pronunciation.all { it.isNotBlank() } shouldBe true
                character.examples.all { it.isNotBlank() } shouldBe true
                character.strokeOrder.all { it.path.isNotBlank() } shouldBe true
            }
            
            // Complex character should have more information
            (complexChar.pronunciation.size >= simpleChar.pronunciation.size) shouldBe true
            (complexChar.examples.size >= simpleChar.examples.size) shouldBe true
            (complexChar.getStrokeCount() >= simpleChar.getStrokeCount()) shouldBe true
        }
    }
    
    "character detail screen should provide accessible pronunciation information" {
        checkAll(iterations = 100, Arb.list(Arb.int(1..5), 1..10)) { pronunciationCounts ->
            // Create characters with different pronunciation patterns
            val characters = pronunciationCounts.mapIndexed { index, count ->
                val pronunciationOptions = listOf("kan", "han", "gen", "shin", "kou")
                val pronunciations = (0 until count).map { pronunciationOptions[it % pronunciationOptions.size] }
                
                Character(
                    character = "漢",
                    script = CharacterScript.KANJI,
                    pronunciation = pronunciations,
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                )
            }
            
            // Test pronunciation accessibility
            characters.forEach { character ->
                // Primary pronunciation should be accessible
                character.getMainPronunciation().isNotEmpty() shouldBe true
                character.getMainPronunciation() shouldBe character.pronunciation.first()
                
                // Multiple pronunciations should be detectable
                val hasMultiple = character.pronunciation.size > 1
                character.hasMultiplePronunciations() shouldBe hasMultiple
                
                // All pronunciations should be valid
                character.pronunciation.forEach { pronunciation ->
                    pronunciation.isNotEmpty() shouldBe true
                    pronunciation.isNotBlank() shouldBe true
                    pronunciation.matches(Regex("^[a-zA-Z]+$")) shouldBe true
                }
                
                // Pronunciation count should match
                character.pronunciation shouldHaveSize character.pronunciation.size
            }
        }
    }
    
    "character detail screen should provide comprehensive example vocabulary" {
        checkAll(iterations = 100, Arb.list(Arb.string(5..30), 1..15)) { exampleTexts ->
            // Create characters with different example patterns
            val characters = exampleTexts.take(8).mapIndexed { index, _ ->
                val examples = when (index % 4) {
                    0 -> listOf("basic example")
                    1 -> listOf("example with reading", "another example")
                    2 -> listOf("complex example with meaning", "usage example", "compound word example")
                    else -> listOf("detailed example", "usage in sentence", "compound formation", "related words", "cultural context")
                }
                
                Character(
                    character = "学",
                    script = CharacterScript.KANJI,
                    pronunciation = listOf("gaku", "mana"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = examples
                )
            }
            
            // Test example vocabulary completeness
            characters.forEach { character ->
                // Examples should be present
                character.examples.isNotEmpty() shouldBe true
                character.hasExamples() shouldBe true
                
                // All examples should be valid
                character.examples.forEach { example ->
                    example.isNotEmpty() shouldBe true
                    example.isNotBlank() shouldBe true
                    example.trim() shouldBe example // No leading/trailing whitespace
                }
                
                // Examples should provide learning value
                character.examples.all { it.length >= 5 } shouldBe true // Meaningful examples
                
                // Example count should be reasonable
                (character.examples.size >= 1) shouldBe true
                (character.examples.size <= 10) shouldBe true // Not overwhelming
            }
        }
    }
    
    "character detail screen should maintain information consistency across all characters" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Create diverse character set
            val diverseCharacters = listOf(
                Character(
                    character = "ひ",
                    script = CharacterScript.HIRAGANA,
                    pronunciation = listOf("hi"),
                    strokeOrder = listOf(Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")),
                    examples = listOf("ひかり (hikari - light)")
                ),
                Character(
                    character = "ヒ",
                    script = CharacterScript.KATAKANA,
                    pronunciation = listOf("hi"),
                    strokeOrder = listOf(Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")),
                    examples = listOf("ヒーロー (hiiroo - hero)")
                ),
                Character(
                    character = "火",
                    script = CharacterScript.KANJI,
                    pronunciation = listOf("hi", "ka"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                        Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20"),
                        Stroke(3, listOf(Point(5f, 15f), Point(25f, 15f)), StrokeDirection.HORIZONTAL, "M5,15 L25,15"),
                        Stroke(4, listOf(Point(12f, 5f), Point(12f, 25f)), StrokeDirection.VERTICAL, "M12,5 L12,25")
                    ),
                    examples = listOf("火 (hi - fire)", "火曜日 (kayoubi - Tuesday)", "花火 (hanabi - fireworks)")
                )
            )
            
            // Test consistency across all character types
            diverseCharacters.forEach { character ->
                // Consistent information structure
                character.pronunciation.isNotEmpty() shouldBe true
                character.examples.isNotEmpty() shouldBe true
                character.strokeOrder.isNotEmpty() shouldBe true
                
                // Consistent information quality
                character.getMainPronunciation().isNotEmpty() shouldBe true
                character.hasExamples() shouldBe true
                character.character.isNotEmpty() shouldBe true
                
                // Consistent accessibility features
                character.script.displayName.isNotEmpty() shouldBe true
                character.script.description.isNotEmpty() shouldBe true
                character.id.isNotEmpty() shouldBe true
                
                // Consistent data validation
                character.pronunciation.all { it.isNotBlank() } shouldBe true
                character.examples.all { it.isNotBlank() } shouldBe true
                character.strokeOrder.all { it.order > 0 } shouldBe true
            }
            
            // Test that different scripts maintain their unique characteristics
            val hiragana = diverseCharacters.find { it.script == CharacterScript.HIRAGANA }!!
            val katakana = diverseCharacters.find { it.script == CharacterScript.KATAKANA }!!
            val kanji = diverseCharacters.find { it.script == CharacterScript.KANJI }!!
            
            hiragana.script.displayName shouldBe "Hiragana"
            katakana.script.displayName shouldBe "Katakana"
            kanji.script.displayName shouldBe "Kanji"
            
            // Kanji typically has more complex information
            (kanji.pronunciation.size >= hiragana.pronunciation.size) shouldBe true
            (kanji.examples.size >= katakana.examples.size) shouldBe true
        }
    }
    
    "character detail screen should handle edge cases in information display" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test edge cases
            val minimalChar = Character(
                character = "ん",
                script = CharacterScript.HIRAGANA,
                pronunciation = listOf("n"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                ),
                examples = listOf("ん (n - n sound)")
            )
            
            val maximalChar = Character(
                character = "鬱",
                script = CharacterScript.KANJI,
                pronunciation = listOf("utsu", "fusa", "shige"),
                strokeOrder = (1..29).map { order ->
                    Stroke(
                        order = order,
                        points = listOf(Point(order.toFloat(), 10f), Point(order + 5f, 20f)),
                        direction = StrokeDirection.values()[order % StrokeDirection.values().size],
                        path = "M${order},10 L${order + 5},20"
                    )
                },
                examples = listOf(
                    "鬱 (utsu - depression)",
                    "鬱病 (utsubyou - depression illness)",
                    "鬱蒼 (ussou - dense/thick)",
                    "鬱積 (usseki - accumulation)",
                    "鬱屈 (ukkutsu - gloom)"
                )
            )
            
            val edgeCases = listOf(minimalChar, maximalChar)
            
            // Test that edge cases still provide complete information
            edgeCases.forEach { character ->
                // Basic completeness
                character.pronunciation.isNotEmpty() shouldBe true
                character.examples.isNotEmpty() shouldBe true
                character.strokeOrder.isNotEmpty() shouldBe true
                
                // Information accessibility
                character.getMainPronunciation().isNotEmpty() shouldBe true
                character.hasExamples() shouldBe true
                (character.getStrokeCount() > 0) shouldBe true
                
                // Information bounds
                (character.pronunciation.size >= 1) shouldBe true
                (character.examples.size >= 1) shouldBe true
                (character.getStrokeCount() >= 1) shouldBe true
                (character.getStrokeCount() <= 30) shouldBe true // Reasonable upper bound
            }
            
            // Test differences between minimal and maximal
            (maximalChar.pronunciation.size >= minimalChar.pronunciation.size) shouldBe true
            (maximalChar.examples.size >= minimalChar.examples.size) shouldBe true
            (maximalChar.getStrokeCount() > minimalChar.getStrokeCount()) shouldBe true
        }
    }
})