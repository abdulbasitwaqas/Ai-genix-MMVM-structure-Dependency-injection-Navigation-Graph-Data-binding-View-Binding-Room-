package com.jsbl.genix.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityCnicCaptureBinding
import com.jsbl.genix.model.registration.ResponseUploadImage
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.extensions.showCaptureAlert
import com.jsbl.genix.viewModel.CameraCaptureViewModel
import com.jsbl.genix.views.activities.ActCaptureCamera.Companion.IMAGE_COMPRESS_Q
import com.jsbl.genix.views.activities.ActCaptureCamera.Companion.IMAGE_W_H
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CNIC_IMAGE_URLS
import com.jsbl.genix.views.dialogs.ProgressDialog
import com.otaliastudios.cameraview.CameraLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ActCaptureCNIC : AppCompatActivity(), OnViewClickListener {

    companion object {
        private val LOG = CameraLogger.create(APP_TAG)
        private const val USE_FRAME_PROCESSOR = false
        private const val DECODE_BITMAP = false
        const val PICK_GALLERY_IMAGE = 25
        const val CNIC_FRONT = "CNIC_FRONT_"
        const val CNIC_BACK = "CNIC_BACK_"
        const val requestCAMERA = 101

    }

    var fileAbsPath: String = ""
    var imagePaths = arrayListOf<String>()
    var isFront = true
    var frontFile: File? = null
    var backFile: File? = null

    private lateinit var binding: ActivityCnicCaptureBinding
    private lateinit var viewModel: CameraCaptureViewModel
    private lateinit var requestObserver: Observer<RequestHandler>

    private var cameraId = 0
    private var flashmode = false
    private var rotation = 0

    private var captureTime: Long = 0
    private var currentFilter = 0
    lateinit var dialogP: ProgressDialog


    private lateinit var scope: CoroutineScope
    private var job = Job()

    lateinit var textRecognizerGallery: TextRecognizer
    var itemdetected = false
    var mCameraSource: CameraSource? = null
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCnicCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.onClickListener = this
//        binding.surfaceView.holder.addCallback(this)
        scope = CoroutineScope(Dispatchers.Main + job)
        viewModel = ViewModelProvider(this).get(CameraCaptureViewModel::class.java)
        setRequestHandler()
        startCameraSource()
        checkFlashAndCameraCount()
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
            }

            override fun onFinish() {
                if (this@ActCaptureCNIC::dialogP.isInitialized)
                    if (dialogP.isAdded)
                        dialogP.dismiss()
            }
        }
    }


    private fun startCameraSource() {
        textRecognizerGallery = TextRecognizer.Builder(this).build()

        val textRecognizer = TextRecognizer.Builder(this@ActCaptureCNIC).build()
        if (!textRecognizer.isOperational) {
            Log.w(
                APP_TAG,
                "Detector dependencies not loaded yet"
            )
            return
        }
        //Initialize camerasource to use high resolution and set Autofocus on.
        mCameraSource = CameraSource.Builder(this, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setAutoFocusEnabled(true)
            .setRequestedPreviewSize(1280, 1024)
            .setRequestedFps(2.0f)
            .build()
        /**
         * Add call back to SurfaceView and check if camera permission is granted.
         * If permission is granted we can start our cameraSource and pass it to surfaceView
         */


        binding.surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ActCaptureCNIC,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@ActCaptureCNIC,
                            arrayOf(Manifest.permission.CAMERA),
                            requestCAMERA
                        )
                        return
                    }
                    mCameraSource!!.start(binding.surfaceView!!.holder)


                    // flashOnButton();
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mCameraSource!!.stop()
            }
        })


    }


    lateinit var requestResponse: ResponseUploadImage

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix,
            true
        )
    }

    @Synchronized
    fun detectText(bitmapText: Bitmap) {
//        val textBitmap = BitmapFactory.decodeResource(resources, R.drawable.cat)

        if (!textRecognizerGallery.isOperational) {
            dismissDialog()
            return
        }
        val frame: com.google.android.gms.vision.Frame =
            com.google.android.gms.vision.Frame.Builder().setBitmap(bitmapText).build()
        val text: SparseArray<TextBlock> = textRecognizerGallery.detect(frame)
        if (text.size() == 0) {
//                showShort(this@ScanCNICActivity, "Unable to Decode file")
            scope.launch(Dispatchers.Main) {
                dismissDialog()
                showShort(this@ActCaptureCNIC, "No Text Detected")

            }

            return
        }
        if (!itemdetected) {
            itemdetected = true
            sortTextBlocks(items = text, bitmap = bitmapText)
        }

    }


    private fun cnicDetector(string: String?): Boolean {
// String regex="[{^{0}\d{3}-{0,1}\d{7}$|^\d{11}$|^\d{4}-\d{7}$]";
// String reg="[03]?[0-9]{9}$";
        val reg = "^[0-9+]{5}-[0-9+]{7}-[0-9]{1}$".toRegex()

        if (string == null) {
            return false
        } else if (string == "") {
            return false
        } else if (string.contains(" ")) {
            var strsplited = string.trim().split(" ")
            if (strsplited.size > 1) {
                for (str2 in strsplited) {
                    if (str2.contains(reg)) {
                        return true
                    }
                }
            }
        } else {
            if (string.trim().contains(reg)) {
                return true
            }
        }

        //        String reg_overseas = "^[0-9+]{6}-[0-9+]{6}-[0-9]{1}$";

        return false
        // check the lenght of the enter data in EditText and give error if its empty
    }

    @Synchronized
    private fun sortTextBlocks(items: SparseArray<TextBlock>, bitmap: Bitmap) {
        if (items.size() != 0) {
            logD(APP_TAG, "Some Text found")
            for (i in 0 until items.size()) {
                Log.d(APP_TAG, items.valueAt(i).value)
                if (cnicDetector(items.valueAt(i).value)) {
                    Log.d(
                        APP_TAG,
                        "CNIC got " + items.valueAt(i).value
                    )
                    scope.launch(Dispatchers.Main) {
                        dismissDialog()

                    }
//                    showAlert(bitmap)
                    bitmap?.let {
                        showCaptureAlert(this@ActCaptureCNIC, onPositiveClick = {
                            saveImage(it)
                            if (this@ActCaptureCNIC::dialogP.isInitialized)
                                if (dialogP.isAdded)
                                    dialogP.dismiss()
                        }, onNegativeClick = {
                            if (this@ActCaptureCNIC::dialogP.isInitialized)
                                if (dialogP.isAdded)
                                    dialogP.dismiss()
                        }, bitmap = it)
                    }
                    itemdetected = false
                    return

                }
            }
        }
        scope.launch(Dispatchers.Main) {
            if (this@ActCaptureCNIC::dialogP.isInitialized)
                if (dialogP.isAdded)
                    dialogP.dismiss()
            showShort(this@ActCaptureCNIC, "No CNIC Detected")
        }
        itemdetected = false
    }

    fun setRequestHandler() {
        requestObserver = object : Observer<RequestHandler> {
            override fun onChanged(t: RequestHandler?) {

                if (t != null) {

                    if (t.loading && !t.isSuccess) {
                    } else if (!t.loading && !t.isSuccess) {
                        if (dialogP.isAdded)
                            dialogP.dismiss()
                        logout(t.any!!, this@ActCaptureCNIC)
                    } else if (!t.loading && t.isSuccess) {
                        if (dialogP.isAdded)
                            dialogP.dismiss()
                        //TODO
                        if (t.any is ArrayList<*>) {
//                            requestResponse = t.any as ResponseUploadImage
//                            setResults(requestResponse.uRL!!)
                            try {
                                imagePaths = t.any as ArrayList<String>
                                setResults(imagePaths)
                            } catch (e: java.lang.Exception) {
                                logD(APP_TAG, "exception caught while getting Array")
                            }

                        }
                    }
                } else {

                }
            }

        }
        viewModel.requestHandlerMLD.observe(this, requestObserver)
    }

    public fun setResults(arrayList: ArrayList<String>) {
        val data = Intent()
        data.putStringArrayListExtra(INTENT_CNIC_IMAGE_URLS, arrayList)
//        data.putExtra(INTENT_IMAGE_URL, fileAbsPath)
        setResult(RESULT_OK, data)
//---close the activity---
        finish();
    }

    @Synchronized
    private fun saveImage(finalBitmap2: Bitmap) {
        var finalBitmap = finalBitmap2

        try {
            if (isFront) {
                frontFile = createImageFile()
                val out = FileOutputStream(frontFile)
                logD(
                    APP_TAG,
                    "First Image :: height : ${finalBitmap.height} , width : ${finalBitmap.width}"
                )
                logD(APP_TAG, "First Image :: byte count : ${finalBitmap.byteCount}")

                finalBitmap = getResizedBitmap(finalBitmap, IMAGE_W_H)
                finalBitmap.compress(CompressFormat.JPEG, IMAGE_COMPRESS_Q, out)
                logD(APP_TAG, "after resize")
                logD(
                    APP_TAG,
                    "First Image :: height : ${finalBitmap.height} , width : ${finalBitmap.width}"
                )
                logD(APP_TAG, "First Image :: byte count : ${finalBitmap.byteCount}")
                out.flush()
                out.close()
                fileAbsPath = frontFile!!.path
                imagePaths.add(0, fileAbsPath)

                isFront = false
                setCaption()
            } else {
                backFile = createImageFile()
                val out = FileOutputStream(backFile)
                logD(
                    APP_TAG,
                    "second Image :: height : ${finalBitmap.height} , width : ${finalBitmap.width}"
                )
                logD(APP_TAG, "second Image :: byte count : ${finalBitmap.byteCount}")
                finalBitmap = getResizedBitmap(finalBitmap, IMAGE_W_H)
                finalBitmap.compress(CompressFormat.JPEG, IMAGE_COMPRESS_Q, out)
                logD(APP_TAG, "after resize")
                logD(
                    APP_TAG,
                    "second Image :: height : ${finalBitmap.height} , width : ${finalBitmap.width}"
                )
                logD(APP_TAG, "second Image :: byte count : ${finalBitmap.byteCount}")

                fileAbsPath = backFile!!.path
                imagePaths.add(1, fileAbsPath)
//                logD(APP_TAG,"Back File space after compress: ${backFile!!.totalSpace}")
                showPDialog()
                if (imagePaths.size > 1) {

                    viewModel.uploadCNICImages(frontFile!!, backFile!!)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setCaption() {
        scope.launch(Dispatchers.Main) {
            if (isFront) {
                binding.upperLabel.text = getString(R.string.scan_upper_caption_front)
            } else {
                if (this@ActCaptureCNIC::dialogP.isInitialized)
                    if (dialogP.isAdded)
                        dialogP.dismiss()
                binding.upperLabel.text = getString(R.string.scan_upper_caption_back)
            }
        }

    }


    @NonNull
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE"

        if (isFront) {
            imageFileName = CNIC_FRONT + timeStamp + "_"
        } else {
            imageFileName = CNIC_BACK + timeStamp + "_"
        }
        val storageDir: File =
            this@ActCaptureCNIC.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = image.absolutePath
        return image
    }

    @Synchronized
    private fun showPDialog() {
        timer.start()
        dialogP = ProgressDialog.newInstance()
        dialogP.showAllowingStateLoss(supportFragmentManager, "progress")
        dialogP.isCancelable = false
    }

    private fun dismissDialog() {
        scope.launch(Dispatchers.Main) {
            if (this@ActCaptureCNIC::dialogP.isInitialized) {
                if (dialogP?.isAdded!!)
                    dialogP?.dismiss()
            }
        }
    }

    fun checkFlashAndCameraCount() {
        /*if (Camera.getNumberOfCameras() > 1) {
            binding..setVisibility(View.VISIBLE);
        }*/
        if (!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH
            )
        ) {
            binding.turnOn.visible()
        } else {
            binding.turnOn.invisible()

        }
    }

    override fun onClick(view: View, obj: Any) {
        when (view.id) {

            R.id.back -> {
                onBackPressed()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
            R.id.gallery -> {
                galleryImage()
            }
            R.id.captureImage -> {
                clickImage()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
            R.id.turnOn -> {

//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
        }
    }

    @Synchronized
    private fun clickImage() {
        if (mCameraSource != null) {
            showPDialog()

            mCameraSource!!.takePicture( /*shutterCallback*/null,
                CameraSource.PictureCallback { bytes ->
//                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val orientation: Int = Exif.getOrientation(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    var bitmapPicture: Bitmap? = null
                    when (orientation) {
                        90 -> {
                            bitmapPicture = rotateImage(bitmap, 90f)
                        }
                        180 -> {
                            bitmapPicture = rotateImage(bitmap, 180f)
                        }

                        270 -> {
                            bitmapPicture = rotateImage(bitmap, 270f)
                        }
                        0 -> {
                            bitmapPicture = bitmap
                        }
                    }
                    //write your code here to save bitmap
                    /*  if (bitmapPicture != null)
                          showAlert(bitmapPicture)*/


                    try {
                        dismissDialog()
                        if (bitmapPicture != null) {
//                            bitmapPicture = getResizedBitmap(bitmapPicture)
                            detectText(bitmapPicture!!)
                        }
                    } catch (e: Exception) {
                    }
                })
        }
    }

    @Synchronized
    fun galleryImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_GALLERY_IMAGE)
//        if (Build.VERSION.SDK_INT <= 19) {
//            val i = Intent()
//            i.type = "image/*"
//            i.action = Intent.ACTION_GET_CONTENT
//            i.addCategory(Intent.CATEGORY_OPENABLE)
//            startActivityForResult(i, PICK_GALLERY_IMAGE)
//        } else if (Build.VERSION.SDK_INT > 19) {
//            val intent = Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            )
//            startActivityForResult(intent, PICK_GALLERY_IMAGE)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_IMAGE) {
                var thumbnail: Bitmap?
                val selectedImageUri = data!!.data
                showPDialog()

                scope.launch(Dispatchers.Default) {
                    setDetails(selectedImageUri!!)
                }
                /*  logD(
                      APP_TAG,
                      "path of image from gallery......******************........." +
                              selectedImagePath + ""
                  )*/
            }
        }
    }

    fun setDetails(uri: Uri) {

        var thumbnail: Bitmap?
        val selectedImagePath = uriToFilename(uri!!)
        thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            /*ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    this@ActCaptureCNIC.contentResolver,
                    uri
                )
            )*/
            MediaStore.Images.Media.getBitmap(
                this@ActCaptureCNIC.contentResolver,
                uri
            )
//                   BitmapFactory.decodeFile(selectedImagePath)
        } else {
            MediaStore.Images.Media.getBitmap(
                this@ActCaptureCNIC.contentResolver,
                uri
            )
        }


        scope.launch(Dispatchers.Main) {

            if (thumbnail != null) {
//                thumbnail = getResizedBitmap(thumbnail!!)
                detectText(thumbnail!!)
//                showAlert(thumbnail!!)
//                saveImage(thumbnail!!)
            } else {
                showShort(this@ActCaptureCNIC, "Unable to Decode file")
                dismissDialog()
            }
        }

    }


    private fun uriToFilename(uri: Uri): String? {
        var path: String? = null
        path = if (Build.VERSION.SDK_INT < 11) {
            RealPathUtil.getRealPathFromURI_BelowAPI11(this, uri)
        } else if (Build.VERSION.SDK_INT < 19) {
            RealPathUtil.getRealPathFromURI_API11to18(this, uri)
        } else {
            RealPathUtil.getRealPathFromURI_API19(this, uri)
        }
        return path
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != requestCAMERA) {
            Log.d(
                APP_TAG,
                "Got unexpected permission result: $requestCode"
            )
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@ActCaptureCNIC,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mCameraSource!!.start(binding.surfaceView!!.holder)
                //flashOnButton();
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
        }
    }

}