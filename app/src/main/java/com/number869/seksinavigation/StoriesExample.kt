package com.number869.seksinavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun StoriesExample(state: OverlayLayoutState, key: String) {
	val isExpanded by remember { derivedStateOf { state.getIsExpanded(key) } }

	// this means the image will be 9:16 when expanded
	val animatedAspectRatio by animateFloatAsState(if (isExpanded) 0.5625f else 1f)
	val animatedShape by animateDpAsState(if (isExpanded) 0.dp else 160.dp)

	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Image(
			painter = painterResource(id = R.drawable.ic_launcher_background),
			contentDescription = "story image",
			modifier = Modifier
				.let { return@let if (isExpanded) it.fillMaxSize() else it.size(64.dp) }
				.aspectRatio(animatedAspectRatio)
				.clip(RoundedCornerShape(animatedShape)),
			contentScale = ContentScale.FillBounds
		)
		
		AnimatedVisibility(visible = !isExpanded) {
			Text(text = key)
		}
	}
}