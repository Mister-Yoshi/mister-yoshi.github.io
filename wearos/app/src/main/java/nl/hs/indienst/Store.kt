package nl.hs.indienst

import android.content.ComponentName
import android.content.Context
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import org.json.JSONArray
import org.json.JSONObject

/** Eén logregel: een overstap (pause == 0) of een afgeronde pauze (pause = duur in ms). */
data class LogItem(val t: Long, val pause: Long = 0L)

/** De dienst, zoals de webapp: S = {on, t, s0, log, p, pt}. */
data class Dienst(
    val on: Boolean = false,
    val start: Long = 0L,
    val station: String = "",
    val transfers: Int = 0,
    val pauseStart: Long = 0L,
    val pausedTotal: Long = 0L,
    val log: List<LogItem> = emptyList(),
    val lastSummary: String = "",
) {
    fun worked(now: Long): Long {
        if (!on) return 0L
        val p = pausedTotal + if (pauseStart > 0) now - pauseStart else 0L
        return (now - start - p).coerceAtLeast(0L)
    }
}

fun hm(ms: Long): String {
    val m = ms / 60000
    return "%d:%02d".format(m / 60, m % 60)
}

fun hms(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d:%02d".format(s / 3600, (s / 60) % 60, s % 60)
}

class Store(private val ctx: Context) {
    private val prefs = ctx.getSharedPreferences("hsdienst", Context.MODE_PRIVATE)

    fun load(): Dienst {
        val raw = prefs.getString("s", null) ?: return Dienst()
        return try {
            val o = JSONObject(raw)
            val arr = o.optJSONArray("log") ?: JSONArray()
            val log = (0 until arr.length()).map {
                val e = arr.getJSONObject(it)
                LogItem(e.getLong("t"), e.optLong("pz", 0L))
            }
            Dienst(
                on = o.optBoolean("on"),
                start = o.optLong("t"),
                station = o.optString("s0"),
                transfers = o.optInt("n"),
                pauseStart = o.optLong("p"),
                pausedTotal = o.optLong("pt"),
                log = log,
                lastSummary = o.optString("sum"),
            )
        } catch (e: Exception) {
            Dienst()
        }
    }

    fun save(d: Dienst) {
        val arr = JSONArray()
        d.log.take(40).forEach { arr.put(JSONObject().put("t", it.t).put("pz", it.pause)) }
        val o = JSONObject()
            .put("on", d.on).put("t", d.start).put("s0", d.station).put("n", d.transfers)
            .put("p", d.pauseStart).put("pt", d.pausedTotal).put("log", arr).put("sum", d.lastSummary)
        prefs.edit().putString("s", o.toString()).apply()
        refreshSurfaces()
    }

    fun recentStations(): List<String> =
        (prefs.getString("recent", null) ?: "").split("\n").filter { it.isNotBlank() }

    fun remember(station: String) {
        val list = (listOf(station) + recentStations().filter { it != station }).take(5)
        prefs.edit().putString("recent", list.joinToString("\n")).apply()
    }

    fun start(station: String, at: Long): Dienst {
        remember(station)
        val d = load().copy(on = true, start = at, station = station, transfers = 0, pauseStart = 0L, pausedTotal = 0L, log = emptyList())
        save(d); return d
    }

    fun plus(): Dienst {
        val d = load()
        if (!d.on || d.pauseStart > 0) return d
        val n = d.copy(transfers = d.transfers + 1, log = listOf(LogItem(System.currentTimeMillis())) + d.log)
        save(n); return n
    }

    fun minus(): Dienst {
        val d = load()
        if (!d.on || d.transfers == 0) return d
        val i = d.log.indexOfFirst { it.pause == 0L }
        val log = if (i >= 0) d.log.filterIndexed { k, _ -> k != i } else d.log
        val n = d.copy(transfers = d.transfers - 1, log = log)
        save(n); return n
    }

    fun togglePause(): Dienst {
        val d = load()
        if (!d.on) return d
        val now = System.currentTimeMillis()
        val n = if (d.pauseStart > 0) {
            val dur = now - d.pauseStart
            d.copy(pauseStart = 0L, pausedTotal = d.pausedTotal + dur, log = listOf(LogItem(d.pauseStart, dur.coerceAtLeast(1L))) + d.log)
        } else d.copy(pauseStart = now)
        save(n); return n
    }

    fun stop(summary: String): Dienst {
        val n = Dienst(lastSummary = summary)
        save(n); return n
    }

    private fun refreshSurfaces() {
        try {
            TileService.getUpdater(ctx).requestUpdate(DienstTile::class.java)
        } catch (_: Exception) {
        }
        try {
            ComplicationDataSourceUpdateRequester.create(ctx, ComponentName(ctx, DienstComplication::class.java)).requestUpdateAll()
        } catch (_: Exception) {
        }
    }
}
