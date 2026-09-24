package com.ecotrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ecotrack.app.ui.theme.ImpactHigh
import com.ecotrack.app.ui.theme.ImpactLow
import com.ecotrack.app.ui.theme.ImpactMedium

/** Small coloured pill showing High / Medium / Low, used consistently on Logs + Dashboard. */
@Composable
fun ImpactBadge(impactScore: String, modifier: Modifier = Modifier) {
    val color = when (impactScore) {
        "High" -> ImpactHigh
        "Medium" -> ImpactMedium
        else -> ImpactLow
    }
    Text(
        text = impactScore,
        color = Color.White,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
