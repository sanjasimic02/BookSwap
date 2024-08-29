package com.example.bookswap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookswap.R
import com.example.bookswap.models.User
import com.example.bookswap.navigation.Routes
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.appComponents.UserRankingCard
import com.example.bookswap.viewModel.UserAuthViewModel

@Composable
fun LeaderboardScreen(
    userViewModel: UserAuthViewModel,
    navController: NavController
) {
    userViewModel.getAllUsersData()
    val allUsersCollection = userViewModel.allUsers.collectAsState()
    val users = remember { mutableListOf<User>() }

    LaunchedEffect(allUsersCollection?.value) {
        when (val it = allUsersCollection?.value) {
            is Resource.Success -> {
                users.clear()
                users.addAll(it.result.sortedByDescending { user -> user.totalPoints })
            }

            is Resource.Failure -> {
            }

            Resource.Loading -> {
            }

            null -> {}
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFAF3E0))
    ) {
        if (users.isEmpty()) {
            //LOADING umesto ovog!!!
            Text(
                text = "No users found",
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(16.dp)
                    //.align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFF6D4C41))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = "bookSwap",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFFEDC9AF)
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableRows,
                                contentDescription = "Table View",
                                tint = Color(0xFFEDC9AF),
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(Routes.tableScreen)
                                    }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    navController.navigate(Routes.mapScreen)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = Color(0xFF6D4C41),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.map),
                                    contentDescription = "Map Icon",
                                    tint = Color(0xFFEDC9AF),
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                            //Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "The Most Passionate Book Enthusiasts", //ili Top Book Lovers?
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(4.dp),
                    color = Color(0xFF6D4C41)
                )
                Spacer(modifier = Modifier.height(20.dp))

                users.forEachIndexed { index, user ->
                    UserRankingCard(user = user, rank = index + 1, navController, user.id)
                }
            }

        }
    }
}
