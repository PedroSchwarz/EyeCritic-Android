package com.pedro.schwarz.desafioyourdev.model

class MovieResult(
    private val abstract: String,
    private val lead_paragraph: String,
    private val source: String,
    private val pub_date: String,
    private val multimedia: List<Multimedia>,
    private val byline: Byline
) {
    fun toMovie(): Movie {
        return Movie(
            display_title = abstract,
            mpaa_rating = "0",
            byline = byline.original,
            headline = source,
            summary_short = lead_paragraph,
            publication_date = pub_date,
            src = multimedia[0].url,
        )
    }
}