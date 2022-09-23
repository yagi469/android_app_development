package com.websarva.wings.android.implicitintentsample

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onMapSearchButtonClick(view: View) {
        // 入力欄に入力されたキーワード文字列を取得。
        val etSearchWord = findViewById<EditText>(R.id.etSearchWord)
        var searchWord = etSearchWord.text.toString()
        // 入力されたキーワードをURLエンコード。
        searchWord = URLEncoder.encode(searchWord, "UTF-8")
        // マップアプリと連携するURI文字列を生成。
        val uriStr = "geo:0,0?q=${searchWord}"
        // URI文字列からURIオブジェクトを生成。
        val uri = Uri.parse(uriStr)
        // Intentオブジェクトを生成。
        val intent = Intent(Intent.ACTION_VIEW, uri)
        // アクティビティを起動。
        startActivity(intent)
    }
}