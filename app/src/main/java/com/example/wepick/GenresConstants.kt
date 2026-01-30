package com.example.wepick


object GenresData  {
    val GENRES = mapOf(
        "ua" to listOf("Бойовик", "Пригоди", "Комедія", "Драма", "Романтика", "Фентезі", "Наукова фантастика", "Містика / Детектив", "Жахи", "Трилер","Повсякденність","Спорт","Надприродне","Історичний","Військовий","Кримінал"),
        "ru" to listOf("Боевик", "Приключения", "Комедия", "Драма", "Романтика", "Фэнтези", "Научная фантастика", "Мистика / Детектив", "Ужасы", "Триллер","Повседневность", "Спорт", "Сверхъестественное", "Исторический", "Военный","Криминал"),
        "en" to listOf("Action", "Adventure", "Comedy", "Drama", "Romance", "Fantasy", "Sci-Fi", "Mystery", "Horror", "Thriller", "Slice of Life", "Sports","Supernatural","Historical","War","Crime")
    )

    val tmdbGenreIds = mapOf(
        "Action" to 28, "Adventure" to 12, "Comedy" to 35, "Drama" to 18,
        "Romance" to 10749, "Fantasy" to 14, "Sci-Fi" to 878, "Mystery" to 9648,
        "Horror" to 27, "Thriller" to 53, "Slice of Life" to 10751, // Для TMDB это Family/Daily
        "Sports" to 10770, "Supernatural" to 14, "Historical" to 36, "War" to 10752, "Crime" to 80
    )

    val jikanGenreIds = mapOf(
        "Action" to 1, "Adventure" to 2, "Comedy" to 4, "Drama" to 8,
        "Romance" to 22, "Fantasy" to 10, "Sci-Fi" to 24, "Mystery" to 7,
        "Horror" to 14, "Thriller" to 41, "Slice of Life" to 36,
        "Sports" to 30, "Supernatural" to 37, "Historical" to 13,
        "War" to 38, "Crime" to 50
    )
}