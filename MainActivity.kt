package com.example.stationaryshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApp() }
    }
}

data class Product(val name: String, val price: Int, val image: Int)


// ================= APP =================
@Composable
fun MyApp() {
    var loggedIn by remember { mutableStateOf(false) }

    if (!loggedIn) {
        LoginScreen { loggedIn = true }
    } else {
        ShopScreen()
    }
}


// ================= LOGIN =================
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


// ================= SHOP =================
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
        "cart" -> CartScreen(cart)
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


// ================= CART =================
@Composable
fun CartScreen(cart: List<Product>) {

    var address by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("COD") }
    var goToPayment by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }

    val total = cart.sumOf { it.price }

    if (success) {
        SuccessScreen(cart)
        return
    }

    if (goToPayment) {
        PaymentScreen(onDone = { success = true })
        return
    }

    Column(modifier = Modifier.padding(10.dp)) {

        Text("🛒 Your Cart")

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cart) {
                Text("${it.name} - ₹${it.price}")
            }
        }

        Text("Total: ₹$total", color = Color.Green)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Enter Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Select Payment Method")

        Row(modifier = Modifier.clickable { payment = "COD" }) {
            RadioButton(selected = payment == "COD", onClick = { payment = "COD" })
            Text("Cash on Delivery")
        }

        Row(modifier = Modifier.clickable { payment = "ONLINE" }) {
            RadioButton(selected = payment == "ONLINE", onClick = { payment = "ONLINE" })
            Text("Online Payment")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (address.isNotEmpty()) {
                    if (payment == "COD") {
                        success = true
                    } else {
                        goToPayment = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceed")
        }
    }
}


// ================= PAYMENT =================
@Composable
fun PaymentScreen(onDone: () -> Unit) {

    var method by remember { mutableStateOf("UPI") }

    Column(modifier = Modifier.padding(20.dp)) {

        Text("💳 Payment", color = Color.Blue)

        Row(modifier = Modifier.clickable { method = "UPI" }) {
            RadioButton(selected = method == "UPI", onClick = { method = "UPI" })
            Text("UPI")
        }

        Row(modifier = Modifier.clickable { method = "CARD" }) {
            RadioButton(selected = method == "CARD", onClick = { method = "CARD" })
            Text("Card")
        }

        Row(modifier = Modifier.clickable { method = "QR" }) {
            RadioButton(selected = method == "QR", onClick = { method = "QR" })
            Text("Scan QR")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onDone() }, modifier = Modifier.fillMaxWidth()) {
            Text("Pay Now")
        }
    }
}


// ================= SUCCESS =================
@Composable
fun SuccessScreen(cart: List<Product>) {

    val total = cart.sumOf { it.price }

    Column(modifier = Modifier.padding(20.dp)) {

        Text("✅ Order Placed Successfully!", color = Color.Green)

        Spacer(modifier = Modifier.height(10.dp))

        Text("🧾 Order Summary:")

        LazyColumn {
            items(cart) {
                Text("${it.name} - ₹${it.price}")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Total Paid: ₹$total", color = Color.Blue)
    }
}