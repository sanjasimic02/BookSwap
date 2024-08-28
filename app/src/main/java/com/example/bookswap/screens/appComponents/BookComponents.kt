package com.example.bookswap.screens.appComponents

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookswap.R

@Composable
fun BookDataInput(
    onValueChange: (String) -> Unit = {},
    hint: String,
    value: MutableState<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        TextField(
            value = value.value,
            onValueChange = { newValue ->
                value.value = newValue
                onValueChange(newValue)
            },
            singleLine = true,
            placeholder = {
                Text(
                    text = hint,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color(0xFFBCAAA4)
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFBCAAA4),
                unfocusedIndicatorColor = Color(0xFFBCAAA4),
            ),
            keyboardOptions = KeyboardOptions.Default,
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = Color(0xFF6D4C41)
            )
        )
    }
}

@Composable
fun UploadBookCoverImg(
    selectedImg: MutableState<Uri?>,
    isError: MutableState<Boolean>
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImg.value = uri
        }
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImg.value == Uri.EMPTY || selectedImg.value == null) {
            Image(
                painter = painterResource(id = R.drawable.book_cover),
                contentDescription = "Book cover image",
                modifier = Modifier
                    .size(140.dp)
                    .border(
                        if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                            0.dp,
                            Color.Transparent
                        )
                    )
                    //.clip(RoundedCornerShape(70.dp)) // 50% border radius
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        } else {
            selectedImg.value?.let { uri ->
                Image(
                    painter = painterResource(id = R.drawable.profilepic),
                    contentDescription = "Book cover image",
                    modifier = Modifier
                        .size(140.dp)
                        .border(
                            if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                                0.dp,
                                Color.Transparent
                            )
                        )
                        //.clip(RoundedCornerShape(70.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                )
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        //.clip(RoundedCornerShape(70.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun UploadBookImages(
    selectedImages: MutableState<List<Uri>>
) {
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris != null) {
            selectedImages.value += uris
        }
    }

    LazyRow {
        if (selectedImages.value.size < 5) {
            item {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(100.dp)
                        .height(100.dp)
                        .border(
                            1.dp,
                            color = Color(0xFFC1C1C1),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .background(
                            color = Color(0xFFC1C1C1),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .clickable { pickImagesLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = "")
                }
            }
        }
        items(selectedImages.value.size) { index ->
            val uri = selectedImages.value[index]
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        1.dp,
                        Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .clickable { selectedImages.value -= uri },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

@Composable
fun FilterOptionRow(
    label: String,
    filterOptions: MutableState<Map<String, String>>,
    key: String
) {
    val text = remember { mutableStateOf(filterOptions.value[key] ?: "") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF6D4C41), // Dark brown color for the labels
            modifier = Modifier.weight(1f)
        )
        androidx.compose.material3.TextField(
            value = text.value,
            onValueChange = { newValue ->
                text.value = newValue
                filterOptions.value = filterOptions.value.toMutableMap().apply { put(key, newValue) }
            },
            modifier = Modifier
                .weight(2f)
                .height(56.dp)
                .padding(start = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFF6D4C41),
                unfocusedIndicatorColor = Color(0xFF6D4C41),
            ),
            singleLine = true
        )
    }
}

@Composable
fun FilterLanguage(
    //label: String,
    filterOptions: MutableState<Map<String, String>>,
    key: String
) {
    val text = remember { mutableStateOf(filterOptions.value[key] ?: "") }

    Column(
        modifier = Modifier
            //.padding(16.dp)
    ) {
        // "Language" label
        Text(
            text = "Language:",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF6D4C41)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Row(
                modifier = Modifier.padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = text.value == "Srpski",
                    onClick = {
                        text.value = "Srpski"
                        filterOptions.value = filterOptions.value.toMutableMap().apply { put(key, text.value) }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF6D4C41)
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Srpski",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF6D4C41)
                    )
                )
            }

            // English Radio Button and Label
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = text.value == "English",
                    onClick = {
                        text.value = "English"
                        filterOptions.value = filterOptions.value.toMutableMap().apply { put(key, text.value) }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF6D4C41)
                    )
                )
                Spacer(modifier = Modifier.width(4.dp)) // Small space between RadioButton and Label
                Text(
                    text = "English",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF6D4C41)
                    )
                )
            }
        }
    }
}

@Composable
fun CustomDropdownMenu(
    filterOptions: MutableState<Map<String, String>>,
    key: String,
    options: List<String>
) {
    val text = remember { mutableStateOf(filterOptions.value[key] ?: "") }
    val expanded = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
            //.background(Color(0xFFEDC9AF)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Genre:",
            color = Color(0xFF6D4C41),
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .weight(2f)
                .padding(start = 8.dp)
                .height(56.dp)
                .clickable { expanded.value = true }
                .background(
                    color = Color(0xFF6D4C41).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = text.value.ifEmpty { "Select genre" },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart),
                color = Color(0xFFEDC9AF)
            )

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                //modifier = Modifier.background(Color(0xFFEDC9AF))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            text.value = option
                            filterOptions.value = filterOptions.value.toMutableMap().apply { put(key, option) }
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}
