package com.dotstealab.seksinavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun UmWhateverExampleIdk(state: ExpandableItemsState, key: Any) {
	val isExpanded = state.itemsState[key]?.isExpanded ?: false
	val animationProgress = state.itemsState[key]?.sizeAgainstOriginalAnimationProgress?.combinedProgress ?: 0f

	Box(
		Modifier
			.fillMaxSize()
			.let {
				if (state.itemsState[key]?.isExpanded == true)
					it
				else
					it.clickable { state.addToOverlayStack(key) }
			}
			.background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
	) {
		Column() {
			val animatedStatusbarPadding by animateDpAsState(
				WindowInsets.statusBars.asPaddingValues().calculateTopPadding() * animationProgress,
				label = ""
			)

			Spacer(modifier = Modifier.height(animatedStatusbarPadding))

			Row(
				Modifier
					.height(64.dp)
					.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Box(
					Modifier
						.weight(1f, false)
						.padding(8.dp)
						.clip(CircleShape)
						.aspectRatio(1f)
						.background(MaterialTheme.colorScheme.secondaryContainer),
					contentAlignment = Alignment.Center
				) {
					Icon(
						Icons.Default.Person,
						contentDescription = "",
						tint = MaterialTheme.colorScheme.onSecondaryContainer,
						modifier = Modifier.size(24.dp)
					)
				}
				
				Text(
					text = "a what? bruh?",
					style = MaterialTheme.typography.headlineSmall,
					color = MaterialTheme.colorScheme.onSurface
				)
			}

			AnimatedVisibility(visible = isExpanded) {
				LazyVerticalGrid(
					columns = GridCells.Fixed(2),
					contentPadding = PaddingValues(16.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp),
					horizontalArrangement = Arrangement.spacedBy(16.dp)
				){
					repeat(100) {
						val bruh = "$it 738" // yes

						item {
							ExpandableWrapper(
								Modifier
									.height(128.dp)
									.fillMaxWidth()
									.clickable { state.addToOverlayStack(bruh) },
								key = bruh,
								state = state
							) {
								Column(
									Modifier
										.fillMaxSize()
										.background(MaterialTheme.colorScheme.tertiaryContainer)
								) {

								}
							}
						}
					}
				}
			}
		}
	}
}