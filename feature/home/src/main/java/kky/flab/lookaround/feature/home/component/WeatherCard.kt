package kky.flab.lookaround.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kky.flab.lookaround.core.domain.model.Weather
import kky.flab.lookaround.core.ui.component.LookaroundCard
import kky.flab.lookaround.feature.home.R
import kky.flab.lookaround.feature.home.model.WeatherUiState

@Composable
fun WeatherCard(
    uiState: WeatherUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    LookaroundCard(modifier = modifier) {
        when (uiState) {
            is WeatherUiState.Fail -> FailContent(
                message = uiState.message,
                modifier = Modifier.fillMaxWidth(),
                onRetry = onRetry,
            )

            WeatherUiState.Loading -> LoadingBox(modifier = Modifier.fillMaxWidth())

            is WeatherUiState.Result -> ResultContent(uiState = uiState)
        }
    }
}

@Composable
private fun ResultContent(
    uiState: WeatherUiState.Result,
    modifier: Modifier = Modifier,
) {
    val imageResId = when (uiState.data.sky) {
        Weather.Sky.SUNNY -> R.drawable.sunny
        Weather.Sky.CLOUDY -> R.drawable.cloudy
        Weather.Sky.OVERCAST -> R.drawable.overcast
        Weather.Sky.RAINY -> R.drawable.rainy
        Weather.Sky.SNOW -> R.drawable.snow
    }

    val temperatures = "${uiState.data.temperatures}도"
    val precipitation = "${uiState.data.precipitation}mm"
    val windSpeed = "${uiState.data.windSpeed}m/s"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            WeatherDataRow(title = "기온", content = temperatures)
            WeatherDataRow(title = "강수량", content = precipitation)
            WeatherDataRow(title = "풍속", content = windSpeed)
        }
    }
}

@Composable
private fun FailContent(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            shape = RoundedCornerShape(10.dp),
            onClick = onRetry,
        ) {
            Text(text = "재시도")
        }
    }
}

@Composable
private fun WeatherDataRow(
    title: String,
    content: String,
) {
    Row {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.alignByBaseline(),
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 5.dp).alignByBaseline(),
        )
    }
}

internal class WeatherCardPreviewParameter : PreviewParameterProvider<WeatherUiState> {
    override val values: Sequence<WeatherUiState>
        get() = sequenceOf(
            WeatherUiState.Fail("Preview fail message"),
            WeatherUiState.Loading,
            WeatherUiState.Result(
                Weather(
                    sky = Weather.Sky.SUNNY,
                    precipitation = 1.0,
                    windSpeed = 15,
                    temperatures = 27,
                    time = "",
                    thunderStroke = 0.0,
                )
            ),
        )
}

@Preview
@Composable
private fun WeatherCardPreview(
    @PreviewParameter(WeatherCardPreviewParameter::class) state: WeatherUiState
) {
    WeatherCard(uiState = state) {}
}
