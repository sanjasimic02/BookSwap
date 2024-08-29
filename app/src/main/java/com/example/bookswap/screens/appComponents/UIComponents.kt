package com.example.bookswap.screens.appComponents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookswap.R
import com.example.bookswap.models.User
import com.example.bookswap.navigation.Routes
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


@Composable
fun BSBackground(content: @Composable () -> Unit) {
    val backgroundColor = Color(0xFFF5E6CC)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()
        }
    }
}

@Composable
fun StartImage(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login), // Replace with your image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop, // Adjust based on how you want the image to fit
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()
        }
    }
}
@Composable
fun Heading(text: String) {
    Text(
        text = text,
        color = Color(0xFF4E342E),
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        letterSpacing = 1.5.sp,
        style = TextStyle(
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic
        ),
        modifier = Modifier
            .width(250.dp)
            .padding(vertical = 8.dp)
    )
}

@Composable
fun Heading2(secondary_text: String) {
    Text(
        text = secondary_text,
        color = Color(0xFF6D4C41), // Chestnut color
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Start,
        style = TextStyle(
            fontFamily = FontFamily.Serif
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp)
    )
}

@Composable
fun CustomLabel(label: String) {
    Text(
        text = label,
        color = Color(0xFF8D6E63),
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        style = TextStyle(
            fontFamily = FontFamily.Serif
        ),
        modifier = Modifier.padding(start = 8.dp, top = 8.dp)

    )
}

@Composable
fun CustomInput(
    onValueChange: (String) -> Unit = {},
    hint: String,
    value: MutableState<String>,
    isEmail: Boolean = false,
    isNumber: Boolean = false,
    isError: MutableState<Boolean>,
    errorText: MutableState<String>
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
                if (isError.value) {
                    isError.value = false
                }
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
                focusedIndicatorColor = if (isError.value) Color(0xFFD32F2F) else Color(0xFFBCAAA4),
                unfocusedIndicatorColor = if (isError.value) Color(0xFFD32F2F) else Color(0xFFBCAAA4),
                ),
            keyboardOptions = KeyboardOptions.Default,
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = Color(0xFF6D4C41)
            )
        )

        if (isError.value && errorText.value.isNotEmpty()) {
            Text(
                text = errorText.value,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun UploadProfileImg(
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
                painter = painterResource(id = R.drawable.profilepic),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(140.dp)
                    .border(
                        if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                            0.dp,
                            Color.Transparent
                        )
                    )
                    .clip(RoundedCornerShape(70.dp)) // 50% border radius
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
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .border(
                            if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                                0.dp,
                                Color.Transparent
                            )
                        )
                        .clip(RoundedCornerShape(70.dp))
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
                        .clip(RoundedCornerShape(70.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Password(
    inputValue: MutableState<String>,
    hint: String,
    isError: MutableState<Boolean>,
    errorText: MutableState<String>,
    textStyle: TextStyle = TextStyle(fontSize = 18.sp, color = Color(0xFFBCAAA4)
    )
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = inputValue.value,
        onValueChange = { newValue ->
            inputValue.value = newValue
            if (isError.value) {
                isError.value = false
            }
        },
        singleLine = true,
        placeholder = {
            Text(
                text = hint,
                style = textStyle.copy(color = Color(0xFF9E9E9E))
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                showPassword = !showPassword
            }) {
                Icon(
                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E)
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = if (isError.value) Color(0xFFD32F2F) else Color(0xFFBCAAA4),
            unfocusedIndicatorColor = if (isError.value) Color(0xFFD32F2F) else Color(0xFFBCAAA4),
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        textStyle = textStyle
    )

    if (isError.value && errorText.value.isNotEmpty()) {
        Text(
            text = errorText.value,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = Color(0xFFB71C1C)
            )
        )
    } else {
        Text(text = " ")
    }
}

@Composable
fun RegisterButton(
    onClick: () -> Unit = {},
    buttonText: String,
   // iconImage: ImageVector? = null,
    isEnabled: MutableState<Boolean> = mutableStateOf(true),
    isLoading: MutableState<Boolean> = mutableStateOf(false),
    buttonBackgroundColor: Color = Color(0xFF6D4C41), // Warm brown color
    buttonTextColor: Color = Color(0xFFEDC9AF), // Creamy white color
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 4.dp)
            .height(48.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled.value) buttonBackgroundColor else Color.Gray,
            contentColor = buttonTextColor,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = isEnabled.value
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = buttonTextColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = buttonText,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = buttonTextColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun Alternative(
    text: String,
    link: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFF6F4F28),
                fontStyle = FontStyle.Italic
            )
        )
        Text(
            text = link,
            modifier = Modifier
                .clickable { onClick() }
                .padding(start = 4.dp),
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFF8B572A),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

@Composable
fun CustomDialog(
    showDialog: MutableState<Boolean>,
    onAddBookClick: () -> Unit
) {
    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                       // contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = { showDialog.value = false }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                        }

                    }
                    // Dialog Content
                    Text("Add a book on your current location.")
                    Spacer(modifier = Modifier.height(8.dp))
                    // Content Spacer
                    //Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            onAddBookClick()
                            showDialog.value = false
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Add New Book")
                    }
                }
            }
        }
    }
}

// Helper function to get a BitmapDescriptor from a vector resource
fun bitmapDescriptorFromVector2(context: Context, vectorResId: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

@Composable
fun UserRankingCard(
    user: User,
    rank: Int,
    navController: NavController,
    userId : String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)) // Rounded corners
            .background(Color(0xFF6D4C41))
            .clickable {
                navController.navigate(Routes.bookOwnerScreen + "/${userId}")
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(Color(0xFF6D4C41)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$rank.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp),
                color = Color(0xFFEDC9AF)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = user.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFEDC9AF)
                )
                Text(
                    text = "${user.totalPoints} points",
                    fontSize = 16.sp,
                    color = Color(0xFFEDC9AF)
                )
            }
        }
    }
}