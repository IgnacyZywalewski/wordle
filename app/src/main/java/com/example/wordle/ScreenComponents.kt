package com.example.wordle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DropDownMenu() {
    var selectedLanguage by remember { mutableStateOf("Angielski") }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    val languageOptions = listOf("Angielski", "Polski")

    var selectedLength by remember { mutableStateOf("5") }
    var lengthMenuExpanded by remember { mutableStateOf(false) }
    val lengthOptions = listOf("4", "5", "6")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Jezyki
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
                    .padding(10.dp)
            )
            DropdownMenu(
                expanded = languageMenuExpanded,
                onDismissRequest = { languageMenuExpanded = false }
            ) {
                languageOptions.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language, fontSize = 15.sp) },
                        onClick = {
                            selectedLanguage = language
                            languageMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Dlugosc slow
        Box(modifier = Modifier.weight(1f)) {
            var lengthWord = if(selectedLength.toInt() == 4) "litery" else "liter"
            Text(
                "$selectedLength $lengthWord",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { lengthMenuExpanded = true }
                    .background(Color.DarkGray)
                    .padding(10.dp)
            )
            DropdownMenu(
                expanded = lengthMenuExpanded,
                onDismissRequest = { lengthMenuExpanded = false }
            ) {
                lengthOptions.forEach { length ->
                    lengthWord = if(length.toInt() == 4) "litery" else "liter"

                    DropdownMenuItem(
                        text = { Text("$length $lengthWord", fontSize = 15.sp) },
                        onClick = {
                            selectedLength = length
                            lengthMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WordleGrid() {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(6) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(Color.DarkGray, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "", fontSize = 24.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun Keyboard() {
    val row1 = "QWERTYUIOP"
    val row2 = "ASDFGHJKL"
    val row3 = "ZXCVBNM"
    val row4 = listOf("Enter") + listOf("⌫")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            row1.forEach { letter -> KeyboardButton(letter.toString()) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            row2.forEach { letter -> KeyboardButton(letter.toString()) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            row3.forEach { letter -> KeyboardButton(letter.toString()) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            row4.forEach { letter -> KeyboardButton(letter, Modifier.weight(1f)) }
        }
    }
}

@Composable
fun KeyboardButton(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier
            .padding(4.dp)
            .widthIn(max = 37.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}