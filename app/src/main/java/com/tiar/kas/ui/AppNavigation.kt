package com.tiar.kas.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tiar.kas.data.AppDatabase
import kotlinx.serialization.Serializable

const val NEW_ID = -1L

@Serializable data object HomeRoute
@Serializable data class TxFormRoute(
    val editId: Long = NEW_ID,
    val payCardId: Long = NEW_ID,
    val payAmount: Long = 0L
)
@Serializable data object AccountsRoute
@Serializable data class AccountFormRoute(val editId: Long = NEW_ID)
@Serializable data object SearchRoute
@Serializable data object StatsRoute
@Serializable data object BudgetRoute
@Serializable data object TemplatesRoute
@Serializable data object RecurringRoute
@Serializable data object SecurityRoute
@Serializable data object BackupRoute
@Serializable data object MoreRoute

// satu tab di bilah bawah
private data class Tab(val route: Any, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(HomeRoute, "Transaksi", Icons.AutoMirrored.Outlined.ReceiptLong),
    Tab(StatsRoute, "Statistik", Icons.Outlined.PieChart),
    Tab(AccountsRoute, "Akun", Icons.Outlined.AccountBalanceWallet),
    Tab(MoreRoute, "Lainnya", Icons.Outlined.MoreHoriz)
)

private fun Long.asEditId(): Long? = takeIf { it != NEW_ID }

private fun NavController.safePop() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

// pindah tab tanpa menumpuk layar, posisi tiap tab disimpan dan dipulihkan
private fun NavController.navigateTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }   // buang tumpukan di atas layar awal
        launchSingleTop = true                                            // tab yang sama tidak ditumpuk
        restoreState = true                                               // kembalikan posisi terakhir tab itu
    }
}

@Composable
fun AppNavigation(db: AppDatabase) {
    val navController = rememberNavController()

    // layar yang sedang tampil, berubah setiap pindah layar
    val backStack by navController.currentBackStackEntryAsState()
    val destination = backStack?.destination
    val showBar = tabs.any { tab -> destination?.hasRoute(tab.route::class) == true }

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
                    tabs.forEach { tab ->
                        // hierarchy juga memeriksa grup navigasi di atas layar, untuk persiapan kalau nanti ada grup
                        val selected = destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)        // isi layar tidak tertutup bilah bawah
        ) {

            composable<HomeRoute> {
                HomeScreen(
                    db = db,
                    onAddClick = { navController.navigate(TxFormRoute()) },
                    onRowClick = { id -> navController.navigate(TxFormRoute(editId = id)) },
                    onManageAccounts = { navController.navigateTab(AccountsRoute) },
                    onSearch = { navController.navigate(SearchRoute) },
                    onBackup = { navController.navigate(BackupRoute) }
                )
            } // akhir HomeRoute

            composable<TxFormRoute> { entry ->
                val route = entry.toRoute<TxFormRoute>()
                TransactionFormScreen(
                    db = db,
                    editId = route.editId.asEditId(),
                    payCardId = route.payCardId.asEditId(),
                    payAmount = route.payAmount,
                    onClose = { navController.safePop() }
                )
            } // akhir TxFormRoute

            composable<AccountsRoute> {
                AccountListScreen(
                    db = db,
                    onAdd = { navController.navigate(AccountFormRoute()) },
                    onEdit = { id -> navController.navigate(AccountFormRoute(editId = id)) },
                    onPayCard = { cardId, amount ->
                        navController.navigate(TxFormRoute(payCardId = cardId, payAmount = amount))
                    }
                )
            } // akhir AccountsRoute

            composable<AccountFormRoute> { entry ->
                val route = entry.toRoute<AccountFormRoute>()
                AccountFormScreen(
                    db = db,
                    editId = route.editId.asEditId(),
                    onClose = { navController.safePop() }
                )
            } // akhir AccountFormRoute

            composable<SearchRoute> {
                SearchScreen(
                    db = db,
                    onBack = { navController.safePop() },
                    onRowClick = { id -> navController.navigate(TxFormRoute(editId = id)) }
                )
            } // akhir SearchRoute

            composable<StatsRoute> {
                StatsScreen(db = db)
            } // akhir StatsRoute

            composable<MoreRoute> {
                MoreScreen(
                    onBudget = { navController.navigate(BudgetRoute) },
                    onTemplates = { navController.navigate(TemplatesRoute) },
                    onRecurring = { navController.navigate(RecurringRoute) },
                    onSecurity = { navController.navigate(SecurityRoute) },
                    onBackup = { navController.navigate(BackupRoute) }
                )
            } // akhir MoreRoute

            composable<BudgetRoute> {
                BudgetScreen(db = db, onBack = { navController.safePop() })
            } // akhir BudgetRoute

            composable<TemplatesRoute> {
                TemplateListScreen(db = db, onBack = { navController.safePop() })
            } // akhir TemplatesRoute

            composable<RecurringRoute> {
                RecurringListScreen(db = db, onBack = { navController.safePop() })
            } // akhir RecurringRoute

            composable<SecurityRoute> {
                SecurityScreen(onBack = { navController.safePop() })
            } // akhir SecurityRoute

            composable<BackupRoute> {
                BackupScreen(db = db, onBack = { navController.safePop() })
            } // akhir BackupRoute

        } // akhir NavHost
    } // akhir Scaffold
} // akhir AppNavigation
