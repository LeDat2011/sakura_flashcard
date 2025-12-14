package com.example.sakura_flashcard.data

import com.example.sakura_flashcard.data.model.*

/**
 * Complete Japanese character data for Hiragana, Katakana, and basic Kanji
 */
object JapaneseCharacterData {
    
    private fun createSimpleStroke(order: Int): Stroke {
        return Stroke(
            order = order,
            points = listOf(Point(0f, 0f), Point(100f, 100f)),
            direction = StrokeDirection.CURVED
        )
    }
    
    // Complete Hiragana (46 basic + dakuten/handakuten)
    val hiraganaCharacters: List<Character> = listOf(
        // Vowels (あ行)
        Character(id = "h_a", character = "あ", script = CharacterScript.HIRAGANA, pronunciation = listOf("a"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("あさ (asa) - morning", "あめ (ame) - rain")),
        Character(id = "h_i", character = "い", script = CharacterScript.HIRAGANA, pronunciation = listOf("i"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("いぬ (inu) - dog", "いえ (ie) - house")),
        Character(id = "h_u", character = "う", script = CharacterScript.HIRAGANA, pronunciation = listOf("u"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("うみ (umi) - sea", "うた (uta) - song")),
        Character(id = "h_e", character = "え", script = CharacterScript.HIRAGANA, pronunciation = listOf("e"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("えき (eki) - station", "えん (en) - yen")),
        Character(id = "h_o", character = "お", script = CharacterScript.HIRAGANA, pronunciation = listOf("o"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("おか (oka) - hill", "おと (oto) - sound")),
        
        // K-row (か行)
        Character(id = "h_ka", character = "か", script = CharacterScript.HIRAGANA, pronunciation = listOf("ka"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("かみ (kami) - paper", "かお (kao) - face")),
        Character(id = "h_ki", character = "き", script = CharacterScript.HIRAGANA, pronunciation = listOf("ki"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("きく (kiku) - chrysanthemum", "きた (kita) - north")),
        Character(id = "h_ku", character = "く", script = CharacterScript.HIRAGANA, pronunciation = listOf("ku"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("くも (kumo) - cloud", "くち (kuchi) - mouth")),
        Character(id = "h_ke", character = "け", script = CharacterScript.HIRAGANA, pronunciation = listOf("ke"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("けむり (kemuri) - smoke")),
        Character(id = "h_ko", character = "こ", script = CharacterScript.HIRAGANA, pronunciation = listOf("ko"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("こえ (koe) - voice", "ここ (koko) - here")),
        
        // S-row (さ行)
        Character(id = "h_sa", character = "さ", script = CharacterScript.HIRAGANA, pronunciation = listOf("sa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("さくら (sakura) - cherry blossom")),
        Character(id = "h_shi", character = "し", script = CharacterScript.HIRAGANA, pronunciation = listOf("shi"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("しお (shio) - salt", "した (shita) - below")),
        Character(id = "h_su", character = "す", script = CharacterScript.HIRAGANA, pronunciation = listOf("su"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("すし (sushi) - sushi")),
        Character(id = "h_se", character = "せ", script = CharacterScript.HIRAGANA, pronunciation = listOf("se"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("せかい (sekai) - world")),
        Character(id = "h_so", character = "そ", script = CharacterScript.HIRAGANA, pronunciation = listOf("so"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("そら (sora) - sky", "そと (soto) - outside")),
        
        // T-row (た行)
        Character(id = "h_ta", character = "た", script = CharacterScript.HIRAGANA, pronunciation = listOf("ta"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("たべる (taberu) - to eat")),
        Character(id = "h_chi", character = "ち", script = CharacterScript.HIRAGANA, pronunciation = listOf("chi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ちず (chizu) - map", "ちち (chichi) - father")),
        Character(id = "h_tsu", character = "つ", script = CharacterScript.HIRAGANA, pronunciation = listOf("tsu"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("つき (tsuki) - moon")),
        Character(id = "h_te", character = "て", script = CharacterScript.HIRAGANA, pronunciation = listOf("te"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("て (te) - hand", "てんき (tenki) - weather")),
        Character(id = "h_to", character = "と", script = CharacterScript.HIRAGANA, pronunciation = listOf("to"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("とり (tori) - bird", "ともだち (tomodachi) - friend")),
        
        // N-row (な行)
        Character(id = "h_na", character = "な", script = CharacterScript.HIRAGANA, pronunciation = listOf("na"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("なつ (natsu) - summer", "なまえ (namae) - name")),
        Character(id = "h_ni", character = "に", script = CharacterScript.HIRAGANA, pronunciation = listOf("ni"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("にほん (nihon) - Japan")),
        Character(id = "h_nu", character = "ぬ", script = CharacterScript.HIRAGANA, pronunciation = listOf("nu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ぬの (nuno) - cloth")),
        Character(id = "h_ne", character = "ね", script = CharacterScript.HIRAGANA, pronunciation = listOf("ne"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ねこ (neko) - cat")),
        Character(id = "h_no", character = "の", script = CharacterScript.HIRAGANA, pronunciation = listOf("no"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("のみもの (nomimono) - drink")),
        
        // H-row (は行)
        Character(id = "h_ha", character = "は", script = CharacterScript.HIRAGANA, pronunciation = listOf("ha", "wa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("はな (hana) - flower", "はし (hashi) - chopsticks")),
        Character(id = "h_hi", character = "ひ", script = CharacterScript.HIRAGANA, pronunciation = listOf("hi"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ひと (hito) - person", "ひかり (hikari) - light")),
        Character(id = "h_fu", character = "ふ", script = CharacterScript.HIRAGANA, pronunciation = listOf("fu", "hu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ふゆ (fuyu) - winter")),
        Character(id = "h_he", character = "へ", script = CharacterScript.HIRAGANA, pronunciation = listOf("he", "e"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("へや (heya) - room")),
        Character(id = "h_ho", character = "ほ", script = CharacterScript.HIRAGANA, pronunciation = listOf("ho"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ほん (hon) - book", "ほし (hoshi) - star")),
        
        // M-row (ま行)
        Character(id = "h_ma", character = "ま", script = CharacterScript.HIRAGANA, pronunciation = listOf("ma"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("まち (machi) - town")),
        Character(id = "h_mi", character = "み", script = CharacterScript.HIRAGANA, pronunciation = listOf("mi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("みず (mizu) - water", "みみ (mimi) - ear")),
        Character(id = "h_mu", character = "む", script = CharacterScript.HIRAGANA, pronunciation = listOf("mu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("むし (mushi) - insect")),
        Character(id = "h_me", character = "め", script = CharacterScript.HIRAGANA, pronunciation = listOf("me"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("め (me) - eye")),
        Character(id = "h_mo", character = "も", script = CharacterScript.HIRAGANA, pronunciation = listOf("mo"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("もり (mori) - forest")),
        
        // Y-row (や行)
        Character(id = "h_ya", character = "や", script = CharacterScript.HIRAGANA, pronunciation = listOf("ya"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("やま (yama) - mountain")),
        Character(id = "h_yu", character = "ゆ", script = CharacterScript.HIRAGANA, pronunciation = listOf("yu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ゆき (yuki) - snow", "ゆめ (yume) - dream")),
        Character(id = "h_yo", character = "よ", script = CharacterScript.HIRAGANA, pronunciation = listOf("yo"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("よる (yoru) - night")),
        
        // R-row (ら行)
        Character(id = "h_ra", character = "ら", script = CharacterScript.HIRAGANA, pronunciation = listOf("ra"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("らいねん (rainen) - next year")),
        Character(id = "h_ri", character = "り", script = CharacterScript.HIRAGANA, pronunciation = listOf("ri"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("りんご (ringo) - apple")),
        Character(id = "h_ru", character = "る", script = CharacterScript.HIRAGANA, pronunciation = listOf("ru"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("るす (rusu) - absence")),
        Character(id = "h_re", character = "れ", script = CharacterScript.HIRAGANA, pronunciation = listOf("re"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("れきし (rekishi) - history")),
        Character(id = "h_ro", character = "ろ", script = CharacterScript.HIRAGANA, pronunciation = listOf("ro"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ろく (roku) - six")),
        
        // W-row (わ行)
        Character(id = "h_wa", character = "わ", script = CharacterScript.HIRAGANA, pronunciation = listOf("wa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("わたし (watashi) - I/me")),
        Character(id = "h_wo", character = "を", script = CharacterScript.HIRAGANA, pronunciation = listOf("wo", "o"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("(particle)")),
        
        // N (ん)
        Character(id = "h_n", character = "ん", script = CharacterScript.HIRAGANA, pronunciation = listOf("n"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("にほん (nihon) - Japan")),
        
        // Dakuten (が行, ざ行, だ行, ば行)
        Character(id = "h_ga", character = "が", script = CharacterScript.HIRAGANA, pronunciation = listOf("ga"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("がっこう (gakkou) - school")),
        Character(id = "h_gi", character = "ぎ", script = CharacterScript.HIRAGANA, pronunciation = listOf("gi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ぎんこう (ginkou) - bank")),
        Character(id = "h_gu", character = "ぐ", script = CharacterScript.HIRAGANA, pronunciation = listOf("gu"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ぐあい (guai) - condition")),
        Character(id = "h_ge", character = "げ", script = CharacterScript.HIRAGANA, pronunciation = listOf("ge"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("げんき (genki) - healthy")),
        Character(id = "h_go", character = "ご", script = CharacterScript.HIRAGANA, pronunciation = listOf("go"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ごご (gogo) - afternoon")),

        Character(id = "h_za", character = "ざ", script = CharacterScript.HIRAGANA, pronunciation = listOf("za"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ざっし (zasshi) - magazine")),
        Character(id = "h_ji", character = "じ", script = CharacterScript.HIRAGANA, pronunciation = listOf("ji"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("じかん (jikan) - time")),
        Character(id = "h_zu", character = "ず", script = CharacterScript.HIRAGANA, pronunciation = listOf("zu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("みず (mizu) - water")),
        Character(id = "h_ze", character = "ぜ", script = CharacterScript.HIRAGANA, pronunciation = listOf("ze"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ぜんぶ (zenbu) - all")),
        Character(id = "h_zo", character = "ぞ", script = CharacterScript.HIRAGANA, pronunciation = listOf("zo"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ぞう (zou) - elephant")),
        
        Character(id = "h_da", character = "だ", script = CharacterScript.HIRAGANA, pronunciation = listOf("da"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("だいがく (daigaku) - university")),
        Character(id = "h_di", character = "ぢ", script = CharacterScript.HIRAGANA, pronunciation = listOf("ji", "di"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("(rare)")),
        Character(id = "h_du", character = "づ", script = CharacterScript.HIRAGANA, pronunciation = listOf("zu", "du"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("つづく (tsuzuku) - to continue")),
        Character(id = "h_de", character = "で", script = CharacterScript.HIRAGANA, pronunciation = listOf("de"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("でんわ (denwa) - telephone")),
        Character(id = "h_do", character = "ど", script = CharacterScript.HIRAGANA, pronunciation = listOf("do"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("どこ (doko) - where")),
        
        Character(id = "h_ba", character = "ば", script = CharacterScript.HIRAGANA, pronunciation = listOf("ba"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ばんごはん (bangohan) - dinner")),
        Character(id = "h_bi", character = "び", script = CharacterScript.HIRAGANA, pronunciation = listOf("bi"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("びょういん (byouin) - hospital")),
        Character(id = "h_bu", character = "ぶ", script = CharacterScript.HIRAGANA, pronunciation = listOf("bu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ぶんか (bunka) - culture")),
        Character(id = "h_be", character = "べ", script = CharacterScript.HIRAGANA, pronunciation = listOf("be"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("べんきょう (benkyou) - study")),
        Character(id = "h_bo", character = "ぼ", script = CharacterScript.HIRAGANA, pronunciation = listOf("bo"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ぼく (boku) - I (male)")),
        
        // Handakuten (ぱ行)
        Character(id = "h_pa", character = "ぱ", script = CharacterScript.HIRAGANA, pronunciation = listOf("pa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ぱん (pan) - bread")),
        Character(id = "h_pi", character = "ぴ", script = CharacterScript.HIRAGANA, pronunciation = listOf("pi"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ぴかぴか (pikapika) - sparkling")),
        Character(id = "h_pu", character = "ぷ", script = CharacterScript.HIRAGANA, pronunciation = listOf("pu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ぷーる (puuru) - pool")),
        Character(id = "h_pe", character = "ぺ", script = CharacterScript.HIRAGANA, pronunciation = listOf("pe"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ぺん (pen) - pen")),
        Character(id = "h_po", character = "ぽ", script = CharacterScript.HIRAGANA, pronunciation = listOf("po"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("たんぽぽ (tanpopo) - dandelion"))
    )

    
    // Complete Katakana (46 basic + dakuten/handakuten)
    val katakanaCharacters: List<Character> = listOf(
        // Vowels (ア行)
        Character(id = "k_a", character = "ア", script = CharacterScript.KATAKANA, pronunciation = listOf("a"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("アメリカ (Amerika) - America")),
        Character(id = "k_i", character = "イ", script = CharacterScript.KATAKANA, pronunciation = listOf("i"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("イギリス (Igirisu) - England")),
        Character(id = "k_u", character = "ウ", script = CharacterScript.KATAKANA, pronunciation = listOf("u"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ウイスキー (uisukii) - whiskey")),
        Character(id = "k_e", character = "エ", script = CharacterScript.KATAKANA, pronunciation = listOf("e"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("エレベーター (erebeetaa) - elevator")),
        Character(id = "k_o", character = "オ", script = CharacterScript.KATAKANA, pronunciation = listOf("o"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("オレンジ (orenji) - orange")),
        
        // K-row (カ行)
        Character(id = "k_ka", character = "カ", script = CharacterScript.KATAKANA, pronunciation = listOf("ka"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("カメラ (kamera) - camera")),
        Character(id = "k_ki", character = "キ", script = CharacterScript.KATAKANA, pronunciation = listOf("ki"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("キロ (kiro) - kilo")),
        Character(id = "k_ku", character = "ク", script = CharacterScript.KATAKANA, pronunciation = listOf("ku"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("クラス (kurasu) - class")),
        Character(id = "k_ke", character = "ケ", script = CharacterScript.KATAKANA, pronunciation = listOf("ke"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ケーキ (keeki) - cake")),
        Character(id = "k_ko", character = "コ", script = CharacterScript.KATAKANA, pronunciation = listOf("ko"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("コーヒー (koohii) - coffee")),
        
        // S-row (サ行)
        Character(id = "k_sa", character = "サ", script = CharacterScript.KATAKANA, pronunciation = listOf("sa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("サラダ (sarada) - salad")),
        Character(id = "k_shi", character = "シ", script = CharacterScript.KATAKANA, pronunciation = listOf("shi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("シャツ (shatsu) - shirt")),
        Character(id = "k_su", character = "ス", script = CharacterScript.KATAKANA, pronunciation = listOf("su"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("スポーツ (supootsu) - sports")),
        Character(id = "k_se", character = "セ", script = CharacterScript.KATAKANA, pronunciation = listOf("se"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("セーター (seetaa) - sweater")),
        Character(id = "k_so", character = "ソ", script = CharacterScript.KATAKANA, pronunciation = listOf("so"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ソファー (sofaa) - sofa")),
        
        // T-row (タ行)
        Character(id = "k_ta", character = "タ", script = CharacterScript.KATAKANA, pronunciation = listOf("ta"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("タクシー (takushii) - taxi")),
        Character(id = "k_chi", character = "チ", script = CharacterScript.KATAKANA, pronunciation = listOf("chi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("チーズ (chiizu) - cheese")),
        Character(id = "k_tsu", character = "ツ", script = CharacterScript.KATAKANA, pronunciation = listOf("tsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ツアー (tsuaa) - tour")),
        Character(id = "k_te", character = "テ", script = CharacterScript.KATAKANA, pronunciation = listOf("te"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("テレビ (terebi) - TV")),
        Character(id = "k_to", character = "ト", script = CharacterScript.KATAKANA, pronunciation = listOf("to"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("トイレ (toire) - toilet")),
        
        // N-row (ナ行)
        Character(id = "k_na", character = "ナ", script = CharacterScript.KATAKANA, pronunciation = listOf("na"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ナイフ (naifu) - knife")),
        Character(id = "k_ni", character = "ニ", script = CharacterScript.KATAKANA, pronunciation = listOf("ni"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ニュース (nyuusu) - news")),
        Character(id = "k_nu", character = "ヌ", script = CharacterScript.KATAKANA, pronunciation = listOf("nu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ヌードル (nuudoru) - noodle")),
        Character(id = "k_ne", character = "ネ", script = CharacterScript.KATAKANA, pronunciation = listOf("ne"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ネクタイ (nekutai) - necktie")),
        Character(id = "k_no", character = "ノ", script = CharacterScript.KATAKANA, pronunciation = listOf("no"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ノート (nooto) - notebook")),
        
        // H-row (ハ行)
        Character(id = "k_ha", character = "ハ", script = CharacterScript.KATAKANA, pronunciation = listOf("ha"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ハンバーガー (hanbaagaa) - hamburger")),
        Character(id = "k_hi", character = "ヒ", script = CharacterScript.KATAKANA, pronunciation = listOf("hi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ヒーター (hiitaa) - heater")),
        Character(id = "k_fu", character = "フ", script = CharacterScript.KATAKANA, pronunciation = listOf("fu", "hu"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("フランス (Furansu) - France")),
        Character(id = "k_he", character = "ヘ", script = CharacterScript.KATAKANA, pronunciation = listOf("he"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ヘリコプター (herikoputaa) - helicopter")),
        Character(id = "k_ho", character = "ホ", script = CharacterScript.KATAKANA, pronunciation = listOf("ho"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ホテル (hoteru) - hotel")),
        
        // M-row (マ行)
        Character(id = "k_ma", character = "マ", script = CharacterScript.KATAKANA, pronunciation = listOf("ma"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("マクドナルド (Makudonarudo) - McDonald's")),
        Character(id = "k_mi", character = "ミ", script = CharacterScript.KATAKANA, pronunciation = listOf("mi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ミルク (miruku) - milk")),
        Character(id = "k_mu", character = "ム", script = CharacterScript.KATAKANA, pronunciation = listOf("mu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ムービー (muubii) - movie")),
        Character(id = "k_me", character = "メ", script = CharacterScript.KATAKANA, pronunciation = listOf("me"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("メニュー (menyuu) - menu")),
        Character(id = "k_mo", character = "モ", script = CharacterScript.KATAKANA, pronunciation = listOf("mo"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("モデル (moderu) - model")),
        
        // Y-row (ヤ行)
        Character(id = "k_ya", character = "ヤ", script = CharacterScript.KATAKANA, pronunciation = listOf("ya"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ヤフー (Yafuu) - Yahoo")),
        Character(id = "k_yu", character = "ユ", script = CharacterScript.KATAKANA, pronunciation = listOf("yu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ユーチューブ (Yuuchuubu) - YouTube")),
        Character(id = "k_yo", character = "ヨ", script = CharacterScript.KATAKANA, pronunciation = listOf("yo"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ヨーロッパ (Yooroppa) - Europe")),
        
        // R-row (ラ行)
        Character(id = "k_ra", character = "ラ", script = CharacterScript.KATAKANA, pronunciation = listOf("ra"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ラーメン (raamen) - ramen")),
        Character(id = "k_ri", character = "リ", script = CharacterScript.KATAKANA, pronunciation = listOf("ri"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("リモコン (rimokon) - remote control")),
        Character(id = "k_ru", character = "ル", script = CharacterScript.KATAKANA, pronunciation = listOf("ru"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ルール (ruuru) - rule")),
        Character(id = "k_re", character = "レ", script = CharacterScript.KATAKANA, pronunciation = listOf("re"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("レストラン (resutoran) - restaurant")),
        Character(id = "k_ro", character = "ロ", script = CharacterScript.KATAKANA, pronunciation = listOf("ro"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ロボット (robotto) - robot")),
        
        // W-row (ワ行)
        Character(id = "k_wa", character = "ワ", script = CharacterScript.KATAKANA, pronunciation = listOf("wa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ワイン (wain) - wine")),
        Character(id = "k_wo", character = "ヲ", script = CharacterScript.KATAKANA, pronunciation = listOf("wo", "o"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("(particle, rare)")),
        
        // N (ン)
        Character(id = "k_n", character = "ン", script = CharacterScript.KATAKANA, pronunciation = listOf("n"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("パン (pan) - bread")),
        
        // Dakuten
        Character(id = "k_ga", character = "ガ", script = CharacterScript.KATAKANA, pronunciation = listOf("ga"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ガソリン (gasorin) - gasoline")),
        Character(id = "k_gi", character = "ギ", script = CharacterScript.KATAKANA, pronunciation = listOf("gi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ギター (gitaa) - guitar")),
        Character(id = "k_gu", character = "グ", script = CharacterScript.KATAKANA, pronunciation = listOf("gu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("グループ (guruupu) - group")),

        Character(id = "k_ge", character = "ゲ", script = CharacterScript.KATAKANA, pronunciation = listOf("ge"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ゲーム (geemu) - game")),
        Character(id = "k_go", character = "ゴ", script = CharacterScript.KATAKANA, pronunciation = listOf("go"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ゴルフ (gorufu) - golf")),
        
        Character(id = "k_za", character = "ザ", script = CharacterScript.KATAKANA, pronunciation = listOf("za"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ピザ (piza) - pizza")),
        Character(id = "k_ji", character = "ジ", script = CharacterScript.KATAKANA, pronunciation = listOf("ji"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ジュース (juusu) - juice")),
        Character(id = "k_zu", character = "ズ", script = CharacterScript.KATAKANA, pronunciation = listOf("zu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("チーズ (chiizu) - cheese")),
        Character(id = "k_ze", character = "ゼ", script = CharacterScript.KATAKANA, pronunciation = listOf("ze"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ゼロ (zero) - zero")),
        Character(id = "k_zo", character = "ゾ", script = CharacterScript.KATAKANA, pronunciation = listOf("zo"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ゾーン (zoon) - zone")),
        
        Character(id = "k_da", character = "ダ", script = CharacterScript.KATAKANA, pronunciation = listOf("da"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("ダンス (dansu) - dance")),
        Character(id = "k_di", character = "ヂ", script = CharacterScript.KATAKANA, pronunciation = listOf("ji", "di"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("(rare)")),
        Character(id = "k_du", character = "ヅ", script = CharacterScript.KATAKANA, pronunciation = listOf("zu", "du"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("(rare)")),
        Character(id = "k_de", character = "デ", script = CharacterScript.KATAKANA, pronunciation = listOf("de"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("デパート (depaato) - department store")),
        Character(id = "k_do", character = "ド", script = CharacterScript.KATAKANA, pronunciation = listOf("do"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ドア (doa) - door")),
        
        Character(id = "k_ba", character = "バ", script = CharacterScript.KATAKANA, pronunciation = listOf("ba"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("バス (basu) - bus")),
        Character(id = "k_bi", character = "ビ", script = CharacterScript.KATAKANA, pronunciation = listOf("bi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ビール (biiru) - beer")),
        Character(id = "k_bu", character = "ブ", script = CharacterScript.KATAKANA, pronunciation = listOf("bu"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ブログ (burogu) - blog")),
        Character(id = "k_be", character = "ベ", script = CharacterScript.KATAKANA, pronunciation = listOf("be"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ベッド (beddo) - bed")),
        Character(id = "k_bo", character = "ボ", script = CharacterScript.KATAKANA, pronunciation = listOf("bo"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ボール (booru) - ball")),
        
        // Handakuten
        Character(id = "k_pa", character = "パ", script = CharacterScript.KATAKANA, pronunciation = listOf("pa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("パーティー (paatii) - party")),
        Character(id = "k_pi", character = "ピ", script = CharacterScript.KATAKANA, pronunciation = listOf("pi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("ピアノ (piano) - piano")),
        Character(id = "k_pu", character = "プ", script = CharacterScript.KATAKANA, pronunciation = listOf("pu"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("プール (puuru) - pool")),
        Character(id = "k_pe", character = "ペ", script = CharacterScript.KATAKANA, pronunciation = listOf("pe"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("ペン (pen) - pen")),
        Character(id = "k_po", character = "ポ", script = CharacterScript.KATAKANA, pronunciation = listOf("po"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("ポスト (posuto) - post"))
    )
    
    // Basic Kanji (N5 level - most common)
    val kanjiCharacters: List<Character> = listOf(
        Character(id = "kj_ichi", character = "一", script = CharacterScript.KANJI, pronunciation = listOf("ichi", "hitotsu"), strokeOrder = listOf(createSimpleStroke(1)), examples = listOf("一つ (hitotsu) - one thing", "一人 (hitori) - one person")),
        Character(id = "kj_ni", character = "二", script = CharacterScript.KANJI, pronunciation = listOf("ni", "futatsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("二つ (futatsu) - two things", "二人 (futari) - two people")),
        Character(id = "kj_san", character = "三", script = CharacterScript.KANJI, pronunciation = listOf("san", "mittsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("三つ (mittsu) - three things")),
        Character(id = "kj_yon", character = "四", script = CharacterScript.KANJI, pronunciation = listOf("shi", "yon", "yottsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4), createSimpleStroke(5)), examples = listOf("四つ (yottsu) - four things")),
        Character(id = "kj_go", character = "五", script = CharacterScript.KANJI, pronunciation = listOf("go", "itsutsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("五つ (itsutsu) - five things")),
        Character(id = "kj_roku", character = "六", script = CharacterScript.KANJI, pronunciation = listOf("roku", "muttsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("六つ (muttsu) - six things")),
        Character(id = "kj_nana", character = "七", script = CharacterScript.KANJI, pronunciation = listOf("shichi", "nana", "nanatsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("七つ (nanatsu) - seven things")),
        Character(id = "kj_hachi", character = "八", script = CharacterScript.KANJI, pronunciation = listOf("hachi", "yattsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("八つ (yattsu) - eight things")),
        Character(id = "kj_kyuu", character = "九", script = CharacterScript.KANJI, pronunciation = listOf("kyuu", "ku", "kokonotsu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("九つ (kokonotsu) - nine things")),
        Character(id = "kj_juu", character = "十", script = CharacterScript.KANJI, pronunciation = listOf("juu", "too"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("十 (juu) - ten")),
        
        Character(id = "kj_hi", character = "日", script = CharacterScript.KANJI, pronunciation = listOf("nichi", "hi", "bi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("日本 (nihon) - Japan", "今日 (kyou) - today")),
        Character(id = "kj_tsuki", character = "月", script = CharacterScript.KANJI, pronunciation = listOf("getsu", "gatsu", "tsuki"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("月曜日 (getsuyoubi) - Monday")),
        Character(id = "kj_hi2", character = "火", script = CharacterScript.KANJI, pronunciation = listOf("ka", "hi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("火曜日 (kayoubi) - Tuesday")),
        Character(id = "kj_mizu", character = "水", script = CharacterScript.KANJI, pronunciation = listOf("sui", "mizu"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("水曜日 (suiyoubi) - Wednesday", "水 (mizu) - water")),
        Character(id = "kj_ki", character = "木", script = CharacterScript.KANJI, pronunciation = listOf("moku", "ki"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("木曜日 (mokuyoubi) - Thursday", "木 (ki) - tree")),
        Character(id = "kj_kin", character = "金", script = CharacterScript.KANJI, pronunciation = listOf("kin", "kane"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4), createSimpleStroke(5), createSimpleStroke(6), createSimpleStroke(7), createSimpleStroke(8)), examples = listOf("金曜日 (kinyoubi) - Friday", "お金 (okane) - money")),
        Character(id = "kj_tsuchi", character = "土", script = CharacterScript.KANJI, pronunciation = listOf("do", "tsuchi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("土曜日 (doyoubi) - Saturday")),
        
        Character(id = "kj_hito", character = "人", script = CharacterScript.KANJI, pronunciation = listOf("jin", "nin", "hito"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2)), examples = listOf("日本人 (nihonjin) - Japanese person", "人 (hito) - person")),
        Character(id = "kj_yama", character = "山", script = CharacterScript.KANJI, pronunciation = listOf("san", "yama"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("山 (yama) - mountain", "富士山 (fujisan) - Mt. Fuji")),
        Character(id = "kj_kawa", character = "川", script = CharacterScript.KANJI, pronunciation = listOf("sen", "kawa"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("川 (kawa) - river")),
        
        Character(id = "kj_dai", character = "大", script = CharacterScript.KANJI, pronunciation = listOf("dai", "tai", "ookii"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("大きい (ookii) - big", "大学 (daigaku) - university")),
        Character(id = "kj_shou", character = "小", script = CharacterScript.KANJI, pronunciation = listOf("shou", "chiisai", "ko"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("小さい (chiisai) - small")),
        Character(id = "kj_naka", character = "中", script = CharacterScript.KANJI, pronunciation = listOf("chuu", "naka"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4)), examples = listOf("中国 (chuugoku) - China", "中 (naka) - inside")),
        
        Character(id = "kj_ue", character = "上", script = CharacterScript.KANJI, pronunciation = listOf("jou", "ue"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("上 (ue) - above/up")),
        Character(id = "kj_shita", character = "下", script = CharacterScript.KANJI, pronunciation = listOf("ka", "ge", "shita"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("下 (shita) - below/down")),
        Character(id = "kj_hidari", character = "左", script = CharacterScript.KANJI, pronunciation = listOf("sa", "hidari"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4), createSimpleStroke(5)), examples = listOf("左 (hidari) - left")),
        Character(id = "kj_migi", character = "右", script = CharacterScript.KANJI, pronunciation = listOf("u", "migi"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4), createSimpleStroke(5)), examples = listOf("右 (migi) - right")),
        
        Character(id = "kj_otoko", character = "男", script = CharacterScript.KANJI, pronunciation = listOf("dan", "nan", "otoko"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3), createSimpleStroke(4), createSimpleStroke(5), createSimpleStroke(6), createSimpleStroke(7)), examples = listOf("男の人 (otoko no hito) - man")),
        Character(id = "kj_onna", character = "女", script = CharacterScript.KANJI, pronunciation = listOf("jo", "onna"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("女の人 (onna no hito) - woman")),
        Character(id = "kj_ko", character = "子", script = CharacterScript.KANJI, pronunciation = listOf("shi", "ko"), strokeOrder = listOf(createSimpleStroke(1), createSimpleStroke(2), createSimpleStroke(3)), examples = listOf("子供 (kodomo) - child"))
    )
}