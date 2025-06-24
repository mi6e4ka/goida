package dev.mi6e4ka.zov

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import dev.mi6e4ka.zov.ui.theme.ZOVTheme
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZOVTheme {
                AppWithTabs()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWithTabs() {
    val tabs = listOf("Главная", "Фурри", "Mongo clicker \uD83E\uDD6D \uD83D\uDC80")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Ой мне пох mobile")},
                    colors = TopAppBarDefaults.topAppBarColors()
                )
                TabRow(
                    selectedTabIndex = pagerState.currentPage
                ) {
                    tabs.forEachIndexed {index,title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) }},
                            text = { Text(title)}
                        )
                    }
                }
            }
        }
    ) {
        padding ->
        Box(modifier = Modifier.padding(padding)) {
            HorizontalPager(state = pagerState) {
                tab ->
                    when (tab) {
                        0 -> MainPage()
                        1 -> PashaPage()
                        2 -> MangoClicker()
                    }
//                }
            }
        }
    }
}

@Composable
fun MainPage() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current.applicationContext
    val hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val mn = Calendar.getInstance().get(Calendar.MINUTE)
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val batLevel:Int = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    Column(
        modifier = Modifier.verticalScroll(scrollState).fillMaxSize().padding(0.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                val mediaPlayer = MediaPlayer.create(context, R.raw.ekh)
                mediaPlayer.start()
            }
        ) {
            Text("ЪЭ", fontSize = 30.sp)
        }
        Text(text = "$hr hour", fontSize = 85.sp)
        Text(text = "$mn minet", fontSize = 85.sp)
        Text(text = "$batLevel% chrg", fontSize = 75.sp)
        WebmVideoView(
            resourceId = R.raw.furry_speech,
            modifier =  Modifier.width(300.dp)
                     .aspectRatio(1f / 1f)
        )
        Text(text = "opensource version", fontSize = 30.sp)
    }
}

@Composable
fun PashaPage() {
    val context = LocalContext.current.applicationContext
    val diarrheaPlayer = MediaPlayer.create(context, R.raw.diarrhea_sounds)
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        WebmVideoView(
            resourceId = R.raw.furripasha,
            modifier = Modifier.fillMaxWidth()
        )
        Text("вкладка с фурри по запросу паши (это буквально он)")
        Button(onClick = {
            val fartPlayer = MediaPlayer.create(context, R.raw.fart_sound)
            fartPlayer.start()
        }) {
            Text("Кнопка пердежа (?)")
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = {
                diarrheaPlayer.start()
            }) {
                Text("Кнопка поноса... \uD83D\uDC80")
            }
            Button(onClick = {
                diarrheaPlayer.pause()
                diarrheaPlayer.seekTo(0)
            }) {
                Text("Остановить понос")
            }
        }
    }
}

@Composable
fun WebmVideoView(resourceId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    AndroidView(
        factory = {
            VideoView(it).apply {
                setVideoURI("android.resource://${context.packageName}/${resourceId}".toUri())
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    start()
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun MangoClicker() {
    val context = LocalContext.current.applicationContext
    val sharedPrefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    var counter by remember { mutableIntStateOf(sharedPrefs.getInt("mangoCount", 0)) }
    var mangoIncident by remember { mutableStateOf(false) }

    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.mangophonk)
    }
    val mangoIncidentEnabled = !sharedPrefs.getBoolean("mango", false)
    Column(
        verticalArrangement = Arrangement.spacedBy(25.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Mango \uD83D\uDC80\uD83D\uDC80", fontSize = 45.sp)
        Box {
            Image(
                painter = painterResource(id = R.drawable.mango),
                contentDescription = "Mango",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !mangoIncident) {
                        if (!mangoIncidentEnabled) {
                            sharedPrefs.edit {
                                putInt("mangoCount", counter)
                            }
                        }
                        if (counter==52 && mangoIncidentEnabled) {
                            mangoIncident = true
                            sharedPrefs.edit {
                                putBoolean("mango", true)
                            }
                            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)
                            mediaPlayer.pause()
                            mediaPlayer.seekTo(0)
                            mediaPlayer.start()
                            Handler(Looper.getMainLooper()).postDelayed({
                                throw RuntimeException("mango mango mango")
//                            mediaPlayer.pause()
                            }, 6500)
//                        mediaPlayer.setOnCompletionListener {
//                            throw RuntimeException("mango mango mango")
//                        }
                        } else {
                            if (!mediaPlayer.isPlaying) {
                                mediaPlayer.seekTo(50)
                                mediaPlayer.start()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (!mangoIncident) {
                                        mediaPlayer.pause()
                                    }
                                }, 400)
                            }
                            counter++
                        }
                    },
                colorFilter = if (mangoIncident) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.0f) }) else null
            )
            if (mangoIncident) {
                Image(
                    painter = painterResource(R.drawable.mango_troll),
                    contentDescription = "troll",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(250.dp)
                )
            }
        }

        if (mangoIncident) {
            Text("\uD83D\uDC80\uD83D\uDC80 $counter \uD83D\uDC80\uD83D\uDC80", fontSize = 50.sp, color = Color.Red)
        } else {
            Text("$counter", fontSize = 40.sp)
        }

    }
}