package ru.netology.mediaplayer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.IOException
import ru.netology.mediaplayer.databinding.ActivityMainBinding
import ru.netology.mediaplayer.TrackAdapter
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TrackAdapter // Объект Adapter
    private lateinit var manager: LinearLayoutManager

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private var album = Album()
    private var currentTrackPos = -1


    fun getAll(): Unit {

        val url = "https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json"
        println(url)
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                album = gson.fromJson(response.body!!.string(), Album::class.java)

                runOnUiThread {
                    binding.title.text = album.title
                    binding.artist.text = "Исполнитель:" + album.artist
                    binding.published.text = album.published
                    binding.genre.text = album.genre
                    adapter.tracks = album.tracks // Заполнение данными
                    binding.recyclerView.layoutManager = manager // Назначение LayoutManager для RecyclerView
                    binding.recyclerView.adapter = adapter // Назначение адаптера для RecyclerView

                }

            }
        })
    }

    fun updateButtonsVisibility(playVisible: Boolean) {
        if (playVisible) {
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.GONE
        } else {
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.VISIBLE
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manager = LinearLayoutManager(applicationContext)
        adapter = TrackAdapter(this@MainActivity)
        getAll()
        binding.play.setOnClickListener{
            val trackViewHolder = binding.recyclerView.findViewHolderForAdapterPosition(0) as TrackAdapter.TrackViewHolder
            trackViewHolder.binding.play.performClick()
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.VISIBLE
        }
        binding.pause.setOnClickListener{
            val trackViewHolder = binding.recyclerView.findViewHolderForAdapterPosition(adapter.previousPosition) as TrackAdapter.TrackViewHolder
            trackViewHolder.binding.pause.performClick()
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.GONE

        }

        adapter.onItemClick = { track ->
            if(adapter.getState(adapter.previousPosition) == TrackAdapter.NORMAL) {
                updateButtonsVisibility(true)
            }
        }

    }

}