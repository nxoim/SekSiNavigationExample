package com.number869.seksinavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.number869.seksinavigation.ui.theme.SekSiNavigationTheme


class MainActivity : ComponentActivity() {
	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val state = rememberOverlayLayoutState()

			SekSiNavigationTheme {
				Surface {
					OverlayLayout(state, this) {
						Scaffold(
							topBar = {
								CenterAlignedTopAppBar(
									title = { Text(text = "Sek Si Navigation Demo") } ,
									windowInsets = WindowInsets.statusBars
								)
							},
							bottomBar = { BottomBar() }
						) { scaffoldPadding ->
							Surface(Modifier.padding(scaffoldPadding)) {
								LazyColumn(
									contentPadding = PaddingValues(horizontal = 16.dp),
									verticalArrangement = Arrangement.spacedBy(8.dp)
								) {
									repeat(100) {
										item {
											val key = it.toString()

											OverlayItemWrapper(
												Modifier
													.clickable { state.addToOverlayStack(key) }
													.height(64.dp)
													.fillMaxWidth(),
												DpSize(400.dp, 500.dp),
												originalCornerRadius = 16.dp,
												key = key,
												state = state
											) {
												UmWhateverExampleIdk(state, key)
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

@Composable
fun BottomBar() {
	NavigationBar(windowInsets = WindowInsets.navigationBars) {
		NavigationBarItem(
			selected = true,
			onClick = { /*TODO*/ },
			icon = {
				Icon(Icons.Default.Home, contentDescription = "")
			},
			label = {
				Text(text = "Home")
			}
		)
		NavigationBarItem(
			selected = false,
			onClick = { /*TODO*/ },
			icon = {
				Icon(Icons.Default.FavoriteBorder, contentDescription = "")
			},
			label = {
				Text(text = "Favourites")
			}
		)
	}
}
