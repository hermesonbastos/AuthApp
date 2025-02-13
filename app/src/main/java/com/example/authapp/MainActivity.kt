package com.example.authapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authapp.data.AuthRepository
import com.example.authapp.ui.theme.AuthAppTheme
import com.example.authapp.ui.view.ForgotPasswordScreen
import com.example.authapp.ui.view.HomeScreen
import com.example.authapp.ui.view.LoginScreen
import com.example.authapp.ui.view.RegisterScreen
import com.example.authapp.viewmodel.AuthViewModel
import com.example.authapp.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AuthRepository()
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository)).get(AuthViewModel::class.java)

        setContent {
            AuthAppTheme {
                val navController: NavHostController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(authViewModel, navController) }
                    composable("register") { RegisterScreen(authViewModel, navController) }
                    composable("forgotPassword") { ForgotPasswordScreen(authViewModel, navController) }
                    composable("home") { HomeScreen(authViewModel, navController) }
                }
            }
        }
    }
}
