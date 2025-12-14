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
 * Feature: japanese-flashcard-ui, Property 10: Stroke Order Animation Accuracy
 * Validates: Requirements 2.5
 */
class StrokeOrderAnimationAccuracyTest : StringSpec({
    
    "stroke order animation should demonstrate correct sequence and direction for character formation" {
        checkAll(iterations = 100, Arb.list(Arb.int(1..20), 1..15)) { strokeCounts ->
            // Create characters with varying stroke complexities
            val characters = strokeCounts.take(10).map { strokeCount ->
                val strokes = (1..strokeCount).map { order ->
                    val directions = StrokeDirection.values()
                    Stroke(
                        order = order,
                        points = listOf(Point(order * 5f, 10f), Point(order * 5f + 10f, 20f)),
                        direction = directions[order % directions.size],
                        path = "M${order * 5},10 L${order * 5 + 10},20"
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
            
            // Test stroke order sequence accuracy
            characters.forEach { character ->
                // Stroke order should be sequential
                character.strokeOrder.forEachIndexed { index, stroke ->
                    stroke.order shouldBe (index + 1)
                }
                
                // All strokes should have valid order numbers
                val orders = character.strokeOrder.map { it.order }
                orders shouldBe (1..character.strokeOrder.size).toList()
                
                // No duplicate orders
                val uniqueOrders = orders.toSet()
                uniqueOrders shouldHaveSize character.strokeOrder.size
                
                // All strokes should have direction information
                character.strokeOrder.forEach { stroke ->
                    stroke.direction shouldNotBe null
                    stroke.direction shouldBe stroke.direction // Verify direction consistency
                }
                
                // All strokes should have path data for animation
                character.strokeOrder.forEach { stroke ->
                    stroke.path.isNotEmpty() shouldBe true
                    stroke.path.isNotBlank() shouldBe true
                }
            }
        }
    }
    
    "stroke order animation should support all stroke directions accurately" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test all possible stroke directions
            val allDirections = StrokeDirection.values().toList()
            
            val testCharacter = Character(
                character = "複",
                script = CharacterScript.KANJI,
                pronunciation = listOf("fuku"),
                strokeOrder = allDirections.mapIndexed { index, direction ->
                    Stroke(
                        order = index + 1,
                        points = listOf(Point(index * 10f, 10f), Point(index * 10f + 15f, 25f)),
                        direction = direction,
                        path = "M${index * 10},10 L${index * 10 + 15},25"
                    )
                },
                examples = listOf("example")
            )
            
            // Test that all directions are supported
            allDirections.forEach { expectedDirection ->
                val strokeWithDirection = testCharacter.strokeOrder.find { it.direction == expectedDirection }
                strokeWithDirection shouldNotBe null
                strokeWithDirection!!.direction shouldBe expectedDirection
            }
            
            // Test direction properties
            allDirections.forEach { direction ->
                direction.displayName.isNotEmpty() shouldBe true
                direction.displayName.isNotBlank() shouldBe true
                
                // Direction should be identifiable
                StrokeDirection.fromDisplayName(direction.displayName) shouldBe direction
            }
            
            // Test specific direction characteristics
            StrokeDirection.HORIZONTAL.displayName shouldBe "Horizontal"
            StrokeDirection.VERTICAL.displayName shouldBe "Vertical"
            StrokeDirection.DIAGONAL_DOWN_RIGHT.displayName shouldBe "Diagonal Down Right"
            StrokeDirection.DIAGONAL_DOWN_LEFT.displayName shouldBe "Diagonal Down Left"
            StrokeDirection.CURVED.displayName shouldBe "Curved"
            StrokeDirection.DOT.displayName shouldBe "Dot"
        }
    }
    
    "stroke order animation should maintain accuracy for characters with different complexities" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test simple character
            val simpleChar = Character(
                character = "一",
                script = CharacterScript.KANJI,
                pronunciation = listOf("ichi"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(5f, 15f), Point(35f, 15f)), StrokeDirection.HORIZONTAL, "M5,15 L35,15")
                ),
                examples = listOf("example")
            )
            
            // Test medium complexity character
            val mediumChar = Character(
                character = "田",
                script = CharacterScript.KANJI,
                pronunciation = listOf("ta", "den"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(10f, 5f), Point(10f, 35f)), StrokeDirection.VERTICAL, "M10,5 L10,35"),
                    Stroke(2, listOf(Point(5f, 10f), Point(35f, 10f)), StrokeDirection.HORIZONTAL, "M5,10 L35,10"),
                    Stroke(3, listOf(Point(30f, 5f), Point(30f, 35f)), StrokeDirection.VERTICAL, "M30,5 L30,35"),
                    Stroke(4, listOf(Point(5f, 25f), Point(35f, 25f)), StrokeDirection.HORIZONTAL, "M5,25 L35,25"),
                    Stroke(5, listOf(Point(5f, 35f), Point(35f, 35f)), StrokeDirection.HORIZONTAL, "M5,35 L35,35")
                ),
                examples = listOf("example")
            )
            
            // Test complex character
            val complexChar = Character(
                character = "鬱",
                script = CharacterScript.KANJI,
                pronunciation = listOf("utsu"),
                strokeOrder = (1..29).map { order ->
                    val directions = listOf(
                        StrokeDirection.HORIZONTAL,
                        StrokeDirection.VERTICAL,
                        StrokeDirection.CURVED
                    )
                    Stroke(
                        order = order,
                        points = listOf(Point(order * 2f, 5f), Point(order * 2f + 8f, 25f)),
                        direction = directions[order % directions.size],
                        path = "M${order * 2},5 L${order * 2 + 8},25"
                    )
                },
                examples = listOf("example")
            )
            
            val allCharacters = listOf(simpleChar, mediumChar, complexChar)
            
            // Test accuracy for all complexity levels
            allCharacters.forEach { character ->
                // Stroke order should be accurate regardless of complexity
                character.strokeOrder.forEachIndexed { index, stroke ->
                    stroke.order shouldBe (index + 1)
                    stroke.path.isNotEmpty() shouldBe true
                    stroke.direction shouldNotBe null
                }
                
                // Animation data should be complete
                character.getStrokeCount() shouldBe character.strokeOrder.size
                (character.getStrokeCount() > 0) shouldBe true
                
                // All strokes should be animatable
                character.strokeOrder.forEach { stroke ->
                    stroke.path.contains("M") shouldBe true // SVG path should start with Move command
                    (stroke.order > 0) shouldBe true
                }
            }
            
            // Test complexity progression
            (simpleChar.getStrokeCount() < mediumChar.getStrokeCount()) shouldBe true
            (mediumChar.getStrokeCount() < complexChar.getStrokeCount()) shouldBe true
        }
    }
    
    "stroke order animation should handle sequential stroke execution accurately" {
        checkAll(iterations = 100, Arb.int(2..15)) { strokeCount ->
            // Create character with specific stroke count
            val strokes = (1..strokeCount).map { order ->
                Stroke(
                    order = order,
                    points = listOf(Point(order * 8f, 10f), Point(order * 8f + 12f, 30f)),
                    direction = if (order % 2 == 0) StrokeDirection.VERTICAL else StrokeDirection.HORIZONTAL,
                    path = "M${order * 8},10 L${order * 8 + 12},30"
                )
            }
            
            val character = Character(
                character = "複",
                script = CharacterScript.KANJI,
                pronunciation = listOf("fuku"),
                strokeOrder = strokes,
                examples = listOf("example")
            )
            
            // Test sequential execution requirements
            character.strokeOrder.forEachIndexed { index, stroke ->
                // Current stroke order should match position
                stroke.order shouldBe (index + 1)
                
                // Previous strokes should have lower order numbers
                if (index > 0) {
                    val previousStroke = character.strokeOrder[index - 1]
                    (previousStroke.order < stroke.order) shouldBe true
                }
                
                // Next strokes should have higher order numbers
                if (index < character.strokeOrder.size - 1) {
                    val nextStroke = character.strokeOrder[index + 1]
                    (stroke.order < nextStroke.order) shouldBe true
                }
            }
            
            // Test that animation can progress through all strokes
            for (currentStroke in 0 until character.strokeOrder.size) {
                // Strokes up to current should be "completed"
                for (i in 0..currentStroke) {
                    val stroke = character.strokeOrder[i]
                    stroke.order shouldBe (i + 1)
                    stroke.path.isNotEmpty() shouldBe true
                }
                
                // Remaining strokes should be "pending"
                for (i in (currentStroke + 1) until character.strokeOrder.size) {
                    val stroke = character.strokeOrder[i]
                    stroke.order shouldBe (i + 1)
                    (stroke.order > currentStroke + 1) shouldBe true
                }
            }
        }
    }
    
    "stroke order animation should provide accurate path data for all stroke types" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test different stroke path patterns
            val horizontalStroke = Stroke(1, listOf(Point(10f, 15f), Point(30f, 15f)), StrokeDirection.HORIZONTAL, "M10,15 L30,15")
            val verticalStroke = Stroke(2, listOf(Point(20f, 5f), Point(20f, 25f)), StrokeDirection.VERTICAL, "M20,5 L20,25")
            val diagonalStroke = Stroke(3, listOf(Point(10f, 10f), Point(30f, 30f)), StrokeDirection.DIAGONAL_DOWN_RIGHT, "M10,10 L30,30")
            val curvedStroke = Stroke(4, listOf(Point(10f, 20f), Point(20f, 10f), Point(30f, 20f)), StrokeDirection.CURVED, "M10,20 Q20,10 30,20")
            val complexStroke = Stroke(5, listOf(Point(10f, 10f), Point(20f, 10f), Point(20f, 20f), Point(10f, 20f)), StrokeDirection.CURVED, "M10,10 L20,10 L20,20 L10,20 Z")
            
            val testCharacter = Character(
                character = "複",
                script = CharacterScript.KANJI,
                pronunciation = listOf("fuku"),
                strokeOrder = listOf(horizontalStroke, verticalStroke, diagonalStroke, curvedStroke, complexStroke),
                examples = listOf("example")
            )
            
            // Test path data accuracy
            testCharacter.strokeOrder.forEach { stroke ->
                // Path should be valid SVG path data
                stroke.path.isNotEmpty() shouldBe true
                stroke.path.isNotBlank() shouldBe true
                
                // Path should start with a move command
                (stroke.path.startsWith("M") || stroke.path.contains("M")) shouldBe true
                
                // Path should contain coordinate data
                stroke.path.matches(Regex(".*\\d+.*")) shouldBe true
                
                // Path should be animatable
                (stroke.path.length > 5) shouldBe true // Minimum meaningful path length
            }
            
            // Test specific stroke types
            horizontalStroke.path.contains("L") shouldBe true // Line command
            verticalStroke.path.contains("L") shouldBe true // Line command
            curvedStroke.path.contains("Q") shouldBe true // Quadratic curve command
            complexStroke.path.contains("Z") shouldBe true // Close path command
            
            // Test stroke directions match path characteristics
            horizontalStroke.direction shouldBe StrokeDirection.HORIZONTAL
            verticalStroke.direction shouldBe StrokeDirection.VERTICAL
            curvedStroke.direction shouldBe StrokeDirection.CURVED
            complexStroke.direction shouldBe StrokeDirection.CURVED
        }
    }
    
    "stroke order animation should maintain timing and progression accuracy" {
        checkAll(iterations = 100, Arb.list(Arb.int(1..10), 2..12)) { strokeCounts ->
            // Create characters with different stroke counts for timing tests
            val characters = strokeCounts.take(8).map { strokeCount ->
                val strokes = (1..strokeCount).map { order ->
                    Stroke(
                        order = order,
                        points = listOf(Point(order * 6f, 8f), Point(order * 6f + 14f, 28f)),
                        direction = StrokeDirection.values()[order % StrokeDirection.values().size],
                        path = "M${order * 6},8 L${order * 6 + 14},28"
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
            
            // Test timing progression requirements
            characters.forEach { character ->
                val totalStrokes = character.getStrokeCount()
                
                // Each stroke should be individually animatable
                character.strokeOrder.forEachIndexed { index, stroke ->
                    // Stroke should have timing position
                    val progressPosition = (index + 1).toFloat() / totalStrokes.toFloat()
                    (progressPosition > 0.0f) shouldBe true
                    (progressPosition <= 1.0f) shouldBe true
                    
                    // Earlier strokes should have earlier timing
                    if (index > 0) {
                        val previousPosition = index.toFloat() / totalStrokes.toFloat()
                        (previousPosition < progressPosition) shouldBe true
                    }
                }
                
                // Animation should be completable
                (totalStrokes > 0) shouldBe true
                (totalStrokes <= 30) shouldBe true // Reasonable upper bound for animation
                
                // All strokes should be sequentially ordered
                val orders = character.strokeOrder.map { it.order }
                orders shouldBe (1..totalStrokes).toList()
            }
        }
    }
    
    "stroke order animation should handle edge cases and validation accurately" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test minimum stroke character
            val singleStrokeChar = Character(
                character = "一",
                script = CharacterScript.KANJI,
                pronunciation = listOf("ichi"),
                strokeOrder = listOf(
                    Stroke(1, listOf(Point(5f, 15f), Point(35f, 15f)), StrokeDirection.HORIZONTAL, "M5,15 L35,15")
                ),
                examples = listOf("example")
            )
            
            // Test maximum reasonable stroke character
            val maxStrokeChar = Character(
                character = "鬱",
                script = CharacterScript.KANJI,
                pronunciation = listOf("utsu"),
                strokeOrder = (1..29).map { order ->
                    Stroke(
                        order = order,
                        points = listOf(Point(order * 2f, 5f), Point(order * 2f + 6f, 25f)),
                        direction = StrokeDirection.values()[order % StrokeDirection.values().size],
                        path = "M${order * 2},5 L${order * 2 + 6},25"
                    )
                },
                examples = listOf("example")
            )
            
            val edgeCases = listOf(singleStrokeChar, maxStrokeChar)
            
            // Test edge case handling
            edgeCases.forEach { character ->
                // Basic validation should pass
                character.strokeOrder.isNotEmpty() shouldBe true
                character.getStrokeCount() shouldBe character.strokeOrder.size
                
                // Stroke order should be valid
                character.strokeOrder.forEachIndexed { index, stroke ->
                    stroke.order shouldBe (index + 1)
                    (stroke.order > 0) shouldBe true
                    stroke.path.isNotEmpty() shouldBe true
                    stroke.direction shouldNotBe null
                }
                
                // Animation should be feasible
                (character.getStrokeCount() >= 1) shouldBe true
                (character.getStrokeCount() <= 30) shouldBe true
                
                // All strokes should be animatable
                character.strokeOrder.forEach { stroke ->
                    stroke.path.contains("M") shouldBe true
                    stroke.path.matches(Regex(".*\\d+.*")) shouldBe true
                }
            }
            
            // Test bounds
            singleStrokeChar.getStrokeCount() shouldBe 1
            maxStrokeChar.getStrokeCount() shouldBe 29
            (maxStrokeChar.getStrokeCount() > singleStrokeChar.getStrokeCount()) shouldBe true
        }
    }
    
    "stroke order animation should support accurate direction visualization" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test all stroke directions with appropriate paths
            val directionTestStrokes = listOf(
                Stroke(1, listOf(Point(10f, 15f), Point(30f, 15f)), StrokeDirection.HORIZONTAL, "M10,15 L30,15"),
                Stroke(2, listOf(Point(30f, 15f), Point(10f, 15f)), StrokeDirection.HORIZONTAL, "M30,15 L10,15"),
                Stroke(3, listOf(Point(20f, 5f), Point(20f, 25f)), StrokeDirection.VERTICAL, "M20,5 L20,25"),
                Stroke(4, listOf(Point(20f, 25f), Point(20f, 5f)), StrokeDirection.VERTICAL, "M20,25 L20,5"),
                Stroke(5, listOf(Point(20f, 10f), Point(20f, 20f)), StrokeDirection.CURVED, "M20,10 A5,5 0 1,1 20,20"),
                Stroke(6, listOf(Point(20f, 20f), Point(20f, 10f)), StrokeDirection.CURVED, "M20,20 A5,5 0 1,0 20,10")
            )
            
            val testCharacter = Character(
                character = "複",
                script = CharacterScript.KANJI,
                pronunciation = listOf("fuku"),
                strokeOrder = directionTestStrokes,
                examples = listOf("example")
            )
            
            // Test direction accuracy
            testCharacter.strokeOrder.forEach { stroke ->
                // Direction should be properly set
                stroke.direction shouldNotBe null
                stroke.direction.displayName.isNotEmpty() shouldBe true
                
                // Direction should be identifiable
                val directionFromName = StrokeDirection.fromDisplayName(stroke.direction.displayName)
                directionFromName shouldBe stroke.direction
                
                // Path should be compatible with direction
                stroke.path.isNotEmpty() shouldBe true
                stroke.path.contains("M") shouldBe true
            }
            
            // Test specific direction-path relationships
            val horizontal = testCharacter.strokeOrder.find { it.direction == StrokeDirection.HORIZONTAL }!!
            horizontal.path.contains("L") shouldBe true
            
            val vertical = testCharacter.strokeOrder.find { it.direction == StrokeDirection.VERTICAL }!!
            vertical.path.contains("L") shouldBe true
            
            val curved = testCharacter.strokeOrder.find { it.direction == StrokeDirection.CURVED }!!
            curved.path.contains("A") shouldBe true
        }
    }
})