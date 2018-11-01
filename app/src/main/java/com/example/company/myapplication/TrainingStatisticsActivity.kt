package com.example.company.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.media.AudioManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_training_statistics.*
import java.text.BreakIterator


var bmpBase: Bitmap? = null
var url = ""


class TrainingStatisticsActivity : AppCompatActivity() {

    private var finishBmp: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_statistics)
        
        //share example
        DrawPict()
        url = MediaStore.Images.Media.insertImage(this.contentResolver, finishBmp,  "title", null)

        share1.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.putExtra(Intent.EXTRA_STREAM,  Uri.parse(url))
            sharingIntent.type = "image/jpg"
            startActivity(Intent.createChooser(sharingIntent, "Share with friends"))
        }
        //-------------

        val presentationEntries = intent.getSerializableExtra(getString(R.string.presentationEntries)) as HashMap<Int,Float?>
        val presentationSpeedData = mutableListOf<BarEntry>()
        for (i in 0..(presentationEntries.size-1)) {
            presentationSpeedData.add(BarEntry((i).toFloat(), presentationEntries.get(i + 1)!!.toFloat()))
        }

        printSpeedLineChart(presentationSpeedData)


        val presentationTop10Words = getTop10Words(intent.getStringExtra("allRecognizedText"))
        val entries = ArrayList<PieEntry>()
        for (pair in presentationTop10Words){
            entries.add(PieEntry(pair.second.toFloat(), pair.first))
        }
        printPiechart(entries)
    }

    fun DrawPict(){

        val presName = intent.getStringExtra(NAME_OF_PRES)

        Log.d("razmeri", bmpBase?.width.toString() + " / " + bmpBase?.height.toString())

        finishBmp = Bitmap.createBitmap(bmpBase!!.width, bmpBase!!.height + 185, Bitmap.Config.ARGB_8888)
        val whitePaint = Paint()
        whitePaint.style = Paint.Style.FILL
        whitePaint.color = Color.WHITE

        val nameBmp = Bitmap.createBitmap(bmpBase!!.width, 40, Bitmap.Config.ARGB_8888)
        val nameC = Canvas(nameBmp)
        nameC.drawPaint(whitePaint)
        val namePaint = Paint()
        namePaint.color = Color.BLACK
        namePaint.style = Paint.Style.FILL
        namePaint.isAntiAlias = true
        namePaint.textSize = 24f
        namePaint.isUnderlineText = true;
        nameC.drawText(presName, 90f, 30f, namePaint)

        val countBmp = Bitmap.createBitmap(bmpBase!!.width, 30, Bitmap.Config.ARGB_8888)
        val countC = Canvas(countBmp)
        countC.drawPaint(whitePaint)
        val countPaint = Paint()
        countPaint.color = Color.BLACK
        countPaint.style = Paint.Style.FILL
        countPaint.isAntiAlias = true
        countPaint.textSize = 20f
        countC.drawText("Вы тренировались 1 раз(а)", 20f, 20f, countPaint)

        val statBmp = Bitmap.createBitmap(bmpBase!!.width, 115, Bitmap.Config.ARGB_8888)
        val statC = Canvas(statBmp)
        statC.drawPaint(whitePaint)
        val statPaint = Paint()
        statPaint.color = Color.BLACK
        statPaint.style = Paint.Style.FILL
        statPaint.isAntiAlias = true
        statPaint.textSize = 20f
        statC.drawText("Результат тренировки:", 20f, 20f, statPaint)
        statPaint.textSize = 17f
        statPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
        statC.drawText("---Вы говорили 1 секунд(у)", 30f, 43f, statPaint)
        statC.drawText("---Ваш результат на 100% далек от рекордного", 30f, 66f, statPaint)
        statC.drawText("---Вы заработали:", 30f, 99f, statPaint)

        val canvas = Canvas(finishBmp)
        val paint = Paint()
        canvas.drawBitmap(bmpBase,0f,0f, paint)
        canvas.drawBitmap(nameBmp,0f,bmpBase!!.height.toFloat(), paint)
        canvas.drawBitmap(countBmp,0f,bmpBase!!.height.toFloat() + 40f, paint)
        canvas.drawBitmap(statBmp,0f,bmpBase!!.height.toFloat() + 70f, paint)
        val paintCircle = Paint()
        paintCircle.color = Color.YELLOW
        canvas.drawCircle(185f, bmpBase!!.height.toFloat() + 161f, 15f, paintCircle)
        canvas.drawCircle(225f, bmpBase!!.height.toFloat() + 161f, 15f, paintCircle)
        canvas.drawCircle(265f, bmpBase!!.height.toFloat() + 161f, 15f, paintCircle)
    }

    //Инициализация графика скорсти чтения
    fun printSpeedLineChart(lineEntries: List<BarEntry>){
        val labels = ArrayList<String>()

        for(entry in lineEntries)
            labels.add((entry.x +1).toInt().toString())

        val barDataSet = BarDataSet(lineEntries, getString(R.string.words_count))
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS,255)

        val data = BarData(barDataSet)
        data.setValueTextSize(0f)

        speed_bar_chart.setFitBars(true)
        speed_bar_chart.data = data
        speed_bar_chart.description.text = getString(R.string.slide_number)
        speed_bar_chart.description.textSize = 15f
        speed_bar_chart.animateXY(1000,1000)
        speed_bar_chart.legend.textSize = 20f
        speed_bar_chart.legend.position = Legend.LegendPosition.ABOVE_CHART_LEFT
        speed_bar_chart.legend.formSize = 0f
        speed_bar_chart.legend.xEntrySpace = 0f


        speed_bar_chart.setScaleEnabled(false)//выкл возможность зумить
        speed_bar_chart.xAxis.setDrawGridLines(false)//отключение горизонтальных линии сетки
        speed_bar_chart.axisRight.isEnabled = false// ось У справа невидимая
        speed_bar_chart.axisLeft.setDrawGridLines(false)//откл вертикальных линий сетки
        speed_bar_chart.axisLeft.textSize = 15f
        speed_bar_chart.axisLeft.axisMinimum = 0f // минимальное значение оси y = 0

        val xAxis = speed_bar_chart.xAxis
        xAxis.textSize = 12f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f

        speed_bar_chart.invalidate()
    }

    fun printPiechart (lineEntries: List<PieEntry>){
//        val labels = ArrayList<String>()
//        for(entry in lineEntries)
//            labels.add((entry.x +1).toInt().toString())
        val pieDataSet = PieDataSet(lineEntries, getString(R.string.words_count))
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS,255)
//        val entries = ArrayList<PieEntry>()
//
//        val set = PieDataSet(entries, "Election Results")
        val data = PieData(pieDataSet)
        pie_chart.data = data

        pie_chart.invalidate()
    }

    fun getTop10Words(text: String) : List<Pair<String, Int>> {
        val dictionary = HashMap<String, Int>()

        val iterator = BreakIterator.getWordInstance()
        iterator.setText(text)

        var endIndex = iterator.first()
        while (BreakIterator.DONE != endIndex) {
            val startIndex = endIndex
            endIndex = iterator.next()
            if (endIndex != BreakIterator.DONE && Character.isLetterOrDigit(text[startIndex])) {
                val word = text.substring(startIndex, endIndex)
                val count = dictionary[word] ?: 0
                dictionary[word] = count + 1
            }
        }

        val result = ArrayList<Pair<String, Int>>()
        dictionary.onEach {
            val position = getPosition(result, it.value)
            if (position < 10)
                result.add(position, it.toPair())
            if (result.size > 10)
                result.removeAt(10)
        }
        return result
    }

    private fun getPosition(list : List<Pair<String, Int>>, value : Int) : Int {
        if (list.isEmpty())
            return 0
        for (i in list.indices) {
            if (value > list[i].second)
                return i
        }
        return list.size
    }
}
