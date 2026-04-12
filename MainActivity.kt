package com.example.stationaryshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApp() }
    }
}

data class Product(val name: String, val price: Int, val image: Int)

@Composable
fun MyApp() {
    var loggedIn by remember { mutableStateOf(false) }

    if (!loggedIn) {
        LoginScreen { loggedIn = true }
    } else {
        ShopScreen()
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(20.dp)) {

        Text("Siddhivinayak Stationary Shop", color = Color.Red)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (username == "admin" && password == "1234") {
                onLogin()
            }
        }) {
            Text("Login")
        }
    }
}

@Composable
fun ShopScreen() {

    val products = listOf(
        Product("Notebook", 50, R.drawable.notebook),
        Product("Blue Pen", 10, R.drawable.pen),
        Product("Pencil", 5, R.drawable.pencil),
        Product("Eraser", 3, R.drawable.eraser),
        Product("Scale", 15, R.drawable.scale),
        Product("Geometry Box", 120, R.drawable.geometrybox),
        Product("Highlighter", 25, R.drawable.highlighter),
        Product("Marker", 30, R.drawable.markerpen),
        Product("Glue Stick", 20, R.drawable.glue),
        Product("File Folder", 40, R.drawable.folderfile)
    )

    val cart = remember { mutableStateListOf<Product>() }
    var screen by remember { mutableStateOf("shop") }

    when (screen) {
        "shop" -> ShopUI(products, cart) { screen = "cart" }
        "cart" -> CartScreen(cart) { screen = "shop" }
    }
}

@Composable
fun ShopUI(products: List<Product>, cart: MutableList<Product>, goToCart: () -> Unit) {

    Column(modifier = Modifier.padding(10.dp)) {

        Text("🛒 Stationary Shop", color = Color.Red)

        Button(onClick = goToCart) {
            Text("Go To Cart (${cart.size})")
        }

        LazyColumn {
            items(products) { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {

                    Image(
                        painter = painterResource(id = product.image),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name)
                        Text("₹${product.price}", color = Color.Green)
                    }

                    Button(onClick = { cart.add(product) }) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(cart: MutableList<Product>, goBack: () -> Unit) {

    var address by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("COD") }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        OrderSuccess()
        return
    }

    val total = cart.sumOf { it.price }

    Column(modifier = Modifier.padding(10.dp)) {

        Text("🛒 Your Cart", color = Color.Red)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cart) { product ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {

                    Text(product.name, modifier = Modifier.weight(1f))
                    Text("₹${product.price}")

                    Button(onClick = { cart.remove(product) }) {
                        Text("Remove")
                    }
                }
            }
        }

        Text("Total: ₹$total", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Enter Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Payment Method")

        Row {
            RadioButton(selected = payment == "COD", onClick = { payment = "COD" })
            Text("COD")

            Spacer(modifier = Modifier.width(20.dp))

            RadioButton(selected = payment == "ONLINE", onClick = { payment = "ONLINE" })
            Text("Online")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (address.isNotEmpty() && cart.isNotEmpty()) {
                    showSuccess = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order")
        }

        Button(onClick = goBack) {
            Text("Back to Shop")
        }
    }
}

@Composable
fun OrderSuccess() {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("✅ Order Successful!", color = Color.Green)
        Text("Thank you for shopping 🙏")
    }
}