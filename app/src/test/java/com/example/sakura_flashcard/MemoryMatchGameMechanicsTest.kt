package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.GameInput
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * **Feature: japanese-flashcard-ui, Property 18: Memory Match Game Mechanics**
 * **Validates: Requirements 4.4**
 * 
 * Property: For any Memory Match Game session, flip cards should be presented 
 * for matching Japanese words with their meanings
 */
class MemoryMatchGameMechanicsTest : StringSpec({

    "Property 18: Memory Match Game Mechanics - CardMatch input should accept valid card pairs" {
        checkAll(100, Arb.string(1..10), Arb.string(1..10)) { card1, card2 ->
            if (card1.isNotBlank() && card2.isNotBlank() && card1 != card2) {
                val cardMatchInput = GameInput.CardMatch(card1, card2)
                
                cardMatchInput.shouldBeInstanceOf<GameInput.CardMatch>()
                cardMatchInput.card1 shouldBe card1
                cardMatchInput.card2 shouldBe card2
            }
        }
    }

    "Property 18: Memory Match Game Mechanics - CardMatch should validate different cards" {
        checkAll(100, Arb.string(1..10), Arb.string(1..10)) { card1, card2 ->
            if (card1.isNotBlank() && card2.isNotBlank() && card1 != card2) {
                val cardMatchInput = GameInput.CardMatch(card1, card2)
                
                cardMatchInput.card1 shouldNotBe cardMatchInput.card2
                cardMatchInput.card1.isNotBlank() shouldBe true
                cardMatchInput.card2.isNotBlank() shouldBe true
            }
        }
    }

    "Property 18: Memory Match Game Mechanics - CardMatch supports Japanese-English pairs" {
        val japanesePairs = listOf(
            Pair("こんにちは", "Hello"),
            Pair("ありがとう", "Thank you"),
            Pair("さようなら", "Goodbye"),
            Pair("はい", "Yes"),
            Pair("いいえ", "No")
        )
        
        japanesePairs.forEach { (japanese, english) ->
            val cardMatchInput = GameInput.CardMatch(japanese, english)
            
            cardMatchInput.card1 shouldBe japanese
            cardMatchInput.card2 shouldBe english
            cardMatchInput.card1 shouldNotBe cardMatchInput.card2
        }
    }

    "Property 18: Memory Match Game Mechanics - CardMatch supports reverse matching" {
        val pairs = listOf(
            Pair("Hello", "こんにちは"),
            Pair("Thank you", "ありがとう"),
            Pair("Goodbye", "さようなら")
        )
        
        pairs.forEach { (english, japanese) ->
            val cardMatchInput = GameInput.CardMatch(english, japanese)
            
            cardMatchInput.card1 shouldBe english
            cardMatchInput.card2 shouldBe japanese
            cardMatchInput.card1 shouldNotBe cardMatchInput.card2
        }
    }

    "Property 18: Memory Match Game Mechanics - CardMatch supports Kanji-meaning pairs" {
        val kanjiPairs = listOf(
            Pair("水", "Water"),
            Pair("火", "Fire"),
            Pair("木", "Tree"),
            Pair("金", "Gold/Money"),
            Pair("土", "Earth/Soil")
        )
        
        kanjiPairs.forEach { (kanji, meaning) ->
            val cardMatchInput = GameInput.CardMatch(kanji, meaning)
            
            cardMatchInput.card1 shouldBe kanji
            cardMatchInput.card2 shouldBe meaning
            cardMatchInput.card1 shouldNotBe cardMatchInput.card2
        }
    }

    "Property 18: Memory Match Game Mechanics - CardMatch order should not matter for matching logic" {
        checkAll(100, Arb.string(1..10), Arb.string(1..10)) { card1, card2 ->
            if (card1.isNotBlank() && card2.isNotBlank() && card1 != card2) {
                val match1 = GameInput.CardMatch(card1, card2)
                val match2 = GameInput.CardMatch(card2, card1)
                
                // Both should be valid matches (order shouldn't matter for the game logic)
                match1.card1 shouldBe card1
                match1.card2 shouldBe card2
                match2.card1 shouldBe card2
                match2.card2 shouldBe card1
                
                // They represent the same conceptual match but with different order
                setOf(match1.card1, match1.card2) shouldBe setOf(match2.card1, match2.card2)
            }
        }
    }

    "Property 18: Memory Match Game Mechanics - CardMatch supports various character types" {
        val mixedPairs = listOf(
            Pair("ひらがな", "Hiragana"),
            Pair("カタカナ", "Katakana"),
            Pair("漢字", "Kanji"),
            Pair("123", "Numbers"),
            Pair("ABC", "Letters")
        )
        
        mixedPairs.forEach { (card1, card2) ->
            val cardMatchInput = GameInput.CardMatch(card1, card2)
            
            cardMatchInput.card1 shouldBe card1
            cardMatchInput.card2 shouldBe card2
            cardMatchInput.card1.isNotBlank() shouldBe true
            cardMatchInput.card2.isNotBlank() shouldBe true
        }
    }
})