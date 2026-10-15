package com.crossmartmm.teleshop

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crossmartmm.teleshop.ui.AdminApp
import com.crossmartmm.teleshop.ui.theme.TeleshopAdminTheme
import com.crossmartmm.teleshop.viewmodel.AdminViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge with BLACK system bars. The notch / status-bar strip is drawn as a
        // black safe zone by the root container in AdminApp, so content never conflicts
        // with the display cutout. No splash screen is used.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK)
        )
        setContent {
            TeleshopAdminTheme {
                val adminViewModel: AdminViewModel = viewModel()
                AdminApp(viewModel = adminViewModel)
            }
        }
    }
}
