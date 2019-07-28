package id.hipe.sampleapp.data.entity

import id.hipe.sampleapp.domain.model.Main;

data class MainEntity(private val id: Int) {
    fun toMain() = Main(id)
}
