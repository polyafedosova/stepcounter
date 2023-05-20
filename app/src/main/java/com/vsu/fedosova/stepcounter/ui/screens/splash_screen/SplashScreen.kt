package com.vsu.fedosova.stepcounter.ui.screens.splash_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.vsu.fedosova.stepcounter.R
import com.vsu.fedosova.stepcounter.ui.screens.activity_main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        val animatedImageView: ImageView = findViewById(R.id.imageView)

        Glide.with(this)
            .asGif()
            .load(R.drawable.running_shoes)
            .into(animatedImageView)

        lifecycleScope.launch {
            delay(5000)
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
    }
}