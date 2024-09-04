package com.example.bookswap.screens.bookScreens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookswap.screens.appComponents.CustomDropdownMenu
import com.example.bookswap.screens.appComponents.FilterLanguage
import com.example.bookswap.screens.appComponents.FilterOptionRow
import com.example.bookswap.screens.appComponents.RadiusSlider

@Composable
fun FilterDialog(
    isDialogOpen: MutableState<Boolean>,
    onDismissRequest: () -> Unit,
    onApplyFilters: (Map<String, String>) -> Unit, //callback metoda, poziva se na apply click
    isMap : Boolean
) {
    if (isDialogOpen.value) {
        val filterOptions = remember { mutableStateOf(mapOf<String, String>()) }
        val genres = listOf("Romance", "Drama", "Popularna psihologija", "Fiction", "Non-fiction", "Sci-Fi", "Fantasy", "Mystery")
        val radiusValue = remember { mutableFloatStateOf(filterOptions.value["radius"]?.toFloat() ?: 10f) }

        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(20.dp, Color(0xFF6D4C41)),
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5E6CC)
            ) {
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    title = {
                        Text(
                            text = "Filter Books",
                            fontStyle = FontStyle.Italic,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D4C41),
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Spacer(modifier = Modifier.height(20.dp))
                            FilterOptionRow(
                                label = "Author:",
                                filterOptions = filterOptions, //upisuje izabranu vrednost u mapu
                                key = "author"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CustomDropdownMenu(
                                filterOptions = filterOptions,
                                key = "genre",
                                options = genres
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FilterLanguage(filterOptions = filterOptions, key = "language")
                            Spacer(modifier = Modifier.height(16.dp))
                            if(isMap) {
                                RadiusSlider(filterOptions = filterOptions, key = "radius")
                            }
                        }
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            androidx.compose.material3.Button(
                                onClick = onDismissRequest,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6D4C41),
                                    contentColor = Color(0xFF3C0B1A)
                                )
                            ) {
                                androidx.compose.material3.Text(
                                    text = "Cancel",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEDC9AF),
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            androidx.compose.material3.Button(
                                onClick = {
                                    onApplyFilters(filterOptions.value) //poziv callback fje da primeni unete filtere
                                    Log.d("FilterDialog", "Filters applied: ${filterOptions.value}")
                                    onDismissRequest()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6D4C41),
                                    contentColor = Color(0xFF3C0B1A)
                                )
                            ) {
                                androidx.compose.material3.Text(
                                    text = "Apply",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEDC9AF),
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

