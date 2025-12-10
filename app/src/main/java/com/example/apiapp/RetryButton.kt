package com.example.apiapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

@Composable
fun RetryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = dimensionResource(id = R.dimen.default_padding)
    val buttonText = stringResource(id = R.string.retry_button_text)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onClick) {
            Text(buttonText)
        }
    }
}