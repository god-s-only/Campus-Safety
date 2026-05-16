package com.caleb.campussafety.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.caleb.campussafety.auth.presentation.login.LoginScreen
import com.caleb.campussafety.auth.presentation.login.LoginViewModel
import com.caleb.campussafety.auth.presentation.register.RegisterScreen
import com.caleb.campussafety.auth.presentation.register.RegisterViewModel
import com.caleb.campussafety.dashboard.presentation.dashboard.DashboardScreen
import com.caleb.campussafety.dashboard.presentation.dashboard.DashboardViewModel
import com.caleb.campussafety.report.presentation.detail.IncidentDetailScreen
import com.caleb.campussafety.report.presentation.detail.IncidentDetailViewModel
import com.caleb.campussafety.report.presentation.history.HistoryScreen
import com.caleb.campussafety.report.presentation.history.HistoryViewModel
import com.caleb.campussafety.report.presentation.home.StudentHomeScreen
import com.caleb.campussafety.report.presentation.home.StudentHomeViewModel
import com.caleb.campussafety.report.presentation.report.ReportScreen
import com.caleb.campussafety.report.presentation.report.ReportViewModel
import com.caleb.campussafety.splash.SplashScreen
import com.caleb.campussafety.splash.SplashViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object StudentHome : Screen("student_home")
    object SecurityDashboard : Screen("security_dashboard")
    object Report : Screen("report")
    object History : Screen("history")
    object IncidentDetail : Screen("incident_detail/{incidentId}") {
        fun createRoute(incidentId: String) = "incident_detail/$incidentId"
    }
    object Splash : Screen("splash")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            LoginScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateToHome = { isSecurityOfficer ->
                    val destination = if (isSecurityOfficer)
                        Screen.SecurityDashboard.route
                    else
                        Screen.StudentHome.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            RegisterScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateToHome = { isSecurityOfficer ->
                    val destination = if (isSecurityOfficer)
                        Screen.SecurityDashboard.route
                    else
                        Screen.StudentHome.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.StudentHome.route) {
            val viewModel: StudentHomeViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            StudentHomeScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateToReport = {
                    navController.navigate(Screen.Report.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToIncidentDetail = { incidentId ->
                    navController.navigate(
                        Screen.IncidentDetail.createRoute(incidentId)
                    )
                }
            )
        }

        composable(Screen.SecurityDashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            DashboardScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateToIncidentDetail = { incidentId ->
                    navController.navigate(
                        Screen.IncidentDetail.createRoute(incidentId)
                    )
                }
            )
        }

        composable(Screen.Report.route) {
            val viewModel: ReportViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            ReportScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onReportSubmitted = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.route) {
            val viewModel: HistoryViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            HistoryScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToIncidentDetail = { incidentId ->
                    navController.navigate(
                        Screen.IncidentDetail.createRoute(incidentId)
                    )
                }
            )
        }

        composable(
            route = Screen.IncidentDetail.route,
            arguments = listOf(
                navArgument("incidentId") { type = NavType.StringType }
            )
        ) {
            val viewModel: IncidentDetailViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            IncidentDetailScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                actions = viewModel.actions,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = { isSecurityOfficer ->
                    val destination = if (isSecurityOfficer)
                        Screen.SecurityDashboard.route
                    else
                        Screen.StudentHome.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

    }
}