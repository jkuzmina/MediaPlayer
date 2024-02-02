package ru.netology.mediaplayer

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaType
import ru.netology.mediaplayer.databinding.ItemTrackBinding


class TrackAdapter(context: AppCompatActivity) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>(){
    private val mediaPlayer: MediaPlayer
    private val context: AppCompatActivity
    var previousPosition = -1
    var onItemClick: ((Track) -> Unit)? = null

    var tracks: List<Track> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    companion object{
        const val PLAYING = 101
        const val LOADING = 103
        const val NORMAL = 102
    }
    init {
        this.context = context
        this.mediaPlayer = MediaPlayer()
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        this.mediaPlayer.setOnCompletionListener {
            tracks.get(previousPosition).isLoading = false
            tracks.get(previousPosition).isPlaying = false
            notifyItemChanged(previousPosition)
            it.reset()
            //следующий трек
            if(previousPosition == itemCount - 1 && itemCount >= 1){
                previousPosition = 0
            } else{
                previousPosition++
            }
            playMedia(previousPosition)
        }
        //трек подгрузился и начинает воспроизводиться
        this.mediaPlayer.setOnPreparedListener {
            it.start()
            tracks.get(previousPosition).isLoading = false
            tracks.get(previousPosition).isPlaying = true
            notifyItemChanged(previousPosition)
        }

    }

    inner class TrackViewHolder(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root){

        var playButton = binding.play
        var pauseButton = binding.pause

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(tracks[adapterPosition])
            }
        }

        fun bind(track: Track){
            binding.apply {
                trackName.text = track.file
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTrackBinding.inflate(inflater, parent, false)

        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position] // Получение композиции из списка по позиции
        val context = holder.itemView.context

        holder.bind(track)
        holder.playButton.setOnClickListener {
             if (getState(position) == NORMAL) { //идет загрузка трека
                if (previousPosition != -1) {
                    pauseMedia(previousPosition)
                }

                previousPosition = position

                playMedia( position)
            }
        }
        holder.pauseButton.setOnClickListener {
            pauseMedia(position)
        }
        if(getState(position) == NORMAL) {
            holder.playButton.visibility = View.VISIBLE
            holder.pauseButton.visibility = View.GONE

        } else{
            holder.playButton.visibility = View.GONE
            holder.pauseButton.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int = tracks.size

    fun getState(position: Int): Int {
        return if (tracks.get(position).isPlaying)
            PLAYING
        else if (tracks.get(position).isLoading)
            LOADING
        else
            NORMAL
    }

    fun playMedia(pos: Int) {
        (context as MainActivity).updateButtonsVisibility(false)
        if (mediaPlayer.isPlaying) {
            pauseMedia(pos)
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/" + tracks.get(pos).file)
        //подготовка трека к воспроизведению
        //по окончании загрузки isLoading станет false, а isPlaying = true
        tracks.get(pos).isLoading = true
        notifyItemChanged(pos)
        mediaPlayer.prepareAsync()

    }

    fun pauseMedia(pos: Int) {
        (context as MainActivity).updateButtonsVisibility(true)
        tracks.get(pos).isPlaying = false
        tracks.get(pos).isLoading = false
        notifyItemChanged(pos)
        if (mediaPlayer.isPlaying()) {
            //mediaPlayer.stop()
            //mediaPlayer.reset()
            mediaPlayer.pause()

        }
    }


}