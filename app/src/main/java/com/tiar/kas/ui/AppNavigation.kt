package com.tiar.kas.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tiar.kas.data.AppDatabase
import kotlinx.serialization.Serializable

const val NEW_ID = -1L

@Serializable data object HomeRoute
@Serializable data class TxFormRoute(val editId: Long = NEW_ID)
@Serializable data object AccountsRoute
@Serializable data class AccountFormRoute(val editId: Long = NEW_ID)
@Serializable data object SearchRoute
@Serializable data object StatsRoute
@Serializable data object BudgetRoute
@Serializable data object TemplatesRoute

private fun Long.asEditId(): Long? = takeIf { it != NEW_ID }

private fun NavController.safePop() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

@Composable
fun AppNavigation(db: AppDatabase) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {

        composable<HomeRoute> {
            HomeScreen(
                db = db,
                onAddClick = { navController.navigate(TxFormRoute()) },
                onRowClick = { id -> navController.navigate(TxFormRoute(editId = id)) },
                onManageAccounts = { navController.navigate(AccountsRoute) },
                onSearch = { navController.navigate(SearchRoute) },
                onStats = { navController.navigate(StatsRoute) },
                onBudget = { navController.navigate(BudgetRoute) },
                onTemplates = { navController.navigate(TemplatesRoute) }
            )
        } // akhir HomeRoute

        composable<TxFormRoute> { entry ->
            val route = entry.toRoute<TxFormRoute>()
            TransactionFormScreen(
                db = db,
                editId = route.editId.asEditId(),
                onClose = { navController.safePop() }
            )
        } // akhir TxFormRoute

        composable<AccountsRoute> {
            AccountListScreen(
                db = db,
                onBack = { navController.safePop() },
                onAdd = { navController.navigate(AccountFormRoute()) },
                onEdit = { id -> navController.navigate(AccountFormRoute(editId = id)) }
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
                // edit dari hasil pencarian, setelah simpan kembali ke pencarian dengan filter utuh
                onRowClick = { id -> navController.navigate(TxFormRoute(editId = id)) }
            )
        } // akhir SearchRoute

        composable<StatsRoute> {
        	StatsScreen(
        		db = db,
        		onBack = { navController.safePop() }
        	)
        } // akhir StatsRoute
        composable<BudgetRoute> {
        	BudgetScreen(
        		db =db,
        		onBack = { navController.safePop() }
        	)
        } // akhir BudgetRoute
        composable<TemplatesRoute> {
        	TemplateListScreen(
        		db = db,
        		onBack = { navController.safePop() }
        	)
        } // akhir TemplatesRoute
    } // akhir NavHost
} // akhir AppNavigation
