package com.github.agejevasv.evovox


import android.app.Application
import com.github.agejevasv.evovox.dagger.AppComponent
import com.github.agejevasv.evovox.dagger.AppModule
import com.github.agejevasv.evovox.dagger.DaggerAppComponent

class EvovoxApplication : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }

    private fun initDagger(app: EvovoxApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()
}