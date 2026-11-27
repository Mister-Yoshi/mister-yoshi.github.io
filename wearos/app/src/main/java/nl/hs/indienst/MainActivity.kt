package nl.hs.indienst

import android.app.RemoteInput
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.input.RemoteInputIntentHelper
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

val HsRed = Color(0xFFFF4136)
val HsBlue = Color(0xFF4D7CFF)
val HsGrey = Color(0xFFA5A8AB)
val HsYellow = Color(0xFFFFC917)
val HsGreen = Color(0xFF1FD38A)
val HsCard = Color(0xFF141B2E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = Store(this)
        setContent {
            MaterialTheme(colors = Colors(primary = HsBlue, secondary = HsYellow, error = HsRed, onPrimary = Color.White, onSecondary = Color.Black)) {
                App(store)
            }
        }
    }
}

enum class Screen { HOME, STATION, TIME }

@Composable
fun HsLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 40.dp, height = 25.dp)) {
        val u = size.width / 48f
        val cols = listOf(Color(0xFFE8213F), Color(0xFF4D82E0), Color(0xFF9AA2AD))
        cols.forEachIndexed { i, c ->
            val x = i * 14f
            val p = Path().apply {
                moveTo((x + 10f) * u, 2f * u); lineTo((x + 20f) * u, 2f * u)
                lineTo((x + 12f) * u, 28f * u); lineTo((x + 2f) * u, 28f * u); close()
            }
            drawPath(p, c)
        }
    }
}

@Composable
fun App(store: Store) {
    var d by remember { mutableStateOf(store.load()) }
    var screen by remember { mutableStateOf(Screen.HOME) }
    var station by remember { mutableStateOf("") }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) { now = System.currentTimeMillis(); d = store.load(); delay(1000) }
    }

    if (d.on) {
        DutyScreen(d, now, store) { d = it }
    } else when (screen) {
        Screen.HOME -> HomeScreen(d) { screen = Screen.STATION }
        Screen.STATION -> {
            BackHandler { screen = Screen.HOME }
            StationScreen(store) { station = it; screen = Screen.TIME }
        }
        Screen.TIME -> {
            BackHandler { screen = Screen.STATION }
            TimeScreen(station, now) { at -> d = store.start(station, at); screen = Screen.HOME }
        }
    }
}

@Composable
fun Page(content: androidx.wear.compose.foundation.lazy.ScalingLazyListScope.() -> Unit) {
    val state = rememberScalingLazyListState()
    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = state) },
    ) {
        ScalingLazyColumn(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            content = content,
        )
    }
}

@Composable
fun HomeScreen(d: Dienst, onIn: () -> Unit) {
    Page {
        item { HsLogo(Modifier.padding(top = 8.dp)) }
        item { Text(stringResource(R.string.off_duty), style = MaterialTheme.typography.title3, color = HsGrey) }
        item {
            Chip(
                onClick = onIn,
                label = { Text(stringResource(R.string.report_in), fontWeight = FontWeight.Bold) },
                colors = ChipDefaults.primaryChipColors(backgroundColor = HsBlue),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (d.lastSummary.isNotBlank()) {
            item { Text(stringResource(R.string.last_shift), style = MaterialTheme.typography.caption2, color = HsGrey, modifier = Modifier.padding(top = 6.dp)) }
            item { Text(d.lastSummary, style = MaterialTheme.typography.caption1, textAlign = TextAlign.Center) }
        }
        item { Text(stringResource(R.string.local_only), style = MaterialTheme.typography.caption3, color = HsGrey, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp)) }
    }
}

@Composable
fun StationScreen(store: Store, onPick: (String) -> Unit) {
    val label = stringResource(R.string.type_station)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        val data = r.data ?: return@rememberLauncherForActivityResult
        val v = RemoteInput.getResultsFromIntent(data)?.getCharSequence("st")?.toString()?.trim()
        if (!v.isNullOrEmpty()) onPick(v)
    }
    val ctx = LocalContext.current
    var granted by remember { mutableStateOf(Stations.hasPermission(ctx)) }
    var near by remember { mutableStateOf<List<Near>?>(null) }
    var searching by remember { mutableStateOf(false) }
    val perm = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { res ->
        granted = res.values.any { it }
    }
    DisposableEffect(granted) {
        var sig: android.os.CancellationSignal? = null
        if (granted && Stations.all(ctx).isNotEmpty()) {
            searching = true
            sig = Stations.locate(ctx) { loc ->
                searching = false
                near = if (loc == null) emptyList() else Stations.nearest(ctx, loc)
            }
        }
        onDispose { try { sig?.cancel() } catch (_: Exception) {} }
    }
    val recent = remember { store.recentStations() }
    val presets = listOf("Rutledge Centraal", "Adlersteen Centraal", "Assen")
    val list = (recent + presets.filter { it !in recent }).take(6)
    Page {
        item { Text(stringResource(R.string.station), style = MaterialTheme.typography.title3) }
        if (!granted) {
            item {
                Chip(
                    onClick = { perm.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)) },
                    label = { Text(stringResource(R.string.use_location)) },
                    icon = { Text("📍") },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = HsBlue),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else if (searching) {
            item { Text(stringResource(R.string.locating), style = MaterialTheme.typography.caption1, color = HsGrey) }
        } else near?.let { nl ->
            if (nl.isEmpty()) item { Text(stringResource(R.string.no_location), style = MaterialTheme.typography.caption2, color = HsGrey) }
            else {
                item { Text(stringResource(R.string.nearby), style = MaterialTheme.typography.caption1, color = HsGreen) }
                nl.forEach { n ->
                    item {
                        Chip(
                            onClick = { onPick(n.station.name) },
                            label = { Text(n.station.name, maxLines = 2) },
                            secondaryLabel = { Text(if (n.km < 1) "${(n.km * 1000).toInt()} m" else "%.1f km".format(n.km)) },
                            icon = { Text("📍") },
                            colors = ChipDefaults.primaryChipColors(backgroundColor = Color(0xFF123A2A)),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        list.forEach { s ->
            item {
                Chip(
                    onClick = { onPick(s) },
                    label = { Text(s, maxLines = 2) },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item {
            Chip(
                onClick = {
                    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, listOf(RemoteInput.Builder("st").setLabel(label).build()))
                    launcher.launch(intent)
                },
                label = { Text(stringResource(R.string.other_station)) },
                colors = ChipDefaults.primaryChipColors(backgroundColor = HsBlue),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun TimeScreen(station: String, now: Long, onStart: (Long) -> Unit) {
    var offset by remember { mutableStateOf(0) } // minuten terug
    val at = now - offset * 60_000L
    val fmt = DateFormat.getTimeInstance(DateFormat.SHORT)
    Page {
        item { Text(station, style = MaterialTheme.typography.caption1, color = HsGrey, textAlign = TextAlign.Center) }
        item { Text(stringResource(R.string.start_time), style = MaterialTheme.typography.title3) }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Button(onClick = { offset = (offset + 5).coerceAtMost(720) }, colors = ButtonDefaults.secondaryButtonColors(), modifier = Modifier.size(ButtonDefaults.SmallButtonSize)) { Text("−5") }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(92.dp)) {
                    Text(fmt.format(Date(at)), fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    if (offset == 0) Text(stringResource(R.string.now), style = MaterialTheme.typography.caption3, color = HsGrey)
                }
                Button(onClick = { offset = (offset - 5).coerceAtLeast(0) }, enabled = offset > 0, colors = ButtonDefaults.secondaryButtonColors(), modifier = Modifier.size(ButtonDefaults.SmallButtonSize)) { Text("+5") }
            }
        }
        item {
            Chip(
                onClick = { onStart(if (offset == 0) System.currentTimeMillis() else at) },
                label = { Text(stringResource(R.string.start), fontWeight = FontWeight.Bold) },
                colors = ChipDefaults.primaryChipColors(backgroundColor = HsGreen, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun DutyScreen(d: Dienst, now: Long, store: Store, set: (Dienst) -> Unit) {
    val view = LocalView.current
    val ctx = LocalContext.current
    var confirmOut by remember { mutableStateOf(false) }
    LaunchedEffect(confirmOut) { if (confirmOut) { delay(5000); confirmOut = false } }
    val paused = d.pauseStart > 0
    val fmt = DateFormat.getTimeInstance(DateFormat.SHORT)
    fun buzz() { view.performHapticFeedback(HapticFeedbackConstants.CONFIRM) }

    Page {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.size(8.dp)) { drawCircle(if (paused) HsYellow else HsGreen, radius = size.minDimension / 2, center = Offset(size.width / 2, size.height / 2)) }
                Spacer(Modifier.width(6.dp))
                Text(stringResource(if (paused) R.string.paused else R.string.on_duty), style = MaterialTheme.typography.caption1, color = if (paused) HsYellow else HsGreen)
            }
        }
        item { Text(hms(d.worked(now)), fontSize = 34.sp, fontWeight = FontWeight.Bold) }
        item {
            Text(
                stringResource(R.string.from_station, d.station) + " · " + fmt.format(Date(d.start)),
                style = MaterialTheme.typography.caption2, color = HsGrey, textAlign = TextAlign.Center,
            )
        }
        if (paused) {
            item { Text(stringResource(R.string.since, fmt.format(Date(d.pauseStart))) + " · " + hm(now - d.pauseStart), style = MaterialTheme.typography.caption1, color = HsYellow) }
        }
        item { Spacer(Modifier.height(4.dp)) }
        item { Text(stringResource(R.string.transfers), style = MaterialTheme.typography.caption1, color = HsGrey) }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { set(store.minus()) },
                    enabled = !paused && d.transfers > 0,
                    colors = ButtonDefaults.secondaryButtonColors(),
                    modifier = Modifier.size(ButtonDefaults.ExtraSmallButtonSize),
                ) { Text("−") }
                Text("${d.transfers}", fontSize = 40.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp))
                Button(
                    onClick = { set(store.plus()); buzz() },
                    enabled = !paused,
                    colors = ButtonDefaults.primaryButtonColors(backgroundColor = HsBlue),
                    modifier = Modifier.size(ButtonDefaults.DefaultButtonSize),
                ) { Text("+", fontSize = 26.sp, fontWeight = FontWeight.Bold) }
            }
        }
        item {
            Chip(
                onClick = { set(store.togglePause()); buzz() },
                label = { Text(stringResource(if (paused) R.string.pause_end else R.string.pause_start)) },
                colors = if (paused) ChipDefaults.primaryChipColors(backgroundColor = HsYellow, contentColor = Color.Black) else ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Chip(
                onClick = {
                    if (!confirmOut) { confirmOut = true } else {
                        val t = System.currentTimeMillis()
                        val sum = ctx.getString(R.string.summary, d.transfers, hm(d.worked(t)))
                        set(store.stop(sum)); buzz()
                    }
                },
                label = { Text(stringResource(if (confirmOut) R.string.tap_again else R.string.report_out), fontWeight = FontWeight.Bold) },
                colors = ChipDefaults.primaryChipColors(backgroundColor = if (confirmOut) HsRed else Color(0xFF5A1A1A)),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (d.log.isNotEmpty()) {
            item { Text(stringResource(R.string.log), style = MaterialTheme.typography.caption1, color = HsGrey, modifier = Modifier.padding(top = 8.dp)) }
            d.log.take(10).forEach { e ->
                item {
                    CompactChip(
                        onClick = {},
                        label = {
                            Text(
                                fmt.format(Date(e.t)) + "  " + if (e.pause > 0) stringResource(R.string.log_pause, hm(e.pause)) else stringResource(R.string.log_transfer),
                                style = MaterialTheme.typography.caption2,
                            )
                        },
                        colors = ChipDefaults.chipColors(backgroundColor = HsCard),
                    )
                }
            }
        }
    }
}
