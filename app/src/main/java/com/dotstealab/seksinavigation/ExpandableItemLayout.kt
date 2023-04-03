package com.dotstealab.seksinavigation

import android.window.BackEvent
import android.window.OnBackAnimationCallback
import android.window.OnBackInvokedDispatcher
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.animateSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds


// display all of the composables here, as overlays. use ExpandableWrapper
// only to calculate the composables original/collapsed originalBounds and report
// its position on the screen. then display the overlayed one with the
// position and originalBounds from its related ExpandableWrapper until expanded.
// in case its expanded - switch to animated offset and originalBounds.
@Composable
fun ExpandableItemLayoutOld(
	state: ExpandableItemsState,
	onBackInvokedDispatcher: OnBackInvokedDispatcher,
	nonOverlayContent: @Composable () -> Unit
) {
	val isOverlaying = state.overlayStack.lastOrNull() != null
	val onBackPressedCallback = object: OnBackAnimationCallback {
		override fun onBackInvoked() {
			state.closeLastOverlay()
		}

		override fun onBackProgressed(backEvent: BackEvent) {
			super.onBackProgressed(backEvent)
			val key = state.overlayStack.last()
			val itemState = state.itemsState[key]

			if (itemState != null) {
				state.itemsState.replace(
					key,
					itemState.copy(
						backGestureProgress = backEvent.progress,
						backGestureSwipeEdge = backEvent.swipeEdge,
						backGestureOffset = Offset(backEvent.touchX, backEvent.touchY)
					)
				)
			}
		}
	}

	// TODO check if theres anything to go back to other than device's
	//  homescreen
	onBackInvokedDispatcher.registerOnBackInvokedCallback(
		OnBackInvokedDispatcher.PRIORITY_OVERLAY,
		onBackPressedCallback
	)

	// TODO scrim that depends on animation progress
	val baseUiScrimColor by animateColorAsState(
		if (isOverlaying)
			MaterialTheme.colorScheme.scrim.copy(0.2f)
		else
			Color.Transparent, label = ""
	)

	Box(Modifier.fillMaxSize()) {
		// display content behind the overla
		// TODO wrap in a box and apply scrim
		nonOverlayContent()

		// display the overlayed composables with the position and size
		// from its related ExpandableWrapper until expanded
		state.overlayStack.forEach { key ->
			val itemState by remember { derivedStateOf { state.itemsState[key]!! } }

			// this one is for the scrim
			val isOverlayAboveOtherOverlays = state.overlayStack.lastOrNull() == key
			val isExpanded = itemState.isExpanded
			val density = LocalDensity.current.density
			val originalSize = Size(
				(itemState.originalBounds.size.width / density),
				(itemState.originalBounds.size.height / density)
			)

			val originalOffset = itemState.originalBounds.topLeft
			var overlayRect by remember { mutableStateOf(Rect.Zero) }

			val backGestureProgress = itemState.backGestureProgress
			val backGestureSwipeEdge = itemState.backGestureSwipeEdge
			val backGestureOffset = itemState.backGestureOffset

			// there must be a way to calculate animation duration without
			// hardcoding a number
			val animationDuration = 600 // milliseconds
			val onSwipeSizeChangeExtent = 0.15f
			val onSwipeOffsetXChangeExtent = 0.1f
			val onSwipeOffsetYChangeExtent = 0.02f
			// the higher the number is - the earlier the gesture will
			// fully depend the vertical swipe offset
			val onSwipeOffsetYPrevalence = backGestureProgress * 1f


			// interpolates from 0 to 1 over the specified duration
			// into "animationProgress"
			val transition = updateTransition(targetState = isExpanded, label = "transition")

			val targetAnimationProgress = if (isExpanded) 1f else 0f
			var animationProgress by remember { mutableStateOf(0f) }
			var isAnimating by remember { mutableStateOf(true) }

			// potential solution to hardcoded animation duration
//			val isAnimating = itemState.originalBounds == overlayRect
//			var isAnimating = transition.isRunning

			// all these calculations to tune the animation
			val sizeExpandedWithSwipeProgress = Size(
				state.screenSize.width * (1f - backGestureProgress * onSwipeSizeChangeExtent),
				state.screenSize.height * (1f - backGestureProgress * onSwipeSizeChangeExtent)
			)

			val offsetExpandedWithSwipeProgress = Offset(
				if (backGestureSwipeEdge == 0)
				// if swipe is from the left side
					((state.screenSize.width * onSwipeOffsetXChangeExtent) * backGestureProgress)
				else
				// if swipe is from the right side
					(-(state.screenSize.width * onSwipeOffsetXChangeExtent) * backGestureProgress),
				(backGestureOffset.y * onSwipeOffsetYChangeExtent) * onSwipeOffsetYPrevalence
			)



			val animatedSize by transition.animateSize(
				transitionSpec = { spring(0.95f, if (isExpanded) 350f else 650f) },
				label = "animatedSizeWidth"
			) { isExpanded ->
				if (isExpanded) sizeExpandedWithSwipeProgress else originalSize
			}

			val animatedOffset by transition.animateIntOffset(
				transitionSpec = { spring(if (isExpanded) 0.95f else 0.7f, 350f) },
				label = "animatedOffsetX"
			) { isExpanded ->
				if (isExpanded) IntOffset(offsetExpandedWithSwipeProgress.x.roundToInt(), offsetExpandedWithSwipeProgress.y.roundToInt())
				else IntOffset(originalOffset.x.roundToInt(), originalOffset.y.roundToInt())
			}

			val animatedAlignmentHorizontal by transition.animateFloat(
				transitionSpec = { spring(if (isExpanded) 0.95f else 0.7f, 350f) },
				label = "animatedAlignmentHorizontal"
			) { isExpanded ->
				if (isExpanded) 0f else -1f
			}

			val animatedAlignmentVertical by transition.animateFloat(
				transitionSpec = { spring(if (isExpanded) 0.95f else 0.7f, 350f) },
				label = "animatedAlignmentVertical"
			) { isExpanded ->
				if (isExpanded) 0f else -1f
			}

			var processedOffset = if (isAnimating)
				IntOffset(animatedOffset.x, animatedOffset.y)
			else
				IntOffset(
					offsetExpandedWithSwipeProgress.x.roundToInt(),
					offsetExpandedWithSwipeProgress.y.roundToInt()
			)

			var processedSize = if (isAnimating)
				DpSize(animatedSize.width.dp, animatedSize.height.dp)
			else
				DpSize(
					sizeExpandedWithSwipeProgress.width.dp,
					sizeExpandedWithSwipeProgress.height.dp
			)

			LaunchedEffect(isAnimating) {
				if (!isAnimating) {
					processedOffset = IntOffset(offsetExpandedWithSwipeProgress.x.roundToInt(), offsetExpandedWithSwipeProgress.y.roundToInt())
					processedSize = DpSize(sizeExpandedWithSwipeProgress.width.dp, sizeExpandedWithSwipeProgress.height.dp)
				}
			}


			LaunchedEffect(Unit) {
				// display once everything is initialized
				state.itemsState.replace(
					key,
					itemState.copy(
						isExpanded = true,
						isOverlaying = true
					)
				)
			}

			// interpolating value once theres a request to change the
			// item's state
			LaunchedEffect(isExpanded) {
				if (targetAnimationProgress != animationProgress) {
					isAnimating = true
					val startTime = System.currentTimeMillis()
					var elapsedTime: Long

					val startProgress = animationProgress
					val differenceAnimation = targetAnimationProgress - startProgress
					val sign = if (differenceAnimation > 0) 1 else -1

					do {
						elapsedTime = System.currentTimeMillis() - startTime

						val progress = elapsedTime.toFloat() / animationDuration
						animationProgress = startProgress + sign * progress * abs(differenceAnimation)

						withFrameNanos {
							// Wait for the next frame to be rendered
						}
					} while (elapsedTime < animationDuration)

					isAnimating = false
					animationProgress = targetAnimationProgress
				}
			}

			// NO DONT COMBINE THESE TWO (unless you know how to
			// magically make it work)

			// when expanded changes to false - dont make the
			// original item disappear immediately, but wait for "animationDuration"
			// for the animation to finish
			LaunchedEffect(transition.currentState) {
				if (!isExpanded) {
					delay(animationDuration.milliseconds)
					state.itemsState.replace(key, itemState.copy(isOverlaying = false))
					state.overlayStack.remove(key)
				}
			}

			Box(
				Modifier
					.offset { processedOffset }
					.size(processedSize)
					.align(
						BiasAlignment(
							animatedAlignmentHorizontal,
							animatedAlignmentVertical
						)
					)
					.onGloballyPositioned { overlayRect = it.boundsInWindow() }
			) {
				// display content
				state.getContent(key)()
			}
		}
	}
}

// display all of the composables here, as overlays. use ExpandableWrapper
// only to calculate the composables original/collapsed originalBounds and report
// its position on the screen. then display the overlayed one with the
// position and originalBounds from its related ExpandableWrapper until expanded.
// in case its expanded - switch to animated offset and originalBounds.
@Composable
fun ExpandableItemLayout(
	state: ExpandableItemsState,
	onBackInvokedDispatcher: OnBackInvokedDispatcher,
	nonOverlayContent: @Composable () -> Unit
) {
	val isOverlaying = state.overlayStack.lastOrNull() != null
	val onBackPressedCallback = object: OnBackAnimationCallback {
		override fun onBackInvoked() {
			state.closeLastOverlay()
		}

		override fun onBackProgressed(backEvent: BackEvent) {
			super.onBackProgressed(backEvent)
			val key = state.overlayStack.last()
			val itemState = state.itemsState[key]

			if (itemState != null) {
				state.itemsState.replace(
					key,
					itemState.copy(
						backGestureProgress = backEvent.progress,
						backGestureSwipeEdge = backEvent.swipeEdge,
						backGestureOffset = Offset(backEvent.touchX, backEvent.touchY)
					)
				)
			}
		}
	}

	// TODO check if theres anything to go back to other than device's
	//  homescreen
	onBackInvokedDispatcher.registerOnBackInvokedCallback(
		OnBackInvokedDispatcher.PRIORITY_OVERLAY,
		onBackPressedCallback
	)

	// TODO scrim that depends on animation progress
	val baseUiScrimColor by animateColorAsState(
		if (isOverlaying)
			MaterialTheme.colorScheme.scrim.copy(0.2f)
		else
			Color.Transparent, label = ""
	)

	Box(Modifier.fillMaxSize()) {
		// display content behind the overla
		// TODO wrap in a box and apply scrim
		nonOverlayContent()

		// display the overlayed composables with the position and size
		// from its related ExpandableWrapper until expanded
		state.overlayStack.forEach { key ->
			val itemState by remember { derivedStateOf { state.itemsState[key]!! } }
			LaunchedEffect(Unit) {
				state.itemsState.replace(
					key,
					itemState.copy(
						isExpanded = true,
						isOverlaying = true
					)
				)
			}
			// this one is for the scrim
			val isOverlayAboveOtherOverlays = state.overlayStack.lastOrNull() == key
			val isExpanded = itemState.isExpanded

			val density = LocalDensity.current.density
			val originalSize = IntSize(
				(itemState.originalBounds.size.width / density).toInt(),
				(itemState.originalBounds.size.height / density).toInt()
			)
			val originalOffset = itemState.originalBounds.topLeft
			var overlayBounds by remember { mutableStateOf(Rect.Zero) }

			val backGestureProgress = itemState.backGestureProgress
			val backGestureSwipeEdge = itemState.backGestureSwipeEdge
			val backGestureOffset = itemState.backGestureOffset

			// there must be a way to calculate animation duration without
			// hardcoding a number
			val onSwipeSizeChangeExtent = 0.15f
			val onSwipeOffsetXChangeExtent = 0.1f
			val onSwipeOffsetYChangeExtent = 0.02f
			val onSwipeOffsetYPrevalence = backGestureProgress * 1f
			// the higher the number above is - the earlier the gesture will
			// fully depend the vertical swipe offset

			// interpolates from 0 to 1 over the specified duration
			// into "animationProgress"
			var animationProgress by remember { mutableStateOf(0f) }
			val sizeAnimationProgress = itemState.sizeAgainstOriginalAnimationProgress

			var isAnimating by remember { mutableStateOf(true) }

			// all these calculations to tune the animation
			val sizeExpandedWithSwipeProgress = IntSize(
				(state.screenSize.width * (1f - backGestureProgress * onSwipeSizeChangeExtent)).toInt(),
				(state.screenSize.height * (1f - backGestureProgress * onSwipeSizeChangeExtent)).toInt()
			)

			val offsetExpandedWithSwipeProgress = Offset(
				if (backGestureSwipeEdge == 0)
					// if swipe is from the left side
					((state.screenSize.width * onSwipeOffsetXChangeExtent) * backGestureProgress)
				else
					// if swipe is from the right side
					(-(state.screenSize.width * onSwipeOffsetXChangeExtent) * backGestureProgress),
				(backGestureOffset.y * onSwipeOffsetYChangeExtent) * onSwipeOffsetYPrevalence
			)

			val animatedSize by animateIntSizeAsState(
				if (isExpanded) {
					if (isAnimating) {
						state.screenSize
					} else {
						// this loads when user stops tpuching the screen
						sizeExpandedWithSwipeProgress
					}
				} else {
					originalSize
				},
				animationSpec = spring(0.95f, if (isExpanded) 350f else 650f),
				label = ""
			)

			val animatedOffset by animateOffsetAsState(
				if (isExpanded) offsetExpandedWithSwipeProgress else originalOffset,
				spring(if (isExpanded) 0.95f else 0.7f, 350f),
				label = ""
			)

			val animatedAlignment by animateAlignmentAsState(
				if (isExpanded) Alignment.Center else Alignment.TopStart,
				spring(if (isExpanded) 0.95f else 0.7f, 350f)
			)

			val processedOffset = if (isExpanded && !isAnimating) IntOffset(
				offsetExpandedWithSwipeProgress.x.roundToInt(),
				offsetExpandedWithSwipeProgress.y.roundToInt()
			) else
				IntOffset(
					animatedOffset.x.roundToInt(),
					animatedOffset.y.roundToInt()
				)

			val processedSize = if (isExpanded && !isAnimating) DpSize(
				sizeExpandedWithSwipeProgress.width.dp,
				sizeExpandedWithSwipeProgress.height.dp
			) else
				DpSize(
					animatedSize.width.dp,
					animatedSize.height.dp
				)

			LaunchedEffect(animatedOffset) {
				animationProgress = -(overlayBounds.top - itemState.originalBounds.top) / (itemState.originalBounds.top - Rect.Zero.top)
				state.setOffsetAnimationProgress(
					key,
					animationProgress
				)

				if (animationProgress == 0f) {
					// when the items are in place - wait a bit and then
					// decide that the animation is done
					// because it might cross the actual position before
					// the spring animation is done
					delay(10)
					isAnimating = false
				}

				if (!isExpanded && !isAnimating) {
					state.itemsState.replace(key, itemState.copy(isOverlaying = false))
					state.overlayStack.remove(key)
				}

				val widthScaleFraction = animatedSize.width / state.screenSize.width.toFloat()
				val heightScaleFraction = animatedSize.height / state.screenSize.height.toFloat()

				state.setScaleFraction(key, widthScaleFraction)
			}

			LaunchedEffect(animatedSize) {
				val widthForOriginalProgressCalculation = (processedSize.width.value - originalSize.width) / (state.screenSize.width - originalSize.width)
				val heightForOriginalProgressCalculation = (processedSize.height.value - originalSize.height) / (state.screenSize.height - originalSize.height)

				state.setSizeAgainstOriginalProgress(
					key,
					SizeAgainstOriginalAnimationProgress(
						widthForOriginalProgressCalculation,
						heightForOriginalProgressCalculation,
						(widthForOriginalProgressCalculation + heightForOriginalProgressCalculation) / 2
					)
				)
			}

			if (itemState.isOverlaying) {
				Box(
					Modifier
						.offset { processedOffset }
						.size(processedSize)
						.align(animatedAlignment)
						.onGloballyPositioned { overlayBounds = it.boundsInWindow() }
				) {
					// display content
					state.getContent(key)()
					Text(sizeAnimationProgress.toString(), modifier = Modifier.statusBarsPadding())
				}
			}
		}
	}
}