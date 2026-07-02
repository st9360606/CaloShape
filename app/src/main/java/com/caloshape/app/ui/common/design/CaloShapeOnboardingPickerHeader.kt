package com.caloshape.app.ui.common.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Keeps picker controls vertically stable across localized one- and two-line headings.
 *
 * The reserved height belongs to the whole heading group, so spare space stays below the
 * subtitle instead of appearing between the title and subtitle. The container may still
 * grow for unusually long translations or larger accessibility font scales.
 */
@Composable
fun CaloShapeOnboardingPickerHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = PickerHeaderMinHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 40.sp,
                color = CaloShapeOnboardingColors.title(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CaloShapeScreenFrame.contentHorizontalMedium),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = CaloShapeOnboardingColors.subtitle(),
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CaloShapeScreenFrame.onboardingSubtitleHorizontal),
                textAlign = TextAlign.Center
            )
        }
    }
}

private val PickerHeaderMinHeight = 148.dp
