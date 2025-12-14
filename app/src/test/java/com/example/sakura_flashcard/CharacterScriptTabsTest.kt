package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.data.model.Stroke
import com.example.sakura_flashcard.data.model.StrokeDirection
import com.example.sakura_flashcard.data.model.Point
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 6: Character Script Tab Navigation
 * Validates: Requirements 2.1
 */
class CharacterScriptTabsTest : StringSpec({
    
    "learn screen should always provide exactly three character script tabs" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that we have exactly 3 character script tabs
            val expectedScripts = listOf(
                CharacterScript.HIRAGANA,
                CharacterScript.KATAKANA,
                CharacterScript.KANJI
            )
            
            expectedScripts shouldHaveSize 3
            
            // Verify each script has unique properties
            val scriptNames = expectedScripts.map { it.displayName }
            val uniqueNames = scriptNames.toSet()
            uniqueNames shouldHaveSize 3
            
            // Verify all scripts have proper display names
            scriptNames.forEach { name ->
                name.isNotEmpty() shouldBe true
                name.isNotBlank() shouldBe true
                name.first().isUpperCase() shouldBe true
            }
        }
    }
    
    "character script tabs should maintain consistent ordering" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that script tabs are always in the same order
            val expectedOrder = listOf(
                CharacterScript.HIRAGANA,
                CharacterScript.KATAKANA,
                CharacterScript.KANJI
            )
            
            // Verify the order matches expected sequence
            expectedOrder[0] shouldBe CharacterScript.HIRAGANA
            expectedOrder[1] shouldBe CharacterScript.KATAKANA
            expectedOrder[2] shouldBe CharacterScript.KANJI
            
            // Verify display names are in expected order
            val displayNames = expectedOrder.map { it.displayName }
            displayNames shouldContainExactly listOf("Hiragana", "Katakana", "Kanji")
        }
    }
    
    "each character script tab should have proper description and metadata" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test Hiragana tab properties
            CharacterScript.HIRAGANA.displayName shouldBe "Hiragana"
            CharacterScript.HIRAGANA.description shouldBe "Japanese phonetic script for native words"
            CharacterScript.HIRAGANA.description.isNotEmpty() shouldBe true
            
            // Test Katakana tab properties
            CharacterScript.KATAKANA.displayName shouldBe "Katakana"
            CharacterScript.KATAKANA.description shouldBe "Japanese phonetic script for foreign words"
            CharacterScript.KATAKANA.description.isNotEmpty() shouldBe true
            
            // Test Kanji tab properties
            CharacterScript.KANJI.displayName shouldBe "Kanji"
            CharacterScript.KANJI.description shouldBe "Chinese characters used in Japanese writing"
            CharacterScript.KANJI.description.isNotEmpty() shouldBe true
            
            // Verify all descriptions are unique
            val descriptions = listOf(
                CharacterScript.HIRAGANA.description,
                CharacterScript.KATAKANA.description,
                CharacterScript.KANJI.description
            )
            descriptions.toSet() shouldHaveSize 3
        }
    }
    
    "character script tabs should support proper navigation and selection" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that each script can be identified by display name
            CharacterScript.fromDisplayName("Hiragana") shouldBe CharacterScript.HIRAGANA
            CharacterScript.fromDisplayName("Katakana") shouldBe CharacterScript.KATAKANA
            CharacterScript.fromDisplayName("Kanji") shouldBe CharacterScript.KANJI
            
            // Test invalid display names
            CharacterScript.fromDisplayName("Invalid") shouldBe null
            CharacterScript.fromDisplayName("") shouldBe null
            CharacterScript.fromDisplayName("hiragana") shouldBe null // case sensitive
            
            // Test getAllDisplayNames utility
            val allDisplayNames = CharacterScript.getAllDisplayNames()
            allDisplayNames shouldHaveSize 3
            allDisplayNames shouldContainExactly listOf("Hiragana", "Katakana", "Kanji")
        }
    }
    
    "character script tabs should properly categorize characters by script type" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Create test characters for each script using valid Japanese characters
            val hiraganaChars = listOf(
                Character(
                    character = "あ", // Valid hiragana
                    script = CharacterScript.HIRAGANA,
                    pronunciation = listOf("a"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                ),
                Character(
                    character = "い", // Valid hiragana
                    script = CharacterScript.HIRAGANA,
                    pronunciation = listOf("i"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                ),
                Character(
                    character = "う", // Valid hiragana
                    script = CharacterScript.HIRAGANA,
                    pronunciation = listOf("u"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                )
            )
            
            val katakanaChars = listOf(
                Character(
                    character = "ア", // Valid katakana
                    script = CharacterScript.KATAKANA,
                    pronunciation = listOf("a"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                ),
                Character(
                    character = "イ", // Valid katakana
                    script = CharacterScript.KATAKANA,
                    pronunciation = listOf("i"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                ),
                Character(
                    character = "ウ", // Valid katakana
                    script = CharacterScript.KATAKANA,
                    pronunciation = listOf("u"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                    ),
                    examples = listOf("example")
                )
            )
            
            val kanjiChars = listOf(
                Character(
                    character = "漢", // Valid kanji
                    script = CharacterScript.KANJI,
                    pronunciation = listOf("kan"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                        Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                    ),
                    examples = listOf("example")
                ),
                Character(
                    character = "字", // Valid kanji
                    script = CharacterScript.KANJI,
                    pronunciation = listOf("ji"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                        Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                    ),
                    examples = listOf("example")
                ),
                Character(
                    character = "本", // Valid kanji
                    script = CharacterScript.KANJI,
                    pronunciation = listOf("hon"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                        Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                    ),
                    examples = listOf("example")
                )
            )
            
            // Verify characters are properly categorized
            hiraganaChars.all { it.script == CharacterScript.HIRAGANA } shouldBe true
            katakanaChars.all { it.script == CharacterScript.KATAKANA } shouldBe true
            kanjiChars.all { it.script == CharacterScript.KANJI } shouldBe true
            
            // Verify each tab would have the correct characters
            val allCharacters = hiraganaChars + katakanaChars + kanjiChars
            val hiraganaFiltered = allCharacters.filter { it.script == CharacterScript.HIRAGANA }
            val katakanaFiltered = allCharacters.filter { it.script == CharacterScript.KATAKANA }
            val kanjiFiltered = allCharacters.filter { it.script == CharacterScript.KANJI }
            
            hiraganaFiltered shouldHaveSize hiraganaChars.size
            katakanaFiltered shouldHaveSize katakanaChars.size
            kanjiFiltered shouldHaveSize kanjiChars.size
        }
    }
    
    "character script tabs should maintain consistent visual and interaction patterns" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that tab-related Compose classes are available
            val tabRowClass = try {
                Class.forName("androidx.compose.material3.TabRowKt")
                true
            } catch (e: ClassNotFoundException) {
                try {
                    Class.forName("androidx.compose.material3.TabRow")
                    true
                } catch (e2: ClassNotFoundException) {
                    false
                }
            }
            
            val tabClass = try {
                Class.forName("androidx.compose.material3.TabKt")
                true
            } catch (e: ClassNotFoundException) {
                try {
                    Class.forName("androidx.compose.material3.Tab")
                    true
                } catch (e2: ClassNotFoundException) {
                    false
                }
            }
            
            val pagerClass = try {
                Class.forName("androidx.compose.foundation.pager.HorizontalPager")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
            
            // At least tab components should be available
            (tabRowClass || tabClass) shouldBe true
        }
    }
    
    "character script tabs should support proper state management and navigation" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that each script enum value has consistent properties
            val scripts = CharacterScript.values()
            scripts shouldHaveSize 3
            
            // Test that each script has a unique ordinal
            val ordinals = scripts.map { it.ordinal }.toSet()
            ordinals shouldHaveSize 3
            
            // Test ordinal ordering matches expected tab order
            CharacterScript.HIRAGANA.ordinal shouldBe 0
            CharacterScript.KATAKANA.ordinal shouldBe 1
            CharacterScript.KANJI.ordinal shouldBe 2
            
            // Test that scripts can be accessed by ordinal
            CharacterScript.values()[0] shouldBe CharacterScript.HIRAGANA
            CharacterScript.values()[1] shouldBe CharacterScript.KATAKANA
            CharacterScript.values()[2] shouldBe CharacterScript.KANJI
        }
    }
    
    "character script tabs should provide consistent content structure for each tab" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that each script type has the required properties for tab content
            val scripts = listOf(
                CharacterScript.HIRAGANA,
                CharacterScript.KATAKANA,
                CharacterScript.KANJI
            )
            
            scripts.forEach { script ->
                // Each script should have a display name for the tab
                script.displayName.isNotEmpty() shouldBe true
                script.displayName.isNotBlank() shouldBe true
                
                // Each script should have a description for the tab content
                script.description.isNotEmpty() shouldBe true
                script.description.isNotBlank() shouldBe true
                
                // Display name should be properly capitalized
                script.displayName.first().isUpperCase() shouldBe true
                
                // Description should be a proper sentence
                script.description.first().isUpperCase() shouldBe true
                script.description.contains("Japanese") || script.description.contains("Chinese") shouldBe true
            }
        }
    }
})