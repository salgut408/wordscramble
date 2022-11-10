package com.example.android.unscramble.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.android.unscramble.data.SCORE_INCREASE
import kotlinx.coroutines.flow.update


class GameViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")
    private set


    init {
        resetGame()
    }

    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun updateGameState(updatedScore: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScore,
                currentWordCount = currentState.currentWordCount.inc()
            )
        }
    }

    fun checkUserGuess() {
        if(_uiState.value.userGuess.equals(currentWord, true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)

        }else {
            updateUserGuess("")
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
    }
//    fun checkUserGuess() {
//        if (userGuess.equals(currentWord, ignoreCase = true)) {
//
//        }else {
//
//        }
//        updateUserGuess("")
//    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while ( String(tempWord).equals(word)){
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        if(usedWords.contains(currentWord)){
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }
}