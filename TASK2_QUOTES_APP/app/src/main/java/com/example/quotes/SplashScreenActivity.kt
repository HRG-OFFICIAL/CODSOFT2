package com.example.quotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.quotes.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private var keepSplashOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize splash screen
        val splashScreen = installSplashScreen()

        // Keep splash screen until animation starts
        splashScreen.setKeepOnScreenCondition { keepSplashOn }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start animation
        binding.splashImage.apply {
            scaleX = 0.8f
            scaleY = 0.8f
            alpha = 0f
            animate()
                .setDuration(1000)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .withEndAction {
                    // Animation complete - release splash screen
                    keepSplashOn = false

                    // Optional: add slight delay or fade-out before transition
                    postDelayed({
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                        finish() // Prevent back navigation to splash
                    }, 300)
                }
                .start()
        }
    }

    override fun onPause() {
        super.onPause()
        // Cancel animation to avoid memory leaks
        binding.splashImage.animate().cancel()
    }
}
