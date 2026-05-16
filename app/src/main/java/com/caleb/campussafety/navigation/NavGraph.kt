package com.caleb.campussafety.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.caleb.campussafety.auth.presentation.login.LoginScreen
import com.caleb.campussafety.auth.presentation.login.LoginViewModel
import com.caleb.campussafety.auth.presentation.register.RegisterScreen
import com.caleb.campussafety.auth.presentation.register.RegisterViewModel
import com.caleb.campussafety.report.presentation.home.StudentHomeScreen
import com.caleb.campussafety.report.presentation.home.StudentHomeViewModel
import com.caleb.campussafety.report.presentation.report.ReportScreen
import com.caleb.campussafety.report.presentation.report.ReportViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object StudentHome : Screen("student_home")
    object Report : Screen("report")
    object History : Screen("history")
    object IncidentDetail : Screen("incident_detail/{incidentId}") {
        fun createRoute(incidentId: String) = "incident_detail/$incidentId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            LoginScreen(
                state = state,
                actions = viewModel.actions,
                onEvent = viewModel::onEvent,
                onNavigateToHome = {
                    navController.navigate(Screen.StudentHome.route) {
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
                onNavigateToHome = {
                    navController.navigate(Screen.StudentHome.route) {
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
    }
}