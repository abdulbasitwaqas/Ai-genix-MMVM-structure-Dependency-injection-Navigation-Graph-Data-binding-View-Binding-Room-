package com.jsbl.genix.views.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityCameraCaptureBinding
import com.jsbl.genix.model.registration.ResponseUploadImage
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.extensions.showCaptureAlert
import com.jsbl.genix.viewModel.CameraCaptureViewModel
import com.jsbl.genix.views.activities.ActCaptureCNIC.Companion.PICK_GALLERY_IMAGE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_IMAGE_URL
import com.jsbl.genix.views.dialogs.ProgressDialog
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okio.IOException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ActCaptureCamera : AppCompatActivity(), OnViewClickListener {

    // new changes

    companion object {
        private val LOG = CameraLogger.create(APP_TAG)
        private const val USE_FRAME_PROCESSOR = false
        private const val DECODE_BITMAP = false
        const val PROFILE_IMAGE = "PROFILE_"
        const val IMAGE_W_H = 1500
        const val IMAGE_COMPRESS_Q = 30

    }

    var fileAbsPath: String = ""


    private lateinit var binding: ActivityCameraCaptureBinding
    private lateinit var viewModel: CameraCaptureViewModel
    private lateinit var requestObserver: Observer<RequestHandler>

    private var cameraId = 0
    private var flashmode = false
    private var rotation = 0

    private var captureTime: Long = 0
    private var currentFilter = 0
    lateinit var dialogP: ProgressDialog
    var imagePaths = arrayListOf<String>()


    private lateinit var scope: CoroutineScope
    private var job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.onClickListener = this
//        binding.surfaceView.holder.addCallback(this)
        scope = CoroutineScope(Dispatchers.Main + job)
        viewModel = ViewModelProvider(this).get(CameraCaptureViewModel::class.java)
        setRequestHandler()
        checkFlashAndCameraCount()
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        binding.cameraView.setLifecycleOwner(this)
        binding.cameraView.addCameraListener(Listener())
        if (USE_FRAME_PROCESSOR) {
            binding.cameraView.addFrameProcessor(object : FrameProcessor {
                private var lastTime = System.currentTimeMillis()
                override fun process(frame: Frame) {
                    val newTime = frame.time
                    val delay = newTime - lastTime
                    lastTime = newTime
                    LOG.v("Frame delayMillis:", delay, "FPS:", 1000 / delay)
                    if (DECODE_BITMAP) {
                        if (frame.format == ImageFormat.NV21
                            && frame.dataClass == ByteArray::class.java
                        ) {
                            val data = frame.getData<ByteArray>()
                            val yuvImage = YuvImage(
                                data,
                                frame.format,
                                frame.size.width,
                                frame.size.height,
                                null
                            )
                            val jpegStream = ByteArrayOutputStream()
                            yuvImage.compressToJpeg(
                                Rect(
                                    0, 0,
                                    frame.size.width,
                                    frame.size.height
                                ), 100, jpegStream
                            )
                            val jpegByteArray = jpegStream.toByteArray()
                            val bitmap = BitmapFactory.decodeByteArray(
                                jpegByteArray,
                                0, jpegByteArray.size
                            )
                            bitmap.toString()
                        }
                    }
                }
            })
        }
    }

    lateinit var requestResponse: ResponseUploadImage

    fun setRequestHandler() {
        requestObserver = object : Observer<RequestHandler> {
            override fun onChanged(t: RequestHandler?) {

                if (t != null) {

                    if (t.loading && !t.isSuccess) {
                    } else if (!t.loading && !t.isSuccess) {
                        dismissDialog()
                        logout(t.any!!, this@ActCaptureCamera)
                    } else if (!t.loading && t.isSuccess) {
                        dismissDialog()
                        //TODO
                        if (t.any is ArrayList<*>) {
//                            requestResponse = t.any as ResponseUploadImage
//                            setResults(requestResponse.uRL!!)
                            try {
                                imagePaths = t.any as ArrayList<String>
                                setResults(imagePaths[0])
                            } catch (e: java.lang.Exception) {
                                logD(APP_TAG, "exception caught while getting Array")
                            }

                        }
                        if (t.any is ResponseUploadImage) {
                            requestResponse = t.any as ResponseUploadImage

                        }
                    }
                } else {
                    dismissDialog()
                }
            }

        }
        viewModel.requestHandlerMLD.observe(this, requestObserver)
    }

    public fun setResults(string: String) {
        val data = Intent()
        data.putExtra(INTENT_IMAGE_URL, string)
//        data.putExtra(INTENT_IMAGE_URL, fileAbsPath)
        setResult(RESULT_OK, data)
//---close the activity---
        finish();
    }


    private inner class Listener : CameraListener() {
        override fun onCameraOpened(options: CameraOptions) {
            /* val group = controlPanel.getChildAt(0) as ViewGroup
             for (i in 0 until group.childCount) {
                 val view = group.getChildAt(i) as OptionView<*>
                 view.onCameraOpened(camera, options)
             }*/
        }

        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
            dismissDialog()
//            message("Got CameraException #" + exception.reason, true)
        }

        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            if (binding.cameraView.isTakingVideo) {
//                message("Captured while taking video. Size=" + result.size, false)
                return
            }

            // This can happen if picture was taken with a gesture.
            val callbackTime = System.currentTimeMillis()
            if (captureTime == 0L) captureTime = callbackTime - 300
            LOG.w("onPictureTaken called! Launching activity. Delay:", callbackTime - captureTime)

            try {
                result.toBitmap(IMAGE_W_H, IMAGE_W_H) { bitmap ->
//                    showAlert(bitmap!!)
                    bitmap?.let {
                        showCaptureAlert(this@ActCaptureCamera, onPositiveClick = {
                            saveImage(it)
                        }, onNegativeClick = {
                            dismissDialog()
                        }, bitmap = it)
                    }
//                    saveImage(bitmap!!)
                }
            } catch (e: UnsupportedOperationException) {
               dismissDialog()
            }

            /* PicturePreviewActivity.pictureResult = result
             val intent = Intent(this@CameraCapture, PicturePreviewActivity::class.java)
             intent.putExtra("delay", callbackTime - captureTime)
             startActivity(intent)*/
            captureTime = 0
            LOG.w("onPictureTaken called! Launched activity.")
        }


        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            LOG.w("onVideoTaken called! Launching activity.")
            /* VideoPreviewActivity.videoResult = result
             val intent = Intent(this@CameraActivity, VideoPreviewActivity::class.java)
             startActivity(intent)
            */ LOG.w("onVideoTaken called! Launched activity.")
        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            LOG.w("onVideoRecordingStart!")
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
//            message("Video taken. Processing...", false)
            LOG.w("onVideoRecordingEnd!")
        }

        override fun onExposureCorrectionChanged(
            newValue: Float,
            bounds: FloatArray,
            fingers: Array<PointF>?
        ) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
//            message("Exposure correction:$newValue", false)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
//            message("Zoom:$newValue", false)
        }
    }

    @Synchronized
    private fun saveImage(finalBitmap: Bitmap) {

        try {
            val file = createImageFile()
            val out = FileOutputStream(file)
            finalBitmap.compress(CompressFormat.JPEG, IMAGE_COMPRESS_Q, out)
            out.flush()
            out.close()
            fileAbsPath = file!!.path
            viewModel.uploadImage(file!!, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @NonNull
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = PROFILE_IMAGE + timeStamp + "_"
        val storageDir: File =
            this@ActCaptureCamera.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
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
        dialogP = ProgressDialog.newInstance()
        dialogP.showAllowingStateLoss(supportFragmentManager, "progress")
        dialogP.isCancelable = false
    }

    fun dismissDialog() {
        if (this@ActCaptureCamera::dialogP.isInitialized)
            if (dialogP.isAdded)
                dialogP.dismiss()
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
//                showPDialog()
                capturePicture()
//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
            R.id.turnOn -> {

//                startActivity(Intent(this@CameraCapture, Verification::class.java))

            }
        }
    }

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
//            ImageDecoder.decodeBitmap(
//                ImageDecoder.createSource(
//                    this@ActCaptureCamera.contentResolver,
//                    uri
//                )
//            )
            MediaStore.Images.Media.getBitmap(
                this@ActCaptureCamera.contentResolver,
                uri
            )
//                   BitmapFactory.decodeFile(selectedImagePath)
        } else {
            MediaStore.Images.Media.getBitmap(
                this@ActCaptureCamera.contentResolver,
                uri
            )
        }

        scope.launch(Dispatchers.Main) {
            dismissDialog()
            if (thumbnail != null) {
                thumbnail = getResizedBitmap(thumbnail!!, IMAGE_W_H)
                saveImage(thumbnail!!)
            } else {
                showShort(this@ActCaptureCamera, "Unable to Decode file")
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


    private fun capturePicture() {
        if (binding.cameraView.mode == Mode.VIDEO) return run {
        }
        if (binding.cameraView.isTakingPicture) return
        captureTime = System.currentTimeMillis()
        showPDialog()
        binding.cameraView.takePicture()
    }


}