package com.example.quotes

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuotesViewModel : ViewModel() {
    val filteredQuotesSize: Int
        get() = _filteredQuotes.size
    private val _quotes = MutableLiveData<List<Quote>>()
    val quotes: LiveData<List<Quote>> get() = _quotes

    companion object {
        private val allQuotes = listOf(
            Quote(
                "Be yourself; everyone else is already taken.",
                "Oscar Wilde",
                QuoteCategory.IDENTITY,
                R.drawable.identity
            ),
            Quote(
                "So many books, so little time.",
                "Frank Zappa",
                QuoteCategory.READING,
                R.drawable.reading
            ),
            Quote(
                "A room without books is like a body without a soul.",
                "Marcus Tullius Cicero",
                QuoteCategory.READING,
                R.drawable.reading
            ),

            // Add multiple quotes per category
            Quote(
                "In the middle of every difficulty lies opportunity.",
                "Albert Einstein",
                QuoteCategory.RESILIENCE,
                R.drawable.resilience
            ),
            Quote(
                "Our greatest glory is not in never falling, but in rising every time we fall.",
                "Confucius",
                QuoteCategory.RESILIENCE,
                R.drawable.resilience
            ),
            Quote(
                "That which does not kill us makes us stronger.",
                "Friedrich Nietzsche",
                QuoteCategory.RESILIENCE,
                R.drawable.resilience
            ),

            Quote(
                "Happiness is not something ready made. It comes from your own actions.",
                "Dalai Lama",
                QuoteCategory.HAPPINESS,
                R.drawable.happiness
            ),
            Quote(
                "The purpose of our lives is to be happy.",
                "Dalai Lama",
                QuoteCategory.HAPPINESS,
                R.drawable.happiness
            )
        )
    }

    private var _filteredQuotes: List<Quote> = emptyList()
    private val _currentQuote = MutableLiveData<Quote?>()
    val currentQuoteLive: LiveData<Quote?> get() = _currentQuote

    private var currentCategoryQuotes: List<Quote> = emptyList()

    var currentIndex = 0
        private set // Make setter private to prevent external modification

    // Initialize with first category
    init {
        loadQuotesForCategory(QuoteCategory.IDENTITY)
    }

    val likedQuotes = mutableSetOf<Int>()  // Stores indices of liked quotes

    private var sharedPreferences: SharedPreferences? = null
    private var currentCategory: QuoteCategory = QuoteCategory.IDENTITY // Default category

    val currentQuote: Quote?
        get() = _quotes.value?.getOrNull(currentIndex)

    fun initSharedPreferences(prefs: SharedPreferences) {
        sharedPreferences = prefs
        loadLikedQuotes()
    }

    fun showNextQuote() {
        when {
            _filteredQuotes.isEmpty() -> {
                Log.d("Navigation", "No quotes available")
            }

            _filteredQuotes.size == 1 -> {
                Log.d("Navigation", "Only one quote available")
                // Optionally show a Toast
            }

            else -> {
                currentIndex = (currentIndex + 1) % _filteredQuotes.size
                _currentQuote.value = _filteredQuotes[currentIndex]
            }
        }
    }

    fun showPreviousQuote() {
        when {
            _filteredQuotes.isEmpty() -> {
                Log.d("Navigation", "No quotes available")
            }

            _filteredQuotes.size == 1 -> {
                Log.d("Navigation", "Only one quote available")
                // Optionally show a Toast
            }

            else -> {
                currentIndex = (currentIndex - 1 + _filteredQuotes.size) % _filteredQuotes.size
                _currentQuote.value = _filteredQuotes[currentIndex]
            }
        }
    }

    fun loadQuotesForCategory(category: QuoteCategory) {
        currentCategory = category
        _filteredQuotes = allQuotes.filter { it.category == category }
        currentIndex = 0

        if (_filteredQuotes.isNotEmpty()) {
            _currentQuote.value = _filteredQuotes[0]
        } else {
            _currentQuote.value = null
        }

        Log.d("CategoryLoad", "Loaded ${_filteredQuotes.size} quotes for $category")
    }

    fun loadQuotes() {
        val filtered = allQuotes.filter { it.category == currentCategory }
        _quotes.value = filtered
        _currentQuote.value = filtered.getOrNull(currentIndex)
    }

    fun getLikedImageResourceIds(): List<Int> {
        return likedQuotes.mapNotNull { index ->
            allQuotes.getOrNull(index)?.imageResId
        }
    }


    fun toggleLike() {
        currentQuote?.let { quote ->
            val index = allQuotes.indexOf(quote)
            if (index != -1) {
                if (likedQuotes.contains(index)) {
                    likedQuotes.remove(index)
                } else {
                    likedQuotes.add(index)
                }
            }

        }
    }

    fun isLiked(): Boolean {
        return currentQuote?.let { quote ->
            likedQuotes.contains(allQuotes.indexOf(quote))
        } ?: false
    }

    private fun loadLikedQuotes() {
        likedQuotes.clear()
        val size = allQuotes.size
        sharedPreferences?.getStringSet("liked_quotes", emptySet())?.forEach {
            it.toIntOrNull()?.let { index -> if (index in 0 until size) likedQuotes.add(index) }
        }
    }

    fun saveLikedQuotes() {
        sharedPreferences?.edit()?.apply {
            putStringSet("liked_quotes", likedQuotes.map { it.toString() }.toSet())
            apply()
        }
    }
}

