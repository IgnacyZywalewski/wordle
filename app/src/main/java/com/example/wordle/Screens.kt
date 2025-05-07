package com.example.wordle

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordleScreen() {
    var selectedLanguage by remember { mutableStateOf("Angielski") }
    var selectedLength by remember { mutableStateOf("5") }
    val wordLength = selectedLength.toInt()
    val totalRows = 6
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    data class GameState(
        val guesses: SnapshotStateList<SnapshotStateList<Char>>,
        val rowColors: SnapshotStateMap<Int, List<Color>>,
        val keyboardColors: SnapshotStateMap<String, Color>,
        var currentRow: Int,
        var currentIndex: Int,
        var targetWord: String,
        val validWords: List<String>,
        var isGameOver: Boolean = false
    )

    fun loadWords(language: String, length: Int, context: Context): List<String> {
        val fileName = when (language) {
            "Polski" -> "slowa_${length}.txt"
            else -> "words_${length}.txt"
        }
        return try {
            context.assets.open(fileName).bufferedReader().useLines { lines ->
                lines.map { it.trim().lowercase() }.toList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun randomWord(words: List<String>): String {
        return if (words.isNotEmpty()) words.random() else ""
    }

    fun createGameState(): GameState {
        val words = loadWords(selectedLanguage, wordLength, context)
        return GameState(
            guesses = MutableList(totalRows) { MutableList(wordLength) { ' ' } }.map { it.toMutableStateList() }.toMutableStateList(),
            rowColors = mutableStateMapOf(),
            keyboardColors = mutableStateMapOf(),
            currentRow = 0,
            currentIndex = 0,
            targetWord = randomWord(words),
            validWords = words
        )
    }

    val gameStates = remember { mutableStateMapOf<Pair<String, Int>, GameState>() }
    val currentKey = selectedLanguage to wordLength
    val currentState = gameStates.getOrPut(currentKey) { createGameState() }

    fun resetGrid() {
        val newState = createGameState()
        gameStates[currentKey] = newState
    }

    fun updateColors(guess: String, answer: String, state: GameState) {
        val rowIndex = state.currentRow
        val rowColor = MutableList(guess.length) { Color(0xFF3A3A3C) }
        val answerCharCount = mutableMapOf<Char, Int>()
        answer.forEach { c -> answerCharCount[c] = answerCharCount.getOrDefault(c, 0) + 1 }

        for (i in guess.indices) {
            if (guess[i] == answer[i]) {
                rowColor[i] = Color(0xFF538D4E)
                answerCharCount[guess[i]] = answerCharCount[guess[i]]!! - 1
                state.keyboardColors[guess[i].toString()] = Color(0xFF538D4E)
            }
        }

        for (i in guess.indices) {
            if (guess[i] != answer[i] && answerCharCount.getOrDefault(guess[i], 0) > 0) {
                rowColor[i] = Color(0xFFB59F3B)
                if (state.keyboardColors[guess[i].toString()] != Color(0xFF538D4E)) {
                    state.keyboardColors[guess[i].toString()] = Color(0xFFB59F3B)
                }
                answerCharCount[guess[i]] = answerCharCount[guess[i]]!! - 1
            } else if (rowColor[i] == Color(0xFF3A3A3C)) {
                state.keyboardColors.putIfAbsent(guess[i].toString(), Color(0xFF3A3A3C))
            }
        }
        state.rowColors[rowIndex] = rowColor
    }

    fun onKeyPress(key: String) {
        if (currentState.isGameOver && key != "Reset") return

        when (key) {
            "⌫" -> {
                if (currentState.currentIndex > 0) {
                    currentState.currentIndex--
                    currentState.guesses[currentState.currentRow][currentState.currentIndex] = ' '
                }
            }
            "Enter" -> {
                if (currentState.currentIndex < wordLength) {
                    snackbarMessage = "Niepełne słowo"
                    return
                }

                val guess = currentState.guesses[currentState.currentRow].joinToString("")
                if (guess.lowercase() in currentState.validWords) {
                    updateColors(guess.uppercase(), currentState.targetWord.uppercase(), currentState)

                    if (guess.equals(currentState.targetWord, ignoreCase = true)) {
                        currentState.isGameOver = true
                        snackbarMessage = "Gratulacje! Odgadłeś słowo"
                    } else if (currentState.currentRow == totalRows - 1) {
                        currentState.isGameOver = true
                        snackbarMessage = "Koniec gry. Hasło to: ${currentState.targetWord.uppercase()}"
                    } else {
                        currentState.currentRow++
                        currentState.currentIndex = 0
                    }
                } else {
                    snackbarMessage = "Nieprawidłowe słowo"
                }
            }
            "Reset" -> resetGrid()
            else -> {
                if (key.length == 1 && currentState.currentIndex < wordLength) {
                    currentState.guesses[currentState.currentRow][currentState.currentIndex] = key[0]
                    currentState.currentIndex++
                }
            }
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = data.visuals.message,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                modifier = Modifier.height(170.dp),
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (selectedLanguage == "Polski") "LITERALNIE" else "WORDLE",
                            fontSize = 40.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        DropDownMenu(
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = {
                                selectedLanguage = it
                                resetGrid()
                            },
                            selectedLength = selectedLength,
                            onLengthSelected = {
                                selectedLength = it
                                resetGrid()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                key(wordLength) {
                    WordleGrid(
                        wordLength = wordLength,
                        guesses = currentState.guesses,
                        rowColors = currentState.rowColors
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .padding(10.dp)
            ) {
                Keyboard(selectedLanguage, currentState.keyboardColors) { key ->
                    onKeyPress(key)
                }
            }
        }
    )
}

