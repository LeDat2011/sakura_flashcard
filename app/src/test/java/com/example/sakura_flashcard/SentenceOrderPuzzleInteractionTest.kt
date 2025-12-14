package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.GameInput
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * **Feature: japanese-flashcard-ui, Property 16: Sentence Order Puzzle Interaction**
 * **Validates: Requirements 4.2**
 * 
 * Property: For any Sentence Order Puzzle game, users should be able to drag words 
 * to form correct Japanese sentences
 */
class SentenceOrderPuzzleInteractionTest : StringSpec({

    "Property 16: Sentence Order Puzzle Interaction - WordOrder input should accept valid word lists" {
        checkAll(100, Arb.list(Arb.string(1..10), 1..10)) { words ->
            val nonEmptyWords = words.filter { it.isNotBlank() }
            if (nonEmptyWords.isNotEmpty()) {
                val wordOrderInput = GameInput.WordOrder(nonEmptyWords)
                
                wordOrderInput.shouldBeInstanceOf<GameInput.WordOrder>()
                wordOrderInput.orderedWords shouldBe nonEmptyWords
            }
        }
    }

    "Property 16: Sentence Order Puzzle Interaction - WordOrder input should validate non-empty words" {
        checkAll(100, Arb.list(Arb.string(1..10), 1..5)) { words ->
            val nonEmptyWords = words.filter { it.isNotBlank() }
            if (nonEmptyWords.isNotEmpty()) {
                val wordOrderInput = GameInput.WordOrder(nonEmptyWords)
                
                // All words should be non-blank
                wordOrderInput.orderedWords.all { it.isNotBlank() } shouldBe true
                wordOrderInput.orderedWords.size shouldBe nonEmptyWords.size
            }
        }
    }

    "Property 16: Sentence Order Puzzle Interaction - WordOrder preserves word order" {
        checkAll(100, Arb.list(Arb.string(1..10), 1..8)) { words ->
            val nonEmptyWords = words.filter { it.isNotBlank() }
            if (nonEmptyWords.isNotEmpty()) {
                val wordOrderInput = GameInput.WordOrder(nonEmptyWords)
                
                // Order should be preserved
                wordOrderInput.orderedWords shouldBe nonEmptyWords
                
                // Each word should be in the same position
                nonEmptyWords.forEachIndexed { index, word ->
                    wordOrderInput.orderedWords[index] shouldBe word
                }
            }
        }
    }

    "Property 16: Sentence Order Puzzle Interaction - WordOrder supports Japanese characters" {
        val japaneseWords = listOf("こんにちは", "私", "は", "学生", "です")
        val wordOrderInput = GameInput.WordOrder(japaneseWords)
        
        wordOrderInput.orderedWords shouldBe japaneseWords
        wordOrderInput.orderedWords.all { it.isNotBlank() } shouldBe true
    }

    "Property 16: Sentence Order Puzzle Interaction - WordOrder supports mixed scripts" {
        val mixedWords = listOf("Hello", "こんにちは", "カタカナ", "漢字", "123")
        val wordOrderInput = GameInput.WordOrder(mixedWords)
        
        wordOrderInput.orderedWords shouldBe mixedWords
        wordOrderInput.orderedWords.size shouldBe 5
    }
})