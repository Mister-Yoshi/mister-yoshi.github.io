package nl.hs.indienst

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Station(val name: String, val lat: Double, val lng: Double)
data class Near(val station: Station, val km: Double)

object Stations {
    @Volatile private var cache: List<Station>? = null

    /** Stationslijst uit assets/stations.txt (naam, breedte, lengte; bij het bouwen opgehaald via de Worker). */
    fun all(ctx: Context): List<Station> = cache ?: try {
        ctx.assets.open("stations.txt").bufferedReader().readLines().mapNotNull { l ->
            val p = l.split('\t')
            if (p.size < 3) null else {
                val la = p[1].toDoubleOrNull(); val lo = p[2].toDoubleOrNull()
                if (la == null || lo == null || (la == 0.0 && lo == 0.0)) null else Station(p[0], la, lo)
            }
        }
    } catch (e: Exception) {
        emptyList()
    }.also { cache = it }

    fun km(aLa: Double, aLo: Double, bLa: Double, bLo: Double): Double {
        val r = 6371.0
        val dLa = Math.toRadians(bLa - aLa); val dLo = Math.toRadians(bLo - aLo)
        val h = sin(dLa / 2) * sin(dLa / 2) + cos(Math.toRadians(aLa)) * cos(Math.toRadians(bLa)) * sin(dLo / 2) * sin(dLo / 2)
        return 2 * r * atan2(sqrt(h), sqrt(1 - h))
    }

    fun nearest(ctx: Context, loc: Location, n: Int = 3): List<Near> =
        all(ctx).map { Near(it, km(loc.latitude, loc.longitude, it.lat, it.lng)) }.sortedBy { it.km }.take(n)

    fun hasPermission(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /** Huidige plek: eerst een recente bekende plek, anders een nieuwe meting. */
    @SuppressLint("MissingPermission")
    fun locate(ctx: Context, done: (Location?) -> Unit): CancellationSignal? {
        if (!hasPermission(ctx)) { done(null); return null }
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = buildList {
            if (Build.VERSION.SDK_INT >= 31) add(LocationManager.FUSED_PROVIDER)
            add(LocationManager.GPS_PROVIDER); add(LocationManager.NETWORK_PROVIDER)
        }.filter { try { lm.isProviderEnabled(it) } catch (e: Exception) { false } }
        val fresh = providers.mapNotNull { try { lm.getLastKnownLocation(it) } catch (e: Exception) { null } }
            .filter { System.currentTimeMillis() - it.time < 2 * 60_000 }.maxByOrNull { it.time }
        if (fresh != null) { done(fresh); return null }
        val p = providers.firstOrNull() ?: run { done(null); return null }
        val sig = CancellationSignal()
        try {
            lm.getCurrentLocation(p, sig, ContextCompat.getMainExecutor(ctx)) { done(it) }
        } catch (e: Exception) {
            done(null)
        }
        return sig
    }
}
