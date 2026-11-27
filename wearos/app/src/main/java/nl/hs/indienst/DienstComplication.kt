package nl.hs.indienst

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService

/** Kleine waarde op de wijzerplaat: gewerkte tijd met het aantal overstappen als titel. */
class DienstComplication : SuspendingComplicationDataSourceService() {

    private fun plain(s: String) = PlainComplicationText.Builder(s).build()

    private fun short(text: String, title: String, desc: String): ComplicationData {
        val tap = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return ShortTextComplicationData.Builder(plain(text), plain(desc))
            .setTitle(plain(title))
            .setTapAction(tap)
            .build()
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? =
        if (type == ComplicationType.SHORT_TEXT) short("3:45", "2×", getString(R.string.on_duty)) else null

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        if (request.complicationType != ComplicationType.SHORT_TEXT) return null
        val d = Store(this).load()
        return if (d.on) {
            val t = if (d.pauseStart > 0) getString(R.string.paused) else hm(d.worked(System.currentTimeMillis()))
            short(t, "${d.transfers}×", getString(R.string.on_duty))
        } else short("—", getString(R.string.brand), getString(R.string.off_duty))
    }
}
