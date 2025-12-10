package com.example.apiapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = stringResource(R.string.error_icon_content_description),
            tint = colorResource(R.color.error_color),
            modifier = Modifier.padding( dimensionResource(R.dimen.default_padding))
        )

        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.headlineSmall,
            color = colorResource(R.color.error_color),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(dimensionResource(R.dimen.error_screen_horizontal_padding))
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.error_screen_spacing)))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.error_color),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.error_screen_horizontal_padding))
                .padding(dimensionResource(R.dimen.error_screen_bottom_padding))
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.default_padding))
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = stringResource(R.string.retry_icon_content_description),
                modifier = Modifier.padding(end = dimensionResource(R.dimen.error_screen_spacing))
            )
            Text(stringResource(R.string.retry))
        }
    }
}