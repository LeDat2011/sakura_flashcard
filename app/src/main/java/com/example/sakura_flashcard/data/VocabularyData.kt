package com.example.sakura_flashcard.data

import com.example.sakura_flashcard.data.model.*

object VocabularyData {
    
    // Food vocabulary - N5
    val foodN5 = listOf(
        createFlashcard("food_n5_1", "ごはん", "gohan", "Rice / Meal", "Basic word for cooked rice or meal", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_2", "パン", "pan", "Bread", "From Portuguese 'pão'", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_3", "みず", "mizu", "Water", "Basic vocabulary", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_4", "おちゃ", "ocha", "Tea", "Green tea is very popular in Japan", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_5", "さかな", "sakana", "Fish", "Japan is famous for seafood", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_6", "にく", "niku", "Meat", "General word for meat", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_7", "やさい", "yasai", "Vegetables", "Healthy food category", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_8", "くだもの", "kudamono", "Fruit", "Sweet and healthy", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_9", "たまご", "tamago", "Egg", "Used in many Japanese dishes", VocabularyTopic.FOOD, JLPTLevel.N5),
        createFlashcard("food_n5_10", "ぎゅうにゅう", "gyuunyuu", "Milk", "Cow's milk", VocabularyTopic.FOOD, JLPTLevel.N5)
    )
    
    // Animals vocabulary - N5
    val animalsN5 = listOf(
        createFlashcard("animal_n5_1", "いぬ", "inu", "Dog", "Man's best friend", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_2", "ねこ", "neko", "Cat", "Popular pet in Japan", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_3", "とり", "tori", "Bird", "Flying animal", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_4", "さかな", "sakana", "Fish", "Lives in water", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_5", "うま", "uma", "Horse", "Large animal", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_6", "うし", "ushi", "Cow", "Farm animal", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_7", "ぶた", "buta", "Pig", "Farm animal", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_8", "ひつじ", "hitsuji", "Sheep", "Gives wool", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_9", "うさぎ", "usagi", "Rabbit", "Cute animal with long ears", VocabularyTopic.ANIMALS, JLPTLevel.N5),
        createFlashcard("animal_n5_10", "ぞう", "zou", "Elephant", "Large animal with trunk", VocabularyTopic.ANIMALS, JLPTLevel.N5)
    )

    // Family vocabulary - N5
    val familyN5 = listOf(
        createFlashcard("family_n5_1", "おかあさん", "okaasan", "Mother", "Polite form", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_2", "おとうさん", "otousan", "Father", "Polite form", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_3", "あに", "ani", "Older brother", "My older brother", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_4", "あね", "ane", "Older sister", "My older sister", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_5", "おとうと", "otouto", "Younger brother", "My younger brother", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_6", "いもうと", "imouto", "Younger sister", "My younger sister", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_7", "おじいさん", "ojiisan", "Grandfather", "Polite form", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_8", "おばあさん", "obaasan", "Grandmother", "Polite form", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_9", "かぞく", "kazoku", "Family", "General term", VocabularyTopic.FAMILY, JLPTLevel.N5),
        createFlashcard("family_n5_10", "こども", "kodomo", "Child/Children", "Young person", VocabularyTopic.FAMILY, JLPTLevel.N5)
    )

    // Daily Life vocabulary - N5
    val dailyLifeN5 = listOf(
        createFlashcard("daily_n5_1", "あさ", "asa", "Morning", "Start of day", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_2", "ひる", "hiru", "Noon/Daytime", "Middle of day", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_3", "よる", "yoru", "Night", "End of day", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_4", "いえ", "ie", "House/Home", "Where you live", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_5", "へや", "heya", "Room", "Part of house", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_6", "でんわ", "denwa", "Telephone", "Communication device", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_7", "テレビ", "terebi", "Television", "Entertainment device", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_8", "しごと", "shigoto", "Work/Job", "Daily activity", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_9", "やすみ", "yasumi", "Rest/Holiday", "Time off", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5),
        createFlashcard("daily_n5_10", "ねる", "neru", "To sleep", "Night activity", VocabularyTopic.DAILY_LIFE, JLPTLevel.N5)
    )

    // School vocabulary - N5
    val schoolN5 = listOf(
        createFlashcard("school_n5_1", "がっこう", "gakkou", "School", "Place of learning", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_2", "せんせい", "sensei", "Teacher", "Person who teaches", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_3", "がくせい", "gakusei", "Student", "Person who learns", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_4", "ほん", "hon", "Book", "Reading material", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_5", "えんぴつ", "enpitsu", "Pencil", "Writing tool", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_6", "ノート", "nooto", "Notebook", "For writing notes", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_7", "きょうしつ", "kyoushitsu", "Classroom", "Room for learning", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_8", "しゅくだい", "shukudai", "Homework", "Work to do at home", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_9", "テスト", "tesuto", "Test", "Examination", VocabularyTopic.SCHOOL, JLPTLevel.N5),
        createFlashcard("school_n5_10", "べんきょう", "benkyou", "Study", "Learning activity", VocabularyTopic.SCHOOL, JLPTLevel.N5)
    )

    // Common Expressions - N5
    val expressionsN5 = listOf(
        createFlashcard("expr_n5_1", "こんにちは", "konnichiwa", "Hello", "Daytime greeting", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_2", "おはよう", "ohayou", "Good morning", "Morning greeting", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_3", "こんばんは", "konbanwa", "Good evening", "Evening greeting", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_4", "さようなら", "sayounara", "Goodbye", "Farewell", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_5", "ありがとう", "arigatou", "Thank you", "Expression of gratitude", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_6", "すみません", "sumimasen", "Excuse me/Sorry", "Apology or attention", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_7", "はい", "hai", "Yes", "Affirmative", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_8", "いいえ", "iie", "No", "Negative", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_9", "おねがいします", "onegaishimasu", "Please", "Polite request", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5),
        createFlashcard("expr_n5_10", "いただきます", "itadakimasu", "Let's eat", "Said before eating", VocabularyTopic.COMMON_EXPRESSIONS, JLPTLevel.N5)
    )

    // Numbers - N5
    val numbersN5 = listOf(
        createFlashcard("num_n5_1", "いち", "ichi", "One (1)", "Number 1", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_2", "に", "ni", "Two (2)", "Number 2", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_3", "さん", "san", "Three (3)", "Number 3", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_4", "よん/し", "yon/shi", "Four (4)", "Number 4", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_5", "ご", "go", "Five (5)", "Number 5", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_6", "ろく", "roku", "Six (6)", "Number 6", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_7", "なな/しち", "nana/shichi", "Seven (7)", "Number 7", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_8", "はち", "hachi", "Eight (8)", "Number 8", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_9", "きゅう/く", "kyuu/ku", "Nine (9)", "Number 9", VocabularyTopic.NUMBERS, JLPTLevel.N5),
        createFlashcard("num_n5_10", "じゅう", "juu", "Ten (10)", "Number 10", VocabularyTopic.NUMBERS, JLPTLevel.N5)
    )

    // Colors - N5
    val colorsN5 = listOf(
        createFlashcard("color_n5_1", "あか", "aka", "Red", "Color of fire", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_2", "あお", "ao", "Blue", "Color of sky", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_3", "きいろ", "kiiro", "Yellow", "Color of sun", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_4", "みどり", "midori", "Green", "Color of grass", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_5", "しろ", "shiro", "White", "Color of snow", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_6", "くろ", "kuro", "Black", "Color of night", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_7", "ちゃいろ", "chairo", "Brown", "Color of earth", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_8", "オレンジ", "orenji", "Orange", "Color of orange fruit", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_9", "ピンク", "pinku", "Pink", "Light red color", VocabularyTopic.COLORS, JLPTLevel.N5),
        createFlashcard("color_n5_10", "むらさき", "murasaki", "Purple", "Royal color", VocabularyTopic.COLORS, JLPTLevel.N5)
    )

    // Body Parts - N5
    val bodyPartsN5 = listOf(
        createFlashcard("body_n5_1", "あたま", "atama", "Head", "Top of body", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_2", "め", "me", "Eye", "For seeing", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_3", "みみ", "mimi", "Ear", "For hearing", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_4", "はな", "hana", "Nose", "For smelling", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_5", "くち", "kuchi", "Mouth", "For eating and speaking", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_6", "て", "te", "Hand", "For holding", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_7", "あし", "ashi", "Leg/Foot", "For walking", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_8", "からだ", "karada", "Body", "Whole body", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_9", "かお", "kao", "Face", "Front of head", VocabularyTopic.BODY_PARTS, JLPTLevel.N5),
        createFlashcard("body_n5_10", "ゆび", "yubi", "Finger", "Part of hand", VocabularyTopic.BODY_PARTS, JLPTLevel.N5)
    )

    // Weather - N5
    val weatherN5 = listOf(
        createFlashcard("weather_n5_1", "てんき", "tenki", "Weather", "General term", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_2", "あめ", "ame", "Rain", "Water from sky", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_3", "ゆき", "yuki", "Snow", "Frozen precipitation", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_4", "くもり", "kumori", "Cloudy", "Overcast sky", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_5", "はれ", "hare", "Sunny/Clear", "Good weather", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_6", "かぜ", "kaze", "Wind", "Moving air", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_7", "あつい", "atsui", "Hot", "High temperature", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_8", "さむい", "samui", "Cold", "Low temperature", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_9", "すずしい", "suzushii", "Cool", "Pleasant temperature", VocabularyTopic.WEATHER, JLPTLevel.N5),
        createFlashcard("weather_n5_10", "あたたかい", "atatakai", "Warm", "Comfortable temperature", VocabularyTopic.WEATHER, JLPTLevel.N5)
    )

    // Travel - N5
    val travelN5 = listOf(
        createFlashcard("travel_n5_1", "えき", "eki", "Station", "Train station", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_2", "でんしゃ", "densha", "Train", "Rail transport", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_3", "バス", "basu", "Bus", "Road transport", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_4", "ひこうき", "hikouki", "Airplane", "Air transport", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_5", "くるま", "kuruma", "Car", "Personal vehicle", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_6", "じてんしゃ", "jitensha", "Bicycle", "Two-wheeled vehicle", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_7", "みち", "michi", "Road/Street", "Path for travel", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_8", "ホテル", "hoteru", "Hotel", "Place to stay", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_9", "きっぷ", "kippu", "Ticket", "For transportation", VocabularyTopic.TRAVEL, JLPTLevel.N5),
        createFlashcard("travel_n5_10", "りょこう", "ryokou", "Travel/Trip", "Journey", VocabularyTopic.TRAVEL, JLPTLevel.N5)
    )

    // Anime vocabulary - N5
    val animeN5 = listOf(
        createFlashcard("anime_n5_1", "アニメ", "anime", "Anime", "Japanese animation", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_2", "マンガ", "manga", "Manga", "Japanese comics", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_3", "かわいい", "kawaii", "Cute", "Common expression", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_4", "すごい", "sugoi", "Amazing", "Expression of awe", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_5", "かっこいい", "kakkoii", "Cool", "Stylish/handsome", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_6", "なかま", "nakama", "Friend/Companion", "Close friend", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_7", "ゆめ", "yume", "Dream", "Aspiration", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_8", "ちから", "chikara", "Power/Strength", "Ability", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_9", "たたかう", "tatakau", "To fight", "Battle", VocabularyTopic.ANIME, JLPTLevel.N5),
        createFlashcard("anime_n5_10", "まもる", "mamoru", "To protect", "Defend", VocabularyTopic.ANIME, JLPTLevel.N5)
    )

    // Technology - N5
    val technologyN5 = listOf(
        createFlashcard("tech_n5_1", "コンピューター", "konpyuutaa", "Computer", "Electronic device", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_2", "けいたい", "keitai", "Mobile phone", "Portable phone", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_3", "インターネット", "intaanetto", "Internet", "World wide web", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_4", "メール", "meeru", "Email", "Electronic mail", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_5", "カメラ", "kamera", "Camera", "For taking photos", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_6", "ゲーム", "geemu", "Game", "Video game", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_7", "でんき", "denki", "Electricity", "Power source", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_8", "ラジオ", "rajio", "Radio", "Audio device", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_9", "とけい", "tokei", "Clock/Watch", "Time device", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5),
        createFlashcard("tech_n5_10", "エアコン", "eakon", "Air conditioner", "Cooling device", VocabularyTopic.TECHNOLOGY, JLPTLevel.N5)
    )

    // Clothes - N5
    val clothesN5 = listOf(
        createFlashcard("clothes_n5_1", "ふく", "fuku", "Clothes", "General term", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_2", "シャツ", "shatsu", "Shirt", "Upper body wear", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_3", "ズボン", "zubon", "Pants", "Lower body wear", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_4", "スカート", "sukaato", "Skirt", "Women's wear", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_5", "くつ", "kutsu", "Shoes", "Footwear", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_6", "ぼうし", "boushi", "Hat/Cap", "Head wear", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_7", "コート", "kooto", "Coat", "Outer wear", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_8", "めがね", "megane", "Glasses", "Eye wear", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_9", "かばん", "kaban", "Bag", "Carrying item", VocabularyTopic.CLOTHES, JLPTLevel.N5),
        createFlashcard("clothes_n5_10", "くつした", "kutsushita", "Socks", "Foot wear", VocabularyTopic.CLOTHES, JLPTLevel.N5)
    )

    // Helper function to create flashcard
    private fun createFlashcard(
        id: String,
        japanese: String,
        romaji: String,
        meaning: String,
        explanation: String,
        topic: VocabularyTopic,
        level: JLPTLevel
    ): Flashcard {
        return Flashcard(
            id = id,
            front = FlashcardSide(
                text = japanese,
                translation = romaji
            ),
            back = FlashcardSide(
                text = meaning,
                explanation = explanation
            ),
            topic = topic,
            level = level,
            difficulty = 0.5f,
            isCustom = false,
            createdBy = "system"
        )
    }

    // Get flashcards by topic and level
    fun getFlashcards(topic: VocabularyTopic, level: JLPTLevel): List<Flashcard> {
        // Currently only N5 data available
        if (level != JLPTLevel.N5) {
            return emptyList()
        }
        
        return when (topic) {
            VocabularyTopic.FOOD -> foodN5
            VocabularyTopic.ANIMALS -> animalsN5
            VocabularyTopic.FAMILY -> familyN5
            VocabularyTopic.DAILY_LIFE -> dailyLifeN5
            VocabularyTopic.SCHOOL -> schoolN5
            VocabularyTopic.COMMON_EXPRESSIONS -> expressionsN5
            VocabularyTopic.NUMBERS -> numbersN5
            VocabularyTopic.COLORS -> colorsN5
            VocabularyTopic.BODY_PARTS -> bodyPartsN5
            VocabularyTopic.WEATHER -> weatherN5
            VocabularyTopic.TRAVEL -> travelN5
            VocabularyTopic.ANIME -> animeN5
            VocabularyTopic.TECHNOLOGY -> technologyN5
            VocabularyTopic.CLOTHES -> clothesN5
        }
    }

    // Get all N5 flashcards
    fun getAllN5Flashcards(): List<Flashcard> {
        return foodN5 + animalsN5 + familyN5 + dailyLifeN5 + schoolN5 + 
               expressionsN5 + numbersN5 + colorsN5 + bodyPartsN5 + 
               weatherN5 + travelN5 + animeN5 + technologyN5 + clothesN5
    }
}
