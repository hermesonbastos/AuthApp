package com.example.authapp.ui.view

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.authapp.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.example.authapp.R


@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                viewModel.loginWithGoogle(idToken) { success ->
                    if (success) {
                        navController.navigate("home")
                    } else {
                        Toast.makeText(
                            context,
                            "Erro ao fazer login com Google",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Bem-vindo ao AuthApp!",
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    icon = Icons.Filled.Email
                )

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Senha",
                    icon = Icons.Filled.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        viewModel.login(email, password) { success ->
                            if (success) {
                                navController.navigate("home")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Usuário ou senha inválida",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entrar", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val signInIntent = viewModel.getGoogleSignInClient(context).signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo_google3),
                        contentDescription = "Google Login",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Entrar com Google", fontSize = 18.sp)
                }

                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Criar Conta")
                }

                TextButton(onClick = { navController.navigate("forgotPassword") }) {
                    Text("Esqueci minha senha")
                }
            }
        }
    }
}

    @Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}

