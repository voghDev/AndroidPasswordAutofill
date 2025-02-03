package es.voghdev.androidpasswordautofill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPassword
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.PasswordCredential
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.voghdev.androidpasswordautofill.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        credentialManager = CredentialManager.create(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        var username by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }

                        Title(
                            name = "Login form",
                            modifier = Modifier.padding(innerPadding)
                        )
                        UsernameField(username) {
                            username = it
                        }
                        PasswordField(password) {
                            password = it
                        }
                        SubmitButton(username, password) {
                            // Save credentials after successful login
                            saveCredentials(username, password)
                        }

                        Button(
                            onClick = { getCredentials { u, p -> 
                                username = u
                                password = p
                            } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("Use saved credentials")
                        }
                    }
                }
            }
        }
    }

    private fun saveCredentials(username: String, password: String) {
        lifecycleScope.launch {
            try {
                credentialManager.createCredential(
                    request = CreateCredentialRequest(
                        CreatePasswordRequest(
                            id = username,
                            password = password
                        )
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getCredentials(onCredentialsReceived: (String, String) -> Unit) {
        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = GetCredentialRequest(
                        listOf(
                            GetPassword()
                        )
                    )
                )
                
                when (result) {
                    is GetCredentialResponse -> {
                        val credential = result.credential
                        if (credential is PasswordCredential) {
                            onCredentialsReceived(credential.id, credential.password)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun Title(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "**$name**",
        modifier = modifier
    )
}

@Composable
fun UsernameField(username: String, onValueChange: (String) -> Unit) {
    TextField(
        value = username,
        onValueChange = { onValueChange.invoke(it) },
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun PasswordField(password: String, onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }
    TextField(
        value = password,
        onValueChange = { onValueChange.invoke(it) },
        label = { Text("Password") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "Toggle password visibility")
            }
        }
    )
}

@Composable
fun SubmitButton(
    username: String,
    password: String,
    onSubmit: () -> Unit
) {
    Button(
        onClick = { onSubmit() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        enabled = username.isNotEmpty() && password.isNotEmpty()
    ) {
        Text("Submit")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Title("Login Form")
    }
}
