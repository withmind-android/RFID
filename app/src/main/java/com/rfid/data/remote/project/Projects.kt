package com.rfid.data.remote.project

data class Projects(
    val data: ArrayList<Project>
)

data class Project(
    val id: String,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}