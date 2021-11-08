package com.jsbl.genix

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.jsbl.genix.databinding.TestBinding

class TestCases : AppCompatActivity() {

    private lateinit var thumbView: View

    private lateinit var binding: TestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_cases)

        binding = TestBinding.inflate(layoutInflater)

/*
        thumbView = LayoutInflater.from(this).inflate(R.layout.layout_seekbar_thumb, null, false)
        val sk = findViewById<ProgressBar>(R.id.seekBar)*/
        /*  sk.thumb = getThumb(0)

          sk.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
              override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                  seekBar.thumb = getThumb(progress)
              }

              override fun onStartTrackingTouch(seekBar: SeekBar) {}
              override fun onStopTrackingTouch(seekBar: SeekBar) {}
          })*/


    }

    fun getThumb(progress: Int): Drawable? {
        (thumbView.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            thumbView.measuredWidth,
            thumbView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.measuredWidth, thumbView.measuredHeight)
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }
}
































