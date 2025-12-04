package com.enigmasolver.phonebook.shared.router

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.enigmasolver.phonebook.features.contacts.presentation.ContactsScreen

@kotlinx.serialization.Serializable
object ContactListRoute

//@kotlinx.serialization.Serializable
//object AddContactRoute
//@Serializable
//data class UserDetailRoute(val id: String)

@Composable
fun AppRouter() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ContactListRoute
    ) {
        composable<ContactListRoute> {
            ContactsScreen()
        }
//        composable<AddContactRoute> {
//            AddContactBottomSheet(
//                // Pass navigation callback (don't pass navController to screens!)
////                onUserClick = {
//////                    userId ->
//////                    navController.navigate(UserDetailRoute(id = userId))
////                }
//            )
//        }
//
//        // Route 2: Detail Screen
//        composable<UserDetailRoute> { backStackEntry ->
//            // Extract type-safe args automatically
//            val route: UserDetailRoute = backStackEntry.toRoute()
//
//            // Placeholder for your Detail Screen
//            // UserDetailScreen(userId = route.id)
//        }
    }
}