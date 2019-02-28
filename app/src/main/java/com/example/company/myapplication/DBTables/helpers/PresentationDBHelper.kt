package com.example.company.myapplication.DBTables.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.company.myapplication.appSupport.PdfToBitmap
import com.example.putkovdimi.trainspeech.DBTables.DaoInterfaces.PresentationDataDao
import com.example.putkovdimi.trainspeech.DBTables.SpeechDataBase
import java.io.ByteArrayOutputStream

class PresentationDBHelper {
    private val presentationDataDao: PresentationDataDao
    private val pdfToBitmap: PdfToBitmap

    constructor(ctx: Context) {
        presentationDataDao = SpeechDataBase.getInstance(ctx)!!.PresentationDataDao()
        pdfToBitmap = PdfToBitmap(ctx)
    }

    fun changePresentationImage(presentationId: Int, image: Bitmap) {
        val presentation = presentationDataDao.getPresentationWithId(presentationId) ?: return
        val stream = ByteArrayOutputStream()
        getResizedBitmap(image, 300).compress(Bitmap.CompressFormat.PNG, 100, stream)
        presentation.imageBLOB = stream.toByteArray()
        presentationDataDao.updatePresentation(presentation)
        stream.close()
    }

    fun saveDefaultPresentationImage(presentationId: Int) {
        val presentation = presentationDataDao.getPresentationWithId(presentationId) ?: return
        pdfToBitmap.addPresentation(presentation.stringUri, presentation.debugFlag)
        val bm = pdfToBitmap.getBitmapForSlide(0) ?: return
        val stream = ByteArrayOutputStream()
        getResizedBitmap(bm, 300).compress(Bitmap.CompressFormat.PNG, 100, stream)
        presentation.imageBLOB = stream.toByteArray()
        presentationDataDao.updatePresentation(presentation)
        stream.close()
    }

    fun getPresentationImage(presentationId: Int): Bitmap? {
        return try {
            val presentation = presentationDataDao.getPresentationWithId(presentationId)
            val blob = presentation?.imageBLOB
            val bm = BitmapFactory.decodeByteArray(blob, 0, blob!!.size)
            bm
        } catch (e: Exception) { null }
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}