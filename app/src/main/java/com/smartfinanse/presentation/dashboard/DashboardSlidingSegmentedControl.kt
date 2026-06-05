package com.smartfinanse.presentation.dashboard

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartfinanse.R
import com.smartfinanse.presentation.theme.SmartFinanseTheme

private data class SegmentOption(
    val filter: DashboardContentFilter,
    val labelRes: Int
)

private val segmentOptions = listOf(
    SegmentOption(DashboardContentFilter.EXPENSES_ONLY, R.string.dashboard_filter_expenses_only),
    SegmentOption(DashboardContentFilter.BOTH, R.string.dashboard_filter_both),
    SegmentOption(DashboardContentFilter.INCOME_ONLY, R.string.dashboard_filter_income_only)
)

@Composable
fun DashboardContentFilterRow(
    selected: DashboardContentFilter,
    onSelected: (DashboardContentFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val extraColors = SmartFinanseTheme.extraColors
    val selectedIndex = segmentOptions.indexOfFirst { it.filter == selected }.coerceAtLeast(0)
    val trackShape = RoundedCornerShape(percent = 50)
    val innerPadding = 4.dp
    val controlHeight = 44.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(controlHeight)
            .clip(trackShape)
            .background(extraColors.segmentTrack)
    ) {
        val segmentWidth = (maxWidth - innerPadding * 2) / segmentOptions.size
        val thumbOffset by animateDpAsState(
            targetValue = innerPadding + segmentWidth * selectedIndex,
            animationSpec = spring(stiffness = 400f, dampingRatio = 0.82f),
            label = "segment_thumb_offset"
        )

        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .padding(innerPadding)
                .clip(RoundedCornerShape(percent = 50))
                .background(extraColors.segmentThumb)
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            segmentOptions.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelected(option.filter) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(option.labelRes),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            extraColors.segmentTextSelected
                        } else {
                            extraColors.segmentTextUnselected
                        },
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
