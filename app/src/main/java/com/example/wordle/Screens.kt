package com.example.wordle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordleScreen() {
    var selectedLanguage by remember { mutableStateOf("Angielski") }
    var selectedLength by remember { mutableStateOf("5") }
    val wordLength = selectedLength.toInt()
    val totalRows = 6

    data class GameState(
        val guesses: SnapshotStateList<SnapshotStateList<Char>>,
        var currentRow: Int,
        var currentIndex: Int
    )

    val gameStates = remember {
        mutableStateMapOf<Pair<String, Int>, GameState>()
    }

    val currentKey = selectedLanguage to wordLength
    val currentState = gameStates.getOrPut(currentKey) {
        GameState(
            guesses = MutableList(totalRows) { MutableList(wordLength) { ' ' } }.map {
                it.toMutableStateList()
            }.toMutableStateList(),
            currentRow = 0,
            currentIndex = 0
        )
    }

    fun resetGrid() {
        gameStates[currentKey] = GameState(
            guesses = MutableList(totalRows) { MutableList(wordLength) { ' ' } }.map {
                it.toMutableStateList()
            }.toMutableStateList(),
            currentRow = 0,
            currentIndex = 0
        )
    }

    fun onKeyPress(key: String) {
        when (key) {
            "⌫" -> {
                if (currentState.currentIndex > 0) {
                    currentState.currentIndex--
                    currentState.guesses[currentState.currentRow][currentState.currentIndex] = ' '
                }
            }
            "Enter" -> {
                if (currentState.currentIndex == wordLength && currentState.currentRow < totalRows - 1) {
                    currentState.currentRow++
                    currentState.currentIndex = 0
                }
            }
            "Reset" -> {
                resetGrid()
            }
            else -> {
                if (key.length == 1 && currentState.currentIndex < wordLength) {
                    currentState.guesses[currentState.currentRow][currentState.currentIndex] = key[0]
                    currentState.currentIndex++
                }
            }
        }
    }

    Scaffold(
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
                            },
                            selectedLength = selectedLength,
                            onLengthSelected = {
                                selectedLength = it
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
                WordleGrid(wordLength, currentState.guesses)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .padding(10.dp)
            ) {
                Keyboard(selectedLanguage) { key ->
                    onKeyPress(key)
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewWordleScreen() {
    WordleScreen()
}