package com.ranjan.malav.morselight_flashlightwithmorsecode.ui

import androidx.core.net.toUri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ranjan.malav.morselight_flashlightwithmorsecode.R
import com.ranjan.malav.morselight_flashlightwithmorsecode.app.AppContainer
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.components.Eyebrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.DecodingDrillScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.DecodingDrillViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.SendingDrillScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.SendingDrillViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.MoreScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.MoreViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.ReceiveScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.ReceiveViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.ReferenceChartScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.SendScreen
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.screens.SendViewModel
import com.ranjan.malav.morselight_flashlightwithmorsecode.ui.theme.MorseTheme
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.launchWeb
import com.ranjan.malav.morselight_flashlightwithmorsecode.utils.rateApp

private sealed class Dest(val route: String, @StringRes val titleRes: Int) {
    data object Send : Dest("send", R.string.nav_send)
    data object Receive : Dest("receive", R.string.nav_receive)
    data object More : Dest("more", R.string.nav_more)
    data object Chart : Dest("chart", R.string.more_reference_chart)
    data object DecodingDrill : Dest("ddrill", R.string.more_decoding_drill)
    data object SendingDrill : Dest("sdrill", R.string.more_sending_drill)
}

private data class Tab(val dest: Dest, val icon: ImageVector)
private val tabs = listOf(
    Tab(Dest.Send, Icons.Outlined.FlashlightOn),
    Tab(Dest.Receive, Icons.Outlined.Sensors),
    Tab(Dest.More, Icons.Outlined.MoreHoriz),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseApp(container: AppContainer) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val current = tabs.firstOrNull { it.dest.route == route }?.dest
        ?: when (route) {
            Dest.Chart.route -> Dest.Chart
            Dest.DecodingDrill.route -> Dest.DecodingDrill
            Dest.SendingDrill.route -> Dest.SendingDrill
            else -> Dest.Send
        }
    val isTab = tabs.any { it.dest.route == route }
    val c = MorseTheme.colors

    Scaffold(
        containerColor = c.bgApp,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Eyebrow(stringResource(R.string.eyebrow_app))
                        Text(stringResource(current.titleRes), style = androidx.compose.material3.MaterialTheme.typography.headlineMedium, color = c.textHeading)
                    }
                },
                navigationIcon = {
                    if (!isTab) IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.action_back), tint = c.textBody)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = c.bgApp),
            )
        },
        bottomBar = {
            if (isTab) NavigationBar(
                containerColor = c.surfaceCard,
                modifier = Modifier.fillMaxWidth().border(
                    androidx.compose.foundation.BorderStroke(1.dp, c.borderSubtle),
                ),
            ) {
                tabs.forEach { tab ->
                    val selected = current.route == tab.dest.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(tab.dest.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, stringResource(tab.dest.titleRes)) },
                        label = { Text(stringResource(tab.dest.titleRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = c.accent, selectedTextColor = c.accent,
                            unselectedIconColor = c.textSubtle, unselectedTextColor = c.textSubtle,
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        val ctx = LocalContext.current
        NavHost(nav, startDestination = Dest.Send.route, modifier = Modifier.fillMaxSize().padding(padding)) {
            composable(Dest.Send.route) {
                val vm: SendViewModel = viewModel(factory = viewModelFactory {
                    initializer { SendViewModel(container.torch, container.settings) }
                })
                SendScreen(vm)
            }
            composable(Dest.Receive.route) {
                val vm: ReceiveViewModel = viewModel(factory = viewModelFactory {
                    initializer { ReceiveViewModel(container.torch, container.settings) }
                })
                ReceiveScreen(vm, container.torch)
            }
            composable(Dest.More.route) {
                val vm: MoreViewModel = viewModel(factory = viewModelFactory {
                    initializer { MoreViewModel(container.settings) }
                })
                MoreScreen(
                    vm = vm,
                    onOpenDecodingDrill = { nav.navigate(Dest.DecodingDrill.route) },
                    onOpenSendingDrill = { nav.navigate(Dest.SendingDrill.route) },
                    onOpenReferenceChart = { nav.navigate(Dest.Chart.route) },
                    onRate = { ctx.rateApp() },
                    onSource = { ctx.launchWeb("https://github.com/ranjan-malav/MorseLight".toUri()) },
                    onDonate = { ctx.launchWeb("https://ko-fi.com/ranjan".toUri()) },
                )
            }
            composable(Dest.Chart.route) { ReferenceChartScreen() }
            composable(Dest.DecodingDrill.route) {
                val vm: DecodingDrillViewModel = viewModel()
                DecodingDrillScreen(vm)
            }
            composable(Dest.SendingDrill.route) {
                val vm: SendingDrillViewModel = viewModel()
                SendingDrillScreen(vm)
            }
        }
    }
}
