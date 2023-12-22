import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.myapplication.PlayPage.PlayPageViewModel

class NotificationControllerService : Service() {
    private val viewModel = PlayPageViewModel
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (intent.action) {
                "PLAY_PAUSE_ACTION" -> {
                    Log.i("","playPause")
//                    viewModel.togglePlayback() // 在 ViewModel 中控制播放/暫停
                }
                "PRE_ACTION" -> {
                    Log.i("", "PRE")
                }
                "NEXT_ACTION" -> {
                    Log.i("", "NEXT")
                }
                else -> {}
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
