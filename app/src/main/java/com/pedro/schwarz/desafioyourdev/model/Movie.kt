package com.pedro.schwarz.desafioyourdev.model

data class Movie(
    val display_title: String,
    val mpaa_rating: String,
    val critics_pick: Int,
    val byline: String,
    val headline: String,
    val summary_short: String,
    val publication_date: String,
    val opening_date: String?,
    val date_updated: String,
    val src: String,
    val linkUrl: String,
)