package com.example.stationaryshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MyApp()
            }
        }
    }
}

data class Product(val name: String, val price: Int, val image: Int)
data class Order(
    val items: List<Product>,
    val total: Int,
    val paymentMethod: String
)
@Composable
fun MyApp() {

    var screen by remember { mutableStateOf("welcome") }
    val cart = remember { mutableStateListOf<Product>() }
    val orders = remember { mutableStateListOf<Order>() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        when (screen) {
            "welcome" -> WelcomeScreen { screen = "login" }
            "login" -> LoginScreen { screen = "shop" }
            "shop" -> ShopScreen(cart, orders) { screen = it }
            "cart" -> CartScreen(cart, orders) { screen = it }
            "checkout" -> CheckoutScreen(cart, orders) { screen = it }
            "payment" -> PaymentScreen(cart, orders) { screen = it }
            "success" -> SuccessScreen(cart, orders) { screen = "shop" }
            "profile" -> ProfileScreen(orders) { screen = it }
            "orders" -> OrderHistoryScreen(orders) { screen = "profile" }
        }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛍️ Siddhivinayak Stationary", color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onStart) { Text("Start Shopping") }
        }
    }
}


@Composable
fun LoginScreen(onLogin: () -> Unit) {

    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(Modifier.padding(20.dp)) {
        Text("Login")

        OutlinedTextField(user, { user = it }, label = { Text("Username") })
        OutlinedTextField(pass, { pass = it }, label = { Text("Password") })

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            if (user.isNotEmpty() && pass.isNotEmpty()) onLogin()
        }) {
            Text("Login")
        }
    }
}

@Composable
fun ShopScreen(
    cart: MutableList<Product>,
    orders: List<Order>,
    screenChange: (String) -> Unit
) {

    val products = listOf(
        Product("Notebook", 50, R.drawable.notebook),
        Product("Pen", 10, R.drawable.pen),
        Product("Pencil", 5, R.drawable.pencil),
        Product("Eraser", 3, R.drawable.eraser),
        Product("Scale", 15, R.drawable.scale),
        Product("Marker", 30, R.drawable.markerpen)
    )

    Column {

        Row(
            Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Shop")

            Row {
                Icon(Icons.Default.Person, null,
                    modifier = Modifier.clickable { screenChange("profile") })

                Spacer(modifier = Modifier.width(15.dp))

                Icon(Icons.Default.ShoppingCart, null,
                    modifier = Modifier.clickable { screenChange("cart") })
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(products) { product ->
                Card(Modifier.padding(8.dp)) {
                    Column(Modifier.padding(10.dp)) {

                        Image(
                            painterResource(product.image),
                            null,
                            modifier = Modifier.height(100.dp)
                        )

                        Text(product.name)
                        Text("₹${product.price}", color = Color.Green)

                        Button(onClick = { cart.add(product) }) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    cart: MutableList<Product>,
    orders: MutableList<Order>,
    screenChange: (String) -> Unit
) {

    val total = cart.sumOf { it.price }

    Column(Modifier.padding(10.dp)) {

        Text("🛒 Cart")

        LazyColumn {
            items(cart) {
                Text("${it.name} - ₹${it.price}")
            }
        }

        Text("Total: ₹$total", color = Color.Green)

        Button(
            onClick = { screenChange("checkout") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Now")
        }
    }
}

@Composable
fun CheckoutScreen(
    cart: List<Product>,
    orders: MutableList<Order>,
    screenChange: (String) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(Modifier.padding(20.dp)) {

        Text("Checkout")

        OutlinedTextField(name, { name = it }, label = { Text("Full Name") })
        OutlinedTextField(address, { address = it }, label = { Text("Address") })
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone (10 digits)") })

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            if (phone.length == 10) {
                screenChange("payment")
            }
        }) {
            Text("Continue to Payment")
        }
    }
}

@Composable
fun PaymentScreen(
    cart: MutableList<Product>,
    orders: MutableList<Order>,
    screenChange: (String) -> Unit
) {

    var method by remember { mutableStateOf("UPI") }
    var input by remember { mutableStateOf("") }

    Column(Modifier.padding(20.dp)) {

        Text("💳 Payment", style = MaterialTheme.typography.titleLarge)

        // UPI
        Row(Modifier.clickable { method = "UPI"; input = "" }) {
            RadioButton(method == "UPI", onClick = { method = "UPI"; input = "" })
            Text("UPI")
        }

        // Debit
        Row(Modifier.clickable { method = "DEBIT"; input = "" }) {
            RadioButton(method == "DEBIT", onClick = { method = "DEBIT"; input = "" })
            Text("Debit Card")
        }

        // Credit
        Row(Modifier.clickable { method = "CREDIT"; input = "" }) {
            RadioButton(method == "CREDIT", onClick = { method = "CREDIT"; input = "" })
            Text("Credit Card")
        }

        // COD
        Row(Modifier.clickable { method = "COD"; input = "" }) {
            RadioButton(method == "COD", onClick = { method = "COD"; input = "" })
            Text("Cash on Delivery")
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 🔥 Dynamic Input Field
        when (method) {

            "UPI" -> {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Enter UPI ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "DEBIT" -> {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Enter Debit Card Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "CREDIT" -> {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Enter Credit Card Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "COD" -> {
                Text("💵 Pay when order is delivered", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                // Validation
                if (method != "COD" && input.isEmpty()) return@Button

                val total = cart.sumOf { it.price }

                orders.add(
                    Order(
                        items = cart.toList(),
                        total = total,
                        paymentMethod = method
                    )
                )

                cart.clear()
                screenChange("success")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order")
        }
    }
}

@Composable
fun SuccessScreen(
    cart: List<Product>,
    orders: List<Order>,
    back: () -> Unit
) {
    Column(Modifier.padding(20.dp)) {
        Text("✅ Order Placed Successfully!", color = Color.Green)

        Button(onClick = back) {
            Text("Back to Shop")
        }
    }
}

@Composable
fun ProfileScreen(
    orders: List<Order>,
    screenChange: (String) -> Unit
) {

    Column(Modifier.padding(20.dp)) {

        Text("👤 My Account", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(10.dp))

        // Dummy user info (you can connect later)
        Text("Name: Priyanshu")
        Text("Phone: 9876543210")
        Text("Address: Mumbai")

        Spacer(modifier = Modifier.height(20.dp))

        // 🔥 My Orders Button
        Button(
            onClick = { screenChange("orders") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📦 My Orders")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { screenChange("shop") }) {
            Text("Back")
        }
    }
}

@Composable
fun OrderHistoryScreen(
    orders: List<Order>,
    back: () -> Unit
) {

    Column(Modifier.padding(20.dp)) {

        Text("📦 My Orders", style = MaterialTheme.typography.titleLarge)

        LazyColumn {

            items(orders) { order ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {

                    Column(Modifier.padding(10.dp)) {

                        Text("🧾 Items:")

                        order.items.forEach {
                            Text("• ${it.name} - ₹${it.price}")
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Text("💰 Total: ₹${order.total}", color = Color.Green)
                        Text("💳 Payment: ${order.paymentMethod}", color = Color.Blue)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = back) {
            Text("Back")
        }
    }
}