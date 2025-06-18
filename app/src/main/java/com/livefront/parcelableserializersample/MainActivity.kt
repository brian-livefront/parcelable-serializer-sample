package com.livefront.parcelableserializersample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.livefront.parcelableserializersample.home.HomeRoute
import com.livefront.parcelableserializersample.home.homeDestination
import com.livefront.parcelableserializersample.sealedclass.navigateToSealedClass
import com.livefront.parcelableserializersample.sealedclass.sealedClassDestinationType1
import com.livefront.parcelableserializersample.sealedclass.sealedClassDestinationType2
import com.livefront.parcelableserializersample.ui.theme.ParcelableSerializerSampleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParcelableSerializerSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        homeDestination(
                            onSealedClassClick = { sealedClass ->
                                navController.navigateToSealedClass(sealedClass)
                            }
                        )
                        sealedClassDestinationType1()
                        sealedClassDestinationType2()
                    }
                }
            }
        }
    }
}
