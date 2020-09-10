package com.pedro.schwarz.desafioyourdev.repository

abstract class Resource<T>(val data: T? = null, val error: String? = null)

class Success<T>(data: T?) : Resource<T>(data = data)

class Failure<T>(error: String?) : Resource<T>(error = error)