package com.example.moblab4

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun DetroitNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: DetroitViewModel,
    onLanguageChange: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = DrawerItem.Categories.route,
        modifier = modifier
    ) {
        composable(DrawerItem.Categories.route) {
            CategoriesScreen(
                onCategoryClick = { categoryResId ->
                    navController.navigate("places/$categoryResId")
                }
            )
        }

        composable(
            route = "places/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: R.string.category_coffee
            PlacesScreen(
                categoryId = categoryId,
                onPlaceClick = { placeId ->
                    navController.navigate("details/$placeId")
                },
                navController = navController
            )
        }

        composable(
            route = "details/{placeId}",
            arguments = listOf(navArgument("placeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getInt("placeId") ?: 1
            DetailsScreen(placeId = placeId, navController = navController)
        }

        composable(DrawerItem.About.route) {
            AboutScreen()
        }

        composable(DrawerItem.Settings.route) {
            SettingsScreen(viewModel, onLanguageChange)
        }
    }
}