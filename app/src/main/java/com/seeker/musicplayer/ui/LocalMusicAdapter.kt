package com.seeker.musicplayer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seeker.musicplayer.R
import com.seeker.musicplayer.model.LocalMusicBean

class LocalMusicAdapter(val mDatas: List<LocalMusicBean>) :
    RecyclerView.Adapter<LocalMusicAdapter.LocalMusicViewHolder>() {

    inner class LocalMusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTv: TextView = itemView.findViewById(R.id.item_music_id)
        val musicTv: TextView = itemView.findViewById(R.id.item_music_name)
        val artistTv: TextView = itemView.findViewById(R.id.item_music_artist)
        val albumTv: TextView = itemView.findViewById(R.id.item_music_album)
        val durationTv: TextView = itemView.findViewById(R.id.item_music_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalMusicViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.music_list, parent, false)
        val holder=LocalMusicViewHolder(view)
        return LocalMusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalMusicViewHolder, position: Int) {
        val localMusicBean: LocalMusicBean = mDatas[position]
        holder.idTv.text = localMusicBean.id
        holder.musicTv.text = localMusicBean.music
        holder.artistTv.text = localMusicBean.artist
        holder.albumTv.text = localMusicBean.album
        holder.durationTv.text = localMusicBean.duration

        holder.itemView.setOnClickListener { view ->
            listener?.onItemClick(view, position)
        }
    }

    override fun getItemCount() = mDatas.size

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(o: OnItemClickListener?) {
        this.listener = o
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }









}