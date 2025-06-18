package com.livefront.parcelableserializersample.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livefront.parcelableserializersample.sealedclass.SealedClassRoute

@Composable
fun HomeScreen(
    onSealedClassClick: (SealedClassRoute) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                onSealedClassClick(
                    SealedClassRoute.Type1
                )
            }
        ) {
            Text("Type 1 Route")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSealedClassClick(
                    SealedClassRoute.Type2(
                        data = "Type 2 Test Data"
                    )
                )
            }
        ) {
            Text("Type 2 Route")
        }
    }
}
