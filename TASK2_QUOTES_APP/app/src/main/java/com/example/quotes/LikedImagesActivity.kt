package com.example.quotes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.os.VibrationEffect
import android.widget.TextView

class LikedImagesActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var homeButton: Button
    private lateinit var webButton: ImageButton
    private lateinit var instagramButton: ImageButton
    private lateinit var linkedinButton: ImageButton
    private lateinit var githubButton: ImageButton
    private lateinit var vibrator: Vibrator
    private lateinit var quoteTextView: TextView
    private lateinit var authorTextView: TextView


    private var imageIndex = 0
    private var likedImages = emptyList<Int>()
    private val placeholderImageResId = R.drawable.nothing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_liked_images)

        initializeViews()
        setupListeners()

        val receivedArray = intent.getIntArrayExtra("likedImages")
        likedImages = receivedArray?.toList() ?: emptyList()

        if (likedImages.isEmpty()) {
            showPlaceholderImage()
        } else {
            displayImage()
        }
    }

    private fun initializeViews() {
        imageView = findViewById(R.id.imageViewIdLiked)
        previousButton = findViewById(R.id.prevBtnLiked)
        nextButton = findViewById(R.id.nextBtnLiked)
        homeButton = findViewById(R.id.home)
        webButton = findViewById(R.id.webBtn)
        instagramButton = findViewById(R.id.instagramBtn)
        linkedinButton = findViewById(R.id.linkedinBtn)
        githubButton = findViewById(R.id.githubBtn)
        quoteTextView = findViewById<TextView>(R.id.quoteTextViewLiked)
        authorTextView = findViewById<TextView>(R.id.authorTextViewLiked)
        val vibrator = getSystemService(Vibrator::class.java) as Vibrator
        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }
    private val quoteList = listOf(
        "Be yourself; everyone else is already taken.",
        "So many books, so little time.",
        "A room without books is like a body without a soul.",
        "In the middle of every difficulty lies opportunity.",
        "Our greatest glory is not in never falling, but in rising every time we fall.",
        "That which does not kill us makes us stronger.",
        "Happiness is not something ready made. It comes from your own actions.",
        "The purpose of our lives is to be happy."
    )

    private val authorList = listOf(
        "Oscar Wilde",
        "Frank Zappa",
        "Marcus Tullius Cicero",
        "Albert Einstein",
        "Confucius",
        "Friedrich Nietzsche",
        "Dalai Lama",
        "Dalai Lama"
    )

    private fun setupListeners() {
        previousButton.setOnClickListener {
            vibrate()
            imageIndex = (imageIndex - 1 + likedImages.size) % likedImages.size
            displayImage()
        }

        nextButton.setOnClickListener {
            vibrate()
            imageIndex = (imageIndex + 1) % likedImages.size
            displayImage()
        }

        homeButton.setOnClickListener {
            vibrate()
            finish()
        }

        webButton.setOnClickListener {
            openUrl("https://bento.me/krushnalpatil")
        }

        instagramButton.setOnClickListener {
            openUrl("https://www.instagram.com/krushnal_patil_111")
        }

        linkedinButton.setOnClickListener {
            openUrl("https://www.linkedin.com/in/krushnal-jagannath-patil/")
        }

        githubButton.setOnClickListener {
            openUrl("https://github.com/Krushnal121")
        }
    }

    private fun displayImage() {
        val imageResId = likedImages[imageIndex]
        imageView.setImageResource(imageResId)

        quoteTextView.text = "“${quoteList[imageIndex]}”"
        authorTextView.text = "- ${authorList[imageIndex]}"
    }



    private fun showPlaceholderImage() {
        previousButton.visibility = View.INVISIBLE
        nextButton.visibility = View.INVISIBLE
        imageView.setImageResource(placeholderImageResId)
    }

    private fun openUrl(url: String) {
        vibrate()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun vibrate(duration: Long = 10L) {
        vibrator.vibrate(duration)
    }
}
