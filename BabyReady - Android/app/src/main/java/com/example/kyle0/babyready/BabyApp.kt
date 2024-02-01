package com.example.kyle0.babyready

import android.app.Application
import android.content.Context

class BabyApp : Application() {
    companion object {
        private lateinit var instance : BabyApp
        val context: Context
            get() = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}