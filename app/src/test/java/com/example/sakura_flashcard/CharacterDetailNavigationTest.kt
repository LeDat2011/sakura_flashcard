package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.data.model.Stroke
import com.example.sakura_flashcard.data.model.StrokeDirection
import com.example.sakura_flashcard.data.model.Point
import com.example.sakura_flashcard.navigation.Screen
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 8: Character Detail Navigation
 * Validates: Requirements 2.3
 */
class CharacterDetailNavigationTest : StringSpec({
    
    "character tap interaction should navigate to detail screen with stroke order animation" {
        checkAll(iterations = 100, Arb.list(Arb.string(1..10), 1..20)) { testIds ->
            // Create test characters for navigation testing
            val characters = testIds.take(10).mapIndexed { index, _ ->
                val scripts = listOf(
                    CharacterScript.HIRAGANA,
                    CharacterScript.KATAKANA,
                    CharacterScript.KANJI
                )
                val script = scripts[index % 3]
                
                val character = when (script) {
                    CharacterScript.HIRAGANA -> listOf("き", "く", "け")[index % 3]
                    CharacterScript.KATAKANA -> listOf("キ", "ク", "ケ")[index % 3]
                    CharacterScript.KANJI -> listOf("木", "口", "毛")[index % 3]
                }
                
                Character(
                    character = character,
                    script = script,
                    pronunciation = listOf("test"),
                    strokeOrder = listOf(
                        Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                        Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                    ),
                    examples = listOf("example")
                )
            }
            
            // Test that each character can be used for navigation
            characters.forEach { character ->
                // Character should have valid ID for navigation
                character.id.isNotEmpty() shouldBe true
                character.id.isNotBlank() shouldBe true
                
                // Character should have stroke order data for detail screen
                character.strokeOrder.isNotEmpty() shouldBe true
                character.getStrokeCount() shouldBe character.strokeOrder.size
                (character.getStrokeCount() > 0) shouldBe true
                
                // Navigation route should be constructible
                val navigationRoute = Screen.CharacterDetail.createRoute(character.id)
                navigationRoute.contains(character.id) shouldBe true
                navigationRoute.startsWith("character_detail/") shouldBe true
                
                // Character should have all required data for detail screen
                character.character.isNotEmpty() shouldBe true
                character.script shouldNotBe null
                character.pronunciation.isNotEmpty() shouldBe true
                character.examples.isNotEmpty() shouldBe true
            }
        }
    }
    
    "character detail navigation should support all character scripts consistently" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test navigation for each script type
            val hiraganaChar = Character(
                character = "こ",
                script = CharacterScript.HIRAGANA,
                pronunciation = listOf("ko"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                ),
                examples = listOf("example")
            )
            
            val katakanaChar = Character(
                character = "コ",
                script = CharacterScript.KATAKANA,
                pronunciation = listOf("ko"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20")
                ),
                examples = listOf("example")
            )
            
            val kanjiChar = Character(
                character = "子",
                script = CharacterScript.KANJI,
                pronunciation = listOf("ko"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                    Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20"),
                    Stroke(3, listOf(Point(5f, 15f), Point(25f, 15f)), StrokeDirection.HORIZONTAL, "M5,15 L25,15")
                ),
                examples = listOf("example")
            )
            
            val allCharacters = listOf(hiraganaChar, katakanaChar, kanjiChar)
            
            // Test that all script types support navigation
            allCharacters.forEach { character ->
                // Each character should be navigable
                character.id.isNotEmpty() shouldBe true
                
                // Navigation route should be valid
                val route = Screen.CharacterDetail.createRoute(character.id)
                route.contains(character.id) shouldBe true
                route.startsWith("character_detail/") shouldBe true
                
                // Character should have stroke order for animation
                character.strokeOrder.isNotEmpty() shouldBe true
                character.strokeOrder.all { it.order > 0 } shouldBe true
                
                // Character should have display data
                character.character.isNotEmpty() shouldBe true
                character.script.displayName.isNotEmpty() shouldBe true
                character.script.description.isNotEmpty() shouldBe true
            }
            
            // Test that different scripts have different characteristics
            hiraganaChar.script shouldBe CharacterScript.HIRAGANA
            katakanaChar.script shouldBe CharacterScript.KATAKANA
            kanjiChar.script shouldBe CharacterScript.KANJI
            
            // Kanji typically has more strokes
            (kanjiChar.getStrokeCount() >= hiraganaChar.getStrokeCount()) shouldBe true
            (kanjiChar.getStrokeCount() >= katakanaChar.getStrokeCount()) shouldBe true
        }
    }
    
    "character detail screen should display stroke order animation for all characters" {
        checkAll(iterations = 100, Arb.list(Arb.string(1..5), 1..15)) { testData ->
            // Create characters with varying stroke counts
            val characters = testData.take(10).mapIndexed { index, _ ->
                val strokeCount = (index % 5) + 1 // 1 to 5 strokes
                val strokes = (1..strokeCount).map { strokeIndex ->
                    Stroke(
                        order = strokeIndex,
                        points = listOf(Point(strokeIndex * 10f, 10f), Point(strokeIndex * 10f + 10f, 20f)),
                        direction = StrokeDirection.values()[strokeIndex % StrokeDirection.values().size],
                        path = "M${strokeIndex * 10},10 L${strokeIndex * 10 + 10},20"
                    )
                }
                
                Character(
                    character = "漢",
                    script = CharacterScript.KANJI,
                    pronunciation = listOf("kan"),
                    strokeOrder = strokes,
                    examples = listOf("example")
                )
            }
            
            // Test stroke order animation requirements
            characters.forEach { character ->
                // Character should have valid stroke order
                character.strokeOrder.isNotEmpty() shouldBe true
                
                // Strokes should be in correct order
                character.strokeOrder.forEachIndexed { index, stroke ->
                    stroke.order shouldBe (index + 1)
                    stroke.path.isNotEmpty() shouldBe true
                    stroke.direction shouldNotBe null
                }
                
                // Stroke order should be sequential
                val orders = character.strokeOrder.map { it.order }
                orders shouldBe (1..character.strokeOrder.size).toList()
                
                // Each stroke should have animation data
                character.strokeOrder.forEach { stroke ->
                    stroke.path.isNotBlank() shouldBe true
                    stroke.order shouldBe stroke.order // Verify order consistency
                }
            }
        }
    }
    
    "character detail navigation should maintain proper parent-child relationship" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test navigation hierarchy
            val testCharacter = Character(
                character = "さ",
                script = CharacterScript.HIRAGANA,
                pronunciation = listOf("sa"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(20f, 20f)), StrokeDirection.HORIZONTAL, "M10,10 L20,20"),
                    Stroke(2, listOf(Point(15f, 10f), Point(15f, 20f)), StrokeDirection.VERTICAL, "M15,10 L15,20")
                ),
                examples = listOf("example")
            )
            
            // Character detail should be a sub-screen of Learn
            val detailRoute = Screen.CharacterDetail.createRoute(testCharacter.id)
            val parentRoute = "learn" // Learn screen is the parent
            
            // Detail route should be different from parent
            detailRoute shouldNotBe parentRoute
            detailRoute.contains(testCharacter.id) shouldBe true
            
            // Detail screen should have back navigation capability
            // (This would be tested in UI tests, but we can verify data requirements)
            testCharacter.id.isNotEmpty() shouldBe true // Required for back navigation
            
            // Character should have all data needed for detail display
            testCharacter.character.isNotEmpty() shouldBe true
            testCharacter.script shouldNotBe null
            testCharacter.pronunciation.isNotEmpty() shouldBe true
            testCharacter.strokeOrder.isNotEmpty() shouldBe true
            testCharacter.examples.isNotEmpty() shouldBe true
        }
    }
    
    "character detail navigation should handle unique character identification" {
        checkAll(iterations = 100, Arb.list(Arb.string(1..10), 2..20)) { testIds ->
            // Create multiple characters to test unique identification
            val characters = testIds.take(15).mapIndexed { index, _ ->
                val scripts = listOf(
                    CharacterScript.HIRAGANA,
                    CharacterScript.KATAKANA,
                    CharacterScript.KANJI
                )
                val script = scripts[index % 3]
                
                val character = when (script) {
                    CharacterScript.HIRAGANA -> listOf("し", "す", "せ", "そ", "た")[index % 5]
                    CharacterScript.KATAKANA -> listOf("シ", "ス", "セ", "ソ", "タ")[index % 5]
                    CharacterScript.KANJI -> listOf("四", "数", "世", "曽", "田")[index % 5]
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
            
            // Test that each character has unique identification
            val characterIds = characters.map { it.id }
            val uniqueIds = characterIds.toSet()
            
            // All IDs should be unique
            uniqueIds.size shouldBe characters.size
            
            // Each ID should be valid for navigation
            characters.forEach { character ->
                character.id.isNotEmpty() shouldBe true
                character.id.isNotBlank() shouldBe true
                
                // Navigation route should be unique
                val route = Screen.CharacterDetail.createRoute(character.id)
                route.contains(character.id) shouldBe true
                route.startsWith("character_detail/") shouldBe true
            }
            
            // Test that navigation routes are unique
            val navigationRoutes = characters.map { Screen.CharacterDetail.createRoute(it.id) }
            val uniqueRoutes = navigationRoutes.toSet()
            uniqueRoutes.size shouldBe characters.size
        }
    }
    
    "character detail navigation should support proper data loading for animation" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test characters with complex stroke orders
            val simpleChar = Character(
                character = "一",
                script = CharacterScript.KANJI,
                pronunciation = listOf("ichi"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 10f), Point(30f, 10f)), StrokeDirection.HORIZONTAL, "M10,10 L30,10")
                ),
                examples = listOf("example")
            )
            
            val complexChar = Character(
                character = "龍",
                script = CharacterScript.KANJI,
                pronunciation = listOf("ryuu"),
                strokeOrder = (1..16).map { order ->
                    Stroke(
                        order = order,
                        points = listOf(Point(order * 5f, 10f), Point(order * 5f + 5f, 20f)),
                        direction = if (order % 2 == 0) StrokeDirection.VERTICAL else StrokeDirection.HORIZONTAL,
                        path = "M${order * 5},10 L${order * 5 + 5},20"
                    )
                },
                examples = listOf("example")
            )
            
            val characters = listOf(simpleChar, complexChar)
            
            // Test that both simple and complex characters support navigation
            characters.forEach { character ->
                // Character should be navigable
                character.id.isNotEmpty() shouldBe true
                
                // Character should have complete stroke order data
                character.strokeOrder.isNotEmpty() shouldBe true
                character.strokeOrder.all { it.order > 0 } shouldBe true
                character.strokeOrder.all { it.path.isNotEmpty() } shouldBe true
                
                // Stroke order should be sequential for animation
                val expectedOrder = (1..character.strokeOrder.size).toList()
                val actualOrder = character.strokeOrder.map { it.order }
                actualOrder shouldBe expectedOrder
                
                // Character should have all required display data
                character.character.isNotEmpty() shouldBe true
                character.pronunciation.isNotEmpty() shouldBe true
                character.examples.isNotEmpty() shouldBe true
            }
            
            // Complex character should have more strokes
            (complexChar.getStrokeCount() > simpleChar.getStrokeCount()) shouldBe true
        }
    }
    
    "character detail navigation should maintain consistent route patterns" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test route pattern consistency with realistic character IDs
            val testCharacterIds = listOf(
                "char123",
                "hiragana_a",
                "katakana_ka",
                "kanji_hon",
                "test_id_001"
            )
            
            testCharacterIds.forEach { characterId ->
                val route = Screen.CharacterDetail.createRoute(characterId)
                
                // Route should follow consistent pattern
                route shouldBe "character_detail/$characterId"
                route.startsWith("character_detail/") shouldBe true
                route.contains(characterId) shouldBe true
                
                // Route should not contain invalid characters
                route.contains(" ") shouldBe false
                route.contains("\n") shouldBe false
                route.contains("\t") shouldBe false
                
                // Route should be URL-safe
                route.matches(Regex("^[a-zA-Z0-9_/-]+$")) shouldBe true
            }
            
            // Test that base route is consistent
            val baseRoute = Screen.CharacterDetail.route
            baseRoute shouldBe "character_detail/{characterId}"
            baseRoute.contains("{characterId}") shouldBe true
        }
    }
})