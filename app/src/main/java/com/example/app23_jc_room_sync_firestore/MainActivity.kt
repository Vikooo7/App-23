package com.example.app23_jc_room_sync_firestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.app23_jc_room_sync_firestore.ui.theme.App23_jc_room_sync_firestoreTheme
import com.example.app23_jc_room_sync_firestore.view.PantallaPedidos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Revisa el nombre real del tema en ui/theme/Theme.kt
            App23_jc_room_sync_firestoreTheme {
                PantallaPedidos()
            }
        }
    }
}
