package com.websarva.wings.android.mediasample

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    // メディアプレーヤープロパティ。
    private var _player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // プロパティのメディアプレーヤーオブジェクトを生成。
        _player = MediaPlayer()
        // 音声ファイルのURI文字列を作成。
        val mediaFileUriStr = "android.resource://${packageName}/${R.raw.mountain_stream}"
        // 音声ファイルのURI文字列を元にURIオブジェクトを生成。
        val mediaFileUri = Uri.parse(mediaFileUriStr)
        // プロパティのプレーヤーがnullでなければ…
        _player?.let {
            // メディアプレーヤーに音声ファイルを指定。
            it.setDataSource(this@MainActivity, mediaFileUri)
            // 非同期でのメディア再生準備が完了した際のリスナを設定。
            it.setOnPreparedListener(PlayerPreparedListener())
            // メディア再生が終了した際のリスナを設定。
            it.setOnCompletionListener(PlayerCompletionListener())
            // 非同期でメディア再生を準備。
            it.prepareAsync()
        }
    }

    // プレーヤーの再生準備が整ったときのリスナクラス。
    private inner class PlayerPreparedListener : MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer) {
            // 各ボタンをタップ可能に設定。
            val btPlay = findViewById<Button>(R.id.btPlay)
            btPlay.isEnabled = true
            val btBack = findViewById<Button>(R.id.btBack)
            btBack.isEnabled = true
            val btForward = findViewById<Button>(R.id.btForward)
        }
    }

    // 再生が終了したときのリスナクラス。
    private inner class PlayerCompletionListener : MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer?) {
            // 再生ボタンのラベルを「再生」に設定。
            val btPlay = findViewById<Button>(R.id.btPlay)
            btPlay.setText(R.string.bt_play_play)
        }
    }

    fun onPlayButtonClick(view: View) {
        // プロパティのプレーヤーがnullじゃなかったら
        _player?.let {
            // 再生ボタンを取得。
            val btPlay = findViewById<Button>(R.id.btPlay)
            // プレーヤーが再生中ならば…
            if(it.isPlaying) {
                // プレーヤーを一時停止。
                it.pause()
                // 再生ボタンのラベルを「再生」に設定。
                btPlay.setText(R.string.bt_play_play)
            }
            // プレーヤーが再生中でなければ…
            else {
                // プレーヤーを再生。
                it.start()
                // 再生ボタンのラベルを「一時停止」に設定。
                btPlay.setText(R.string.bt_play_pause)
            }
        }
    }

    override fun onDestroy() {
        // プレーヤーのプロパティがnullじゃなかったら
        _player?.let {
            // プレーヤーが再生中なら…
            if (it.isPlaying) {
                // プレーヤーを停止。
                it.stop()
            }
            // プレーヤーを解放。
            it.release()
        }
        // プレーヤー用プロパティをnullに。
        _player = null
        // 親クラスのメソッド呼び出し。
        super.onDestroy()
    }
}

