package com.rfid.util

import android.content.Context
import android.content.SharedPreferences
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.rfid.data.remote.project.Manufacture
import com.rfid.data.remote.project.Project
import com.rfid.data.remote.project.UserId

object SharedPreferencesPackage {
    fun setAutoLogin(userId: String, userPassword: String, isAuto: Boolean) {
        val preferences = context.getSharedPreferences("base", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(Constants.USER_ID, userId)
        editor.putString(Constants.USER_PW, userPassword)
        editor.putBoolean(Constants.IS_AUTO, isAuto)
        editor.apply()
    }
    fun getUserId(): String {
        val preferences: SharedPreferences =
            context.getSharedPreferences("base", Context.MODE_PRIVATE)
        return preferences.getString(Constants.USER_ID, "").toString()
    }
    fun getUserPassword(): String {
        val preferences: SharedPreferences =
            context.getSharedPreferences("base", Context.MODE_PRIVATE)
        return preferences.getString(Constants.USER_PW, "").toString()
    }
    fun getAutoLogin(): Boolean {
        val preferences: SharedPreferences =
            context.getSharedPreferences("base", Context.MODE_PRIVATE)
        return preferences.getBoolean(Constants.IS_AUTO, false)
    }

    fun setToken(token: String) {
        val preferences = context.getSharedPreferences("base", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(Constants.TOKEN, token)
        editor.apply()
    }
    fun getToken(): String {
        val preferences: SharedPreferences =
            context.getSharedPreferences("base", Context.MODE_PRIVATE)
        return preferences.getString(Constants.TOKEN, "").toString()
    }

    fun setProject(id: String, name: String) {
        val preferences = context.getSharedPreferences("base", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(Constants.PROJECT_ID, id)
        editor.putString(Constants.PROJECT_NAME, name)
        editor.apply()
    }

    fun setManufacture(id: String, name: String) {
        val preferences = context.getSharedPreferences("base", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(Constants.MANUFACTURE_ID, id)
        editor.putString(Constants.MANUFACTURE_NAME, name)
        editor.apply()
    }

    fun getProject(): Project {
        val preferences: SharedPreferences = context.getSharedPreferences("base", Context.MODE_PRIVATE)
        val id = preferences.getString(Constants.PROJECT_ID, "").toString()
        val name = preferences.getString(Constants.PROJECT_NAME, "").toString()
        return Project(id, name)
    }

    fun getManufacture(): Manufacture {
        val preferences: SharedPreferences = context.getSharedPreferences("base", Context.MODE_PRIVATE)
        val id = preferences.getString(Constants.MANUFACTURE_ID, "").toString()
        val name = preferences.getString(Constants.MANUFACTURE_NAME, "").toString()
        return Manufacture(id, name)
    }
}