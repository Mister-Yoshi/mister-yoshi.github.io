package nl.hs.indienst

import androidx.concurrent.futures.ResolvableFuture
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonColors
import androidx.wear.protolayout.material.Chip
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.ListenableFuture

class DienstTile : TileService() {

    private fun <T> done(v: T): ListenableFuture<T> = ResolvableFuture.create<T>().also { it.set(v) }

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val store = Store(this)
        if (requestParams.currentState.lastClickableId == "plus") store.plus()
        val d = store.load()
        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setFreshnessIntervalMillis(60_000L)
            .setTileTimeline(TimelineBuilders.Timeline.fromLayoutElement(layout(d, requestParams.deviceConfiguration)))
            .build()
        return done(tile)
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> =
        done(ResourceBuilders.Resources.Builder().setVersion("1").build())

    private fun openApp(): ModifiersBuilders.Clickable =
        ModifiersBuilders.Clickable.Builder()
            .setId("open")
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName(packageName)
                            .setClassName(MainActivity::class.java.name)
                            .build()
                    ).build()
            ).build()

    private fun text(s: String, typo: Int, color: Int): LayoutElementBuilders.LayoutElement =
        Text.Builder(this, s).setTypography(typo).setColor(argb(color)).build()

    private fun layout(d: Dienst, dev: DeviceParametersBuilders.DeviceParameters): LayoutElementBuilders.LayoutElement {
        val now = System.currentTimeMillis()
        val paused = d.pauseStart > 0
        val label = getString(if (!d.on) R.string.off_duty else if (paused) R.string.paused else R.string.on_duty)
        val labelColor = if (!d.on) 0xFFA5A8AB.toInt() else if (paused) 0xFFFFC917.toInt() else 0xFF1FD38A.toInt()

        val content: LayoutElementBuilders.LayoutElement = if (d.on) {
            val plus = ModifiersBuilders.Clickable.Builder().setId("plus").setOnClick(ActionBuilders.LoadAction.Builder().build()).build()
            LayoutElementBuilders.Row.Builder()
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(
                    LayoutElementBuilders.Column.Builder()
                        .addContent(text(hm(d.worked(now)), Typography.TYPOGRAPHY_DISPLAY3, 0xFFFFFFFF.toInt()))
                        .addContent(text("${d.transfers} × " + getString(R.string.log_transfer).lowercase(), Typography.TYPOGRAPHY_CAPTION1, 0xFFA5A8AB.toInt()))
                        .build()
                )
                .addContent(LayoutElementBuilders.Spacer.Builder().setWidth(dp(10f)).build())
                .addContent(
                    Button.Builder(this, plus)
                        .setTextContent(getString(R.string.plus_one))
                        .setButtonColors(ButtonColors(argb(0xFF4D7CFF.toInt()), argb(0xFFFFFFFF.toInt())))
                        .build()
                )
                .build()
        } else {
            text(if (d.lastSummary.isNotBlank()) d.lastSummary else getString(R.string.brand), Typography.TYPOGRAPHY_BODY2, 0xFFFFFFFF.toInt())
        }

        return PrimaryLayout.Builder(dev)
            .setPrimaryLabelTextContent(text(label, Typography.TYPOGRAPHY_CAPTION1, labelColor))
            .setContent(content)
            .setPrimaryChipContent(
                Chip.Builder(this, openApp(), dev)
                    .setPrimaryLabelContent(getString(if (d.on) R.string.open_app else R.string.report_in))
                    .setChipColors(ChipColors(argb(0xFF141B2E.toInt()), argb(0xFFFFFFFF.toInt())))
                    .build()
            )
            .build()
    }
}
