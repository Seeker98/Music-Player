package com.seeker.musicplayer

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.seeker.musicplayer.model.LocalMusicBean
import com.seeker.musicplayer.ui.LocalMusicAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), View.OnClickListener {
    //data source
    var mDatas = ArrayList<LocalMusicBean>()
    lateinit var adapter: LocalMusicAdapter
    var currentPos: Int = -1
    var musicProgress: Int = 0
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myRequetPermission()
        initControl()
        mediaPlayer = MediaPlayer()
        mDatas = ArrayList()

        adapter = LocalMusicAdapter(mDatas)
        music_list.adapter = adapter
        setEventListener()

        loadLocalMusicData()
        music_list.layoutManager = LinearLayoutManager(this)
    }

    private fun setEventListener() {
        adapter.setOnItemClickListener(object : LocalMusicAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                currentPos = position
                playMusicAt(position)
            }
        })
    }

    private fun playMusicAt(pos: Int) {
        val musicBean = mDatas[pos]
        music_bottom_music_name.text = musicBean.music
        music_bottom_artist_name.text = musicBean.artist
        stopMusic()
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(musicBean.path)
            playMusic()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadLocalMusicData() {
        mDatas.clear()
        val musicProj = arrayOf(TITLE, ARTIST, ALBUM, DURATION, DATA)
        val cursor: Cursor? =
            contentResolver.query(EXTERNAL_CONTENT_URI, musicProj, null, null, null)
        if (cursor != null) {
            var num = 0
            while (cursor.moveToNext()) {
                num++
                mDatas.add(
                    LocalMusicBean(
                        num.toString(),
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        SimpleDateFormat("mm:ss", Locale.CHINA).format(Date(cursor.getLong(3))),
                        cursor.getString(4)
                    )
                )
            }
            adapter.notifyDataSetChanged()
        }
        cursor?.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_refresh-> loadLocalMusicData()
            R.id.menu_close-> finish()
        }
        return true
    }
    

    private fun initControl() {
        music_bottom_control_next.setOnClickListener(this)
        music_bottom_control_prev.setOnClickListener(this)
        music_bottom_control_pause.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.getId()) {
            R.id.music_bottom_control_next -> {
                if (currentPos == mDatas.size - 1) {
                    return Toast.makeText(this, "已经是最后一首", Toast.LENGTH_SHORT).show()
                }
                currentPos++
                playMusicAt(currentPos)
            }
            R.id.music_bottom_control_prev -> {
                if (currentPos <= 0) {
                    return Toast.makeText(this, "已经是第一首", Toast.LENGTH_SHORT).show()
                }
                currentPos--
                playMusicAt(currentPos)
            }
            R.id.music_bottom_control_pause -> {
                if (currentPos == -1) {
                    Toast.makeText(this, "请选择音乐", Toast.LENGTH_SHORT).show()
                    return
                }
                if (mediaPlayer.isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }
        }
    }

    //    controls: play, pause and stop
    private fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            if (musicProgress == 0) {
                try {
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                mediaPlayer.seekTo(musicProgress)
                mediaPlayer.start()
            }
            music_bottom_control_pause.setText(R.string.pause)
        }
    }

    private fun stopMusic() {
        musicProgress = 0
        mediaPlayer.pause()
        mediaPlayer.seekTo(0)
        mediaPlayer.stop()
        music_bottom_control_pause.setText(R.string.play)
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            musicProgress = mediaPlayer.currentPosition
            mediaPlayer.pause()
            music_bottom_control_pause.setText(R.string.play)
        }
    }

    //    ask for perm when run
    private fun myRequetPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
        mediaPlayer.release()
    }
}