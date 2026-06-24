import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.openlog.ui.App

fun main() = application {
    val state = rememberWindowState(size = DpSize(1440.dp, 900.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "openLog",
        state = state,
    ) {
        App()
    }
}
