package com.pedro.schwarz.desafioyourdev.model

class MovieResponse(
    val status: String,
    val copyright: String,
    val has_more: Boolean,
    val num_results: Int,
    val results: List<Movie>,
)