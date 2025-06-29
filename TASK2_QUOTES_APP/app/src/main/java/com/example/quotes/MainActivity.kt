package com.example.quotes

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val categoryImages: Map<QuoteCategory, Int> = mapOf(
        QuoteCategory.IDENTITY to R.drawable.identity,
        QuoteCategory.READING to R.drawable.reading,
        QuoteCategory.RESILIENCE to R.drawable.resilience,
        QuoteCategory.HAPPINESS to R.drawable.happiness,
        QuoteCategory.INNOVATION to R.drawable.innovation,
        QuoteCategory.SUCCESS to R.drawable.success
    )

    private lateinit var imageView: ImageView
    private lateinit var quoteTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var previousButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var viewLikedImagesButton: Button
    private lateinit var vibrator: Vibrator
    private lateinit var shareButton: MaterialButton
    private lateinit var likeButton: MaterialButton
    private lateinit var viewModel: QuotesViewModel
    private var imageUri: Uri? = null
    private lateinit var tabLayout: TabLayout

    private val VIBRATION_DURATION_MS = 50L


    private val categories = listOf(
        "Self & Identity", "Books & Knowledge", "Resilience",
        "Happiness", "Innovation", "Success"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)

        initializeViews()
        setupViewModel()
        setupButtonListeners()
        setupTabLayout()
    }

    private fun initializeViews() {
        imageView = findViewById(R.id.imageViewId)
        quoteTextView = findViewById(R.id.quoteTextView)
        authorTextView = findViewById(R.id.authorTextView)
        previousButton = findViewById(R.id.prevBtn)
        nextButton = findViewById(R.id.nextBtn)
        likeButton = findViewById(R.id.likeBtn)
        viewLikedImagesButton = findViewById(R.id.likedQuotes)
        shareButton = findViewById(R.id.shareBtn)
        vibrator = getSystemService(Vibrator::class.java)
        tabLayout = findViewById(R.id.tabLayout)
    }

    private fun setupTabLayout() {
        // Add tabs
        categories.forEach { category ->
            tabLayout.addTab(tabLayout.newTab().setText(category))
        }

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    if (position in QuoteCategory.values().indices) {
                        val selectedCategory = QuoteCategory.values()[position]
                        viewModel.loadQuotesForCategory(selectedCategory)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(QuotesViewModel::class.java)
        viewModel.initSharedPreferences(getSharedPreferences("QuotesPrefs", MODE_PRIVATE))

        viewModel.currentQuoteLive.observe(this) { quote ->
            Log.d("QuoteUpdate", "UI Updated with quote: ${quote?.text ?: "null"}")
            quote?.let { displayQuote(it) } ?: run {
                Toast.makeText(this, "No quotes available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadQuotes() // Initial load
        viewModel.loadQuotesForCategory(QuoteCategory.IDENTITY)
    }

    private fun setupButtonListeners() {
        nextButton.setOnClickListener {
            vibrate()
            if (viewModel.filteredQuotesSize > 1) {
                viewModel.showNextQuote()
            } else {
                Toast.makeText(this, "Only one quote in this category", Toast.LENGTH_SHORT).show()
            }
        }

        previousButton.setOnClickListener {
            vibrate()
            if (viewModel.filteredQuotesSize > 1) {
                viewModel.showPreviousQuote()
            } else {
                Toast.makeText(this, "Only one quote in this category", Toast.LENGTH_SHORT).show()
            }
        }

        likeButton.setOnClickListener {
            vibrate()
            viewModel.toggleLike()
            viewModel.saveLikedQuotes()
            updateLikeButton()
        }

        viewLikedImagesButton.setOnClickListener {
            vibrate()
            viewModel.saveLikedQuotes()
            val intent = Intent(this, LikedImagesActivity::class.java)
            intent.putExtra("likedImages", viewModel.getLikedImageResourceIds().toIntArray())
            startActivity(intent)
        }

        shareButton.setOnClickListener {
            shareImage()
        }
    }

    private fun displayQuote(quote: Quote) {
        quoteTextView.text = quote.text
        authorTextView.text = quote.author.ifEmpty { getString(R.string.unknown_author) }

        val imageRes = categoryImages[quote.category] ?: R.drawable.nothing
        imageView.setImageResource(imageRes)

        updateLikeButton()
        syncTabSelection(quote.category)
    }

    private fun syncTabSelection(category: QuoteCategory) {
        val index = QuoteCategory.values().indexOf(category)
        if (index != -1 && tabLayout.selectedTabPosition != index) {
            tabLayout.getTabAt(index)?.select()
        }
    }

    private fun updateLikeButton() {
        Toast.makeText(this, if (viewModel.isLiked()) "Liked" else "Unliked", Toast.LENGTH_SHORT).show()
        likeButton.setIconResource(
            if (viewModel.isLiked()) R.drawable.heartfilled else R.drawable.heartempty

        )
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }

    private fun shareImage() {
        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap ?: return
        val imageFile = createTempImageFile(bitmap)
        imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, "${quoteTextView.text} - ${authorTextView.text}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // <-- Required!
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image_using)))
    }

    private fun createTempImageFile(bitmap: Bitmap): File {
        val cacheDir = applicationContext.cacheDir
        val tempFile = File.createTempFile("temp_image_", ".jpg", cacheDir)
        tempFile.deleteOnExit()

        FileOutputStream(tempFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }

        return tempFile
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveLikedQuotes()
    }
}