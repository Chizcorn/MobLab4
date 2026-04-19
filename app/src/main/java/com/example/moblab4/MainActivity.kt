package com.example.moblab4

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DetroitViewModel

    override fun attachBaseContext(newBase: Context?) {
        val prefs = newBase?.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val lang = prefs?.getString("language", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase ?: this, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, DetroitViewModelFactory(application))
            .get(DetroitViewModel::class.java)

        setContent {
            val darkMode by viewModel.isDarkMode.collectAsState()
            AppTheme(darkTheme = darkMode) {
                DetroitCityApp(viewModel, ::applyLanguage)
            }
        }
    }

    fun applyLanguage(languageCode: String) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putString("language", languageCode).apply()
        recreate()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetroitCityApp(viewModel: DetroitViewModel, onLanguageChange: (String) -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        val drawerItem = when {
            currentRoute == DrawerItem.Categories.route ||
                    currentRoute?.startsWith("places/") == true ||
                    currentRoute?.startsWith("details/") == true -> DrawerItem.Categories
            currentRoute == DrawerItem.About.route -> DrawerItem.About
            currentRoute == DrawerItem.Settings.route -> DrawerItem.Settings
            else -> null
        }
        if (drawerItem != null && viewModel.uiState.value.selectedDrawerItem != drawerItem) {
            viewModel.selectDrawerItem(drawerItem)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    viewModel = viewModel,
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope,
                    currentRoute = currentRoute
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { innerPadding ->
            DetroitNavGraph(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                viewModel = viewModel,
                onLanguageChange = onLanguageChange
            )
        }
    }
}

@Composable
fun DrawerContent(
    viewModel: DetroitViewModel,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentRoute: String?
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.drawer_header_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        Divider()
        DrawerItemRow(
            item = DrawerItem.Categories,
            selected = viewModel.uiState.collectAsState().value.selectedDrawerItem == DrawerItem.Categories
        ) {
            viewModel.selectDrawerItem(DrawerItem.Categories)
            scope.launch { drawerState.close() }
            navController.navigate(DrawerItem.Categories.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
        DrawerItemRow(
            item = DrawerItem.About,
            selected = viewModel.uiState.collectAsState().value.selectedDrawerItem == DrawerItem.About
        ) {
            viewModel.selectDrawerItem(DrawerItem.About)
            scope.launch { drawerState.close() }
            if (currentRoute != DrawerItem.About.route) {
                navController.navigate(DrawerItem.About.route) {
                    launchSingleTop = true
                }
            }
        }
        DrawerItemRow(
            item = DrawerItem.Settings,
            selected = viewModel.uiState.collectAsState().value.selectedDrawerItem == DrawerItem.Settings
        ) {
            viewModel.selectDrawerItem(DrawerItem.Settings)
            scope.launch { drawerState.close() }
            if (currentRoute != DrawerItem.Settings.route) {
                navController.navigate(DrawerItem.Settings.route) {
                    launchSingleTop = true
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerItemRow(item: DrawerItem, selected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(stringResource(item.titleResId)) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}