package com.example.wordle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DropDownMenu(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    selectedLength: String,
    onLengthSelected: (String) -> Unit
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }
    val languageOptions = listOf("Angielski", "Polski")

    var lengthMenuExpanded by remember { mutableStateOf(false) }
    val lengthOptions = listOf("4", "5", "6")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Język
        Box(modifier = Modifier.weight(1f)) {
            Text(
                selectedLanguage,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { languageMenuExpanded = true }
                    .background(Color.DarkGray)
                    .padding(vertical = 10.dp)
            )
            DropdownMenu(
                expanded = languageMenuExpanded,
                onDismissRequest = { languageMenuExpanded = false },
                modifier = Modifier.background(Color.DarkGray)
            ) {
                languageOptions.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language, fontSize = 20.sp, color = Color.White) },
                        onClick = {
                            onLanguageSelected(language)
                            languageMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Długość słowa
        Box(modifier = Modifier.weight(1f)) {
            val lengthWord = if (selectedLength == "4") "litery" else "liter"
            Text(
                "$selectedLength $lengthWord",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { lengthMenuExpanded = true }
                    .background(Color.DarkGray)
                    .padding(vertical = 10.dp)
            )
            DropdownMenu(
                expanded = lengthMenuExpanded,
                onDismissRequest = { lengthMenuExpanded = false },
                modifier = Modifier.background(Color.DarkGray)
            ) {
                lengthOptions.forEach { length ->
                    val word = if (length == "4") "litery" else "liter"
                    DropdownMenuItem(
                        text = { Text("$length $word", fontSize = 20.sp, color = Color.White) },
                        onClick = {
                            onLengthSelected(length)
                            lengthMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WordleGrid(wordLength: Int, guesses: List<List<Char>>, rowColors: Map<Int, List<Color>>) {
    val boxSize = if (wordLength == 6) 68.dp else 75.dp

    key(wordLength) {
        Column(modifier = Modifier.fillMaxWidth()) {
            guesses.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEachIndexed { colIndex, letter ->
                        val color = rowColors[rowIndex]?.getOrNull(colIndex) ?: Color.DarkGray
                        Box(
                            modifier = Modifier
                                .size(boxSize)
                                .padding(4.dp)
                                .background(color, shape = RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.toString(),
                                fontSize = 24.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Keyboard(
    selectedLanguage: String,
    keyboardColors: Map<String, Color>,
    onKeyClick: (String) -> Unit
) {
    val row0 = "ĄĆĘŁÓŚŃŻŹ"
    val row1 = "QWERTYUIOP"
    val row2 = "ASDFGHJKL"
    val row3 = "ZXCVBNM"
    val row4 = listOf("Reset", "Enter", "⌫")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (selectedLanguage == "Polski") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                row0.forEach { letter -> KeyboardBox(letter.toString(), keyboardColors[letter.toString()] ?: Color.Gray) { onKeyClick(letter.toString()) } }
            }
        }

        listOf(row1, row2, row3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                row.forEach { letter -> KeyboardBox(letter.toString(), keyboardColors[letter.toString()] ?: Color.Gray) { onKeyClick(letter.toString()) } }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            row4.forEach { key -> KeyboardBox(key, Color.Gray, Modifier.weight(1f)) { onKeyClick(key) } }
        }
    }
}

@Composable
fun KeyboardBox(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .padding(3.dp)
            .width(37.dp)
            .height(50.dp)
            .background(color, shape = RoundedCornerShape(5.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
