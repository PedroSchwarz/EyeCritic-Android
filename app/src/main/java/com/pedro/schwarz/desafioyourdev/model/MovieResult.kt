package com.pedro.schwarz.desafioyourdev.model

class MovieResult(
    private val display_title: String,
    private val mpaa_rating: String,
    private val critics_pick: Int,
    private val byline: String,
    private val headline: String,
    private val summary_short: String,
    private val publication_date: String,
    private val opening_date: String,
    private val date_updated: String,
    private val multimedia: Multimedia?,
    private val link: Link
) {
    fun toMovie(): Movie {
        return Movie(
            display_title = display_title,
            mpaa_rating = mpaa_rating,
            critics_pick = critics_pick,
            byline = byline,
            headline = headline,
            summary_short = summary_short,
            publication_date = publication_date,
            opening_date = opening_date,
            date_updated = date_updated,
            src = multimedia?.src ?: "",
            linkUrl = link.url
        )
    }
}