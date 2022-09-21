package com.websarva.wings.android.databasesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {
    // 選択されたカクテルの主キーIDを表すプロパティ。
    private var _cocktailId = -1
    // 選択されたカクテル名を表すプロパティ。
    private  var _cocktailName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // カクテルリスト用ListView(lvCocktail)を取得。
        val lvCocktail = findViewById<ListView>(R.id.lvCocktail)
        // lvCocktailにリスナを登録。
        lvCocktail.onItemClickListener = ListItemClickListener()
    }

    // 保存ボタンがタップされたときの処理メソッド。
    fun onSaveButtonClick(view: View) {
        // 感想欄を取得。
        val etNote = findViewById<EditText>(R.id.etNote)
        // 感想欄の入力値を消去。
        etNote.setText("")
        // カクテル名を表示するTextViewを取得。
        val tvCocktailName = findViewById<TextView>(R.id.tvCocktailName)
        // カクテル名を「未選択」に変更。
        tvCocktailName.text = getString(R.string.tv_name)
        // 保存ボタンを取得。
        val btnSave = findViewById<Button>(R.id.btnSave)
        // 保存ボタンをタップできないように変更。
        btnSave.isEnabled = false
    }

    // リストがタップされたときの処理が記述されたメンバクラス。
    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            // タップされた行番号をプロパティの主キーIDに代入。
            _cocktailId = position
            // タップされた行のデータを取得。これがカクテル名となるので、プロパティに代入。
            _cocktailName = parent.getItemAtPosition(position) as String
            // カクテル名を表示するTextViewを取得。
            val tvCocktailName = findViewById<TextView>(R.id.tvCocktailName)
            // カクテル名を表示するTextViewに表示カクテル名を設定。
            tvCocktailName.text = _cocktailName
            // 保存ボタンを取得。
            val btnSave = findViewById<Button>(R.id.btnSave)
            // 保存ボタンをタップできるように設定。
            btnSave.isEnabled = true
        }
    }
}