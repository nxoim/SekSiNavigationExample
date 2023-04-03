package com.dotstealab.seksinavigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UmWhateverThing(state: ExpandableItemsState, key: Any) {
	val originalSize = state.itemsState[key]?.originalBounds

	Box(
		Modifier
			.fillMaxSize()
			.let {
				if (state.itemsState[key]?.isExpanded == true)
					it
				else
					it.clickable { state.addToOverlayStack(key) }
			}
			.background(MaterialTheme.colorScheme.secondaryContainer)
	) {
		Column(Modifier.padding(20.dp)) {
			val animatedStatusbarPadding by animateDpAsState(
				if (state.itemsState[key]?.isExpanded ?: false)
					WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
				else 0.dp, label = ""
			)

			Spacer(modifier = Modifier.height(animatedStatusbarPadding))
			Icon(Icons.Default.AccountCircle, contentDescription = "")
//			Text(text = state.itemsState[key].animationProgress)
			Spacer(modifier = Modifier.height(30.dp))

			val uuid = "$key $key"
			ExpandableWrapper(
				modifier = Modifier
					.let {
						if (state.itemsState[uuid]?.isExpanded == true)
							it
						else
							it.clickable { state.addToOverlayStack(uuid) }
					}
					.size(150.dp),
				key = uuid,
				state = state
			) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(MaterialTheme.colorScheme.primary)
				) {
					val sjjsj = "$key $key $key"

					Column {
						val animatedStatusbarPadding by animateDpAsState(
							if (state.itemsState[uuid]?.isExpanded ?: false)
								WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
							else 0.dp, label = ""
						)

						Spacer(modifier = Modifier.height(animatedStatusbarPadding))

						Box {
							ExpandableWrapper(
								modifier = Modifier
									.let {
										if (state.itemsState[sjjsj]?.isExpanded == true)
											it
										else
											it.clickable { state.addToOverlayStack(sjjsj) }
									}
									.size(100.dp),
								key = sjjsj,
								state = state
							) {
								Box(
									modifier = Modifier
										.fillMaxSize()
										.background(MaterialTheme.colorScheme.tertiaryContainer)
								) {
									val bruh = "$key $key $key $key"

									Column {
										val animatedStatusbarPadding by animateDpAsState(
											if (state.itemsState[sjjsj]?.isExpanded ?: false)
												WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
											else 0.dp, label = ""
										)

										Spacer(modifier = Modifier.height(animatedStatusbarPadding))

										ExpandableWrapper(
											modifier = Modifier
												.let {
													if (state.itemsState[bruh]?.isExpanded == true)
														it
													else
														it.clickable { state.addToOverlayStack(bruh) }
												}
												.size(50.dp),
											key = bruh,
											state = state
										) {
											Box(modifier = Modifier
												.fillMaxSize()
												.background(MaterialTheme.colorScheme.secondaryContainer)) {

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