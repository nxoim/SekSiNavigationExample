package com.dotstealab.seksinavigation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment

// This is kanged from stack overflow
@Composable
fun animateAlignmentAsState(
	targetAlignment: Alignment,
	animationSpec: AnimationSpec<Float> = spring()
): State<Alignment> {
	val biased = targetAlignment as BiasAlignment
	val horizontal by animateFloatAsState(biased.horizontalBias, animationSpec)
	val vertical by animateFloatAsState(biased.verticalBias, animationSpec)
	return derivedStateOf { BiasAlignment(horizontal, vertical) }
}

@Composable
fun animateAlignmentBiasAsState(
	targetBiasHorizontal: Float,
	targetBiasVertical: Float,
	animationSpec: AnimationSpec<Float> = spring()
): State<Alignment> {
	val biasHorizontal by animateFloatAsState(targetBiasHorizontal, animationSpec)
	val biasVertical by animateFloatAsState(targetBiasVertical, animationSpec)
	return derivedStateOf { BiasAlignment(biasHorizontal, biasVertical) }
}


@Composable
fun animateHorizontalAlignmentAsState(
	targetBiasValue: Float,
	animationSpec: AnimationSpec<Float> = spring()
): State<BiasAlignment.Horizontal> {
	val bias by animateFloatAsState(targetBiasValue, animationSpec)
	return derivedStateOf { BiasAlignment.Horizontal(bias) }
}

@Composable
fun animateVerticalAlignmentAsState(
	targetBiasValue: Float,
	animationSpec: AnimationSpec<Float> = spring()
): State<BiasAlignment.Vertical> {
	val bias by animateFloatAsState(targetBiasValue, animationSpec)
	return derivedStateOf { BiasAlignment.Vertical(bias) }
}