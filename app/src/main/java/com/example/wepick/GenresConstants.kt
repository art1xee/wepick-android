package com.example.wepick


object GenresData  {
    val GENRES = mapOf(
        "ua" to listOf("Бойовик", "Пригоди", "Комедія", "Драма", "Романтика", "Фентезі", "Наукова фантастика", "Містика / Детектив", "Жахи", "Трилер"),
        "ru" to listOf("Боевик", "Приключения", "Комедия", "Драма", "Романтика", "Фэнтези", "Научная фантастика", "Мистика / Детектив", "Ужасы", "Триллер"),
        "en" to listOf("Action", "Adventure", "Comedy", "Drama", "Romance", "Fantasy", "Sci-Fi", "Mystery", "Horror", "Thriller")
    )
    val DECADES = listOf(1920, 1930, 1940, 1950, 1960, 1970, 1980, 1990, 2000, 2010, 2020)
}