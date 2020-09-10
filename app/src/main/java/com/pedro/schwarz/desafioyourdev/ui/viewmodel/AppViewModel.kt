package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    private val _components = MutableLiveData<Components>().also { it.value = Components() }
    val components: LiveData<Components>
        get() = _components

    var setComponents: Components = Components()
        set(value) {
            field = value
            _components.value = value
        }
}

class Components(val appBar: Boolean = false, val bottomBar: Boolean = false)

