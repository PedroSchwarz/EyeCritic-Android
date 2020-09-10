package com.pedro.schwarz.desafioyourdev.repository

abstract class Resource<T>(val data: T? = null, val error: String? = null)

class Success<T>(data: T? = null) : Resource<T>(data = data)

class Failure<T>(error: String? = null) : Resource<T>(error = error)