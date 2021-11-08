package com.jsbl.genix.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ActivityScanCnicBinding
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.RealPathUtil
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.logD
import com.jsbl.genix.utils.showShort
import com.jsbl.genix.views.activities.ActCaptureCNIC.Companion.PICK_GALLERY_IMAGE
import com.jsbl.genix.views.activities.ActivityRegistration.Companion.INTENT_CNIC
import com.jsbl.genix.views.dialogs.ProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class ActivityScanCNIC : AppCompatActivity(), OnViewClickListener {
    var mCameraView: SurfaceView? = null
    var mCameraSource: CameraSource? = null
    var textView7: TextView? = null
    var isFlashEnable = false
    lateinit var textRecognizer: TextRecognizer
    lateinit var textRecognizerGallery: TextRecognizer
    private lateinit var binding: ActivityScanCnicBinding

    private lateinit var scope: CoroutineScope
    private var job = Job()
    var itemdetected = false

    private var dialogP: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCnicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.onClickListener = this
        init()
        startCameraSource()
        setScanningText()
    }

    private fun init() {
        mCameraView = findViewById(R.id.surfaceView)
        scope = CoroutineScope(Dispatchers.Main + job)
    }

    @Synchronized
    private fun showPDialog() {
        scope.launch(Dispatchers.Main) {
            dialogP = ProgressDialog.newInstance()
            dialogP?.showAllowingStateLoss(supportFragmentManager, "progress")
            dialogP?.isCancelable = false
        }
    }

    private fun dismissDialog() {
        scope.launch(Dispatchers.Main) {
            if (dialogP != null) {
                if (dialogP?.isAdded!!)
                    dialogP?.dismiss()
            }
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
//                imageChooser()
            }
            R.id.turnOn -> {
//                startActivity(Intent(this@CameraCapture, Verification::class.java))
            }
        }
    }

    fun setScanningText() {
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            var count = 0
            override fun run() {
                count++
                if (count == 1) {
                    binding.scanning.setText("Scanning.")
                } else if (count == 2) {
                    binding.scanning.setText("Scanning..")
                } else if (count == 3) {
                    binding.scanning.setText("Scanning...")
                }
                if (count == 3) count = 0
                handler.postDelayed(this, 1 * 500)
            }
        }
        handler.postDelayed(runnable, 1 * 500)
    }

    @Synchronized
    fun detectText(bitmapText: Bitmap) {
//        val textBitmap = BitmapFactory.decodeResource(resources, R.drawable.cat)

        scope.launch(Dispatchers.Main) {
            showPDialog()
        }
        textRecognizerGallery = TextRecognizer.Builder(this@ActivityScanCNIC).build()
        if (!textRecognizerGallery.isOperational) {
            dismissDialog()
            return
        }
        val frame: Frame = Frame.Builder().setBitmap(bitmapText).build()
        val text: SparseArray<TextBlock> = textRecognizerGallery.detect(frame)
        if (text.size() == 0) {
//                showShort(this@ScanCNICActivity, "Unable to Decode file")
            scope.launch(Dispatchers.Main) {
                dismissDialog()
                showShort(this@ActivityScanCNIC, "No Text Detected")

            }

            return
        }
        sortTextBlocks(items = text, fromScan = false)
    }


    @Synchronized
    fun sortTextBlocks(items: SparseArray<TextBlock>, fromScan: Boolean) {
        if (items.size() != 0) {
            logD(APP_TAG, "Some Text found")
            for (i in 0 until items.size()) {
                Log.d(APP_TAG, items.valueAt(i).value)
                if (cnicDetector(items.valueAt(i).value)) {
                    Log.d(
                        APP_TAG,
                        "CNIC got" + items.valueAt(i).value
                    )
                    /* val splitted = items.valueAt(i).value.split("-")
                     if (splitted.size == 3) {
                         setResults(splitted[0] + splitted[1] + splitted[2])
                     }*/
                    setResults(cnicValue!!.replace("-", ""))
//                                setResults(items.valueAt(i).value)
                    itemdetected = false
                    return
                }
            }
        }
        itemdetected = false
        scope.launch(Dispatchers.Main) {
            dismissDialog()
            if (!fromScan)
                showShort(this@ActivityScanCNIC, "No CNIC Detected")

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != requestPermissionID) {
            Log.d(
                TAG,
                "Got unexpected permission result: $requestCode"
            )
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@ActivityScanCNIC,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mCameraSource!!.start(mCameraView!!.holder)
                //flashOnButton();
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun startCameraSource() {

        //Create the TextRecognizer
        textRecognizer = TextRecognizer.Builder(this@ActivityScanCNIC).build()
        if (!textRecognizer.isOperational) {
            Log.w(
                TAG,
                "Detector dependencies not loaded yet"
            )
        } else {
            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = CameraSource.Builder(this@ActivityScanCNIC, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()
            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView!!.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(
                                this@ActivityScanCNIC,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@ActivityScanCNIC,
                                arrayOf(Manifest.permission.CAMERA),
                                requestPermissionID
                            )
                            return
                        }
                        mCameraSource!!.start(mCameraView!!.holder)


                        // flashOnButton();
                    } catch (e: IOException) {
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

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {}

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 */
                override fun receiveDetections(detections: Detections<TextBlock>) {
                    val items = detections.detectedItems
                    if (!itemdetected) {
                        itemdetected = true
                        scope.launch(Dispatchers.Default) {
                            sortTextBlocks(items, true)
                        }
                    }
                }
            })
        }
    }

    @Synchronized
    public fun setResults(string: String) {
        scope.launch(Dispatchers.Main) {

            dismissDialog()
            val data = Intent()
            data.putExtra(INTENT_CNIC, string)
            setResult(RESULT_OK, data)
//---close the activity---
            finish()
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
                    if (str2.matches(reg)) {
                        storeCNIC(str2)
                        return true
                    }
                }
            }
        } else {
            if (string.trim().matches(reg)) {
                storeCNIC(string.trim())
                return true
            }
        }

        //        String reg_overseas = "^[0-9+]{6}-[0-9+]{6}-[0-9]{1}$";

        return false
        // check the lenght of the enter data in EditText and give error if its empty
    }

    private fun cnicDetectorOverSeas(string: String): Boolean {
        val reg = "^[0-9+]{5}-[0-9+]{7}-[0-9]{1}$".toRegex()
        if (string.matches(reg)) {
            storeCNICOverSeas(string)
            return true
        }
        return false
    }

    private fun storeCNIC(cnic: String) {
        cnicValue = cnic
        cnicDetected = true
    }

    private fun storeCNICOverSeas(cnic: String) {
        var mergeCnic = ""
        val part1: String
        val part2: String
        var secondPart = ""
        try {
            val parts =
                cnic.split("\\-").toTypedArray()

            //  Toast.makeText(this, "this "+parts[0]+" "+parts[1]+" "+parts[2], Toast.LENGTH_SHORT).show();
            part1 = parts[0].substring(0, 5)
            part2 = parts[0].substring(5, 6)

            //   Toast.makeText(this, " sub "+part1 +" "+part2, Toast.LENGTH_SHORT).show();
            secondPart = part2 + parts[1] + "-" + parts[2]
            mergeCnic = "$part1-$secondPart"

            //  Toast.makeText(this, "get "+mergeCnic, Toast.LENGTH_SHORT).show();
            cnicValue = mergeCnic
            cnicDetected = true
        }
        catch (e: Exception) {
        }



//        cnicValue = cnic;
//        cnicDetected = true;
//        mTextView.setText("cnic " + cnic);
    }

    private var camera: Camera? = null
    var flashmode = false
    private fun flashOnButton() {
        camera = getCamera(mCameraSource!!)
        if (camera != null) {
            try {
                val param = camera!!.parameters
                param.flashMode =
                    if (!flashmode) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF
                camera!!.parameters = param
                flashmode = !flashmode
                if (flashmode) {
//                    showToast("Flash Switched ON");
                    textView7!!.text = "TURN OFF"
                } else {
//                    showToast("Flash Switched Off");
                    textView7!!.text = "TURN ON"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun galleryImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_GALLERY_IMAGE)
//
//        if (Build.VERSION.SDK_INT <= 19) {
//            val i = Intent()
//            i.type = "image/*"
//            i.action = Intent.ACTION_GET_CONTENT
//            i.addCategory(Intent.CATEGORY_OPENABLE)
//            startActivityForResult(i, ActCaptureCamera.PICK_GALLERY_IMAGE)
//        } else if (Build.VERSION.SDK_INT > 19) {
//            val intent = Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            )
//            startActivityForResult(intent, ActCaptureCamera.PICK_GALLERY_IMAGE)
//        }
    }


    fun imageChooser() {

        // create an instance of the
        // intent of the type image
//        val i = Intent()
//        i.type = "image/*"
//        i.action = Intent.ACTION_GET_CONTENT
//
//        // pass the constant to compare it
//        // with the returned requestCode
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_GALLERY_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_IMAGE) {

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

    private fun setDetails(uri: Uri) {
        val selectedImagePath = uriToFilename(uri!!)
        var thumbnail: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            /*ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    this@ActivityScanCNIC.contentResolver,
                    uri
                )
            )*/
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

//            BitmapFactory.decodeFile(selectedImagePath)

        } else {
            MediaStore.Images.Media.getBitmap(
                this@ActivityScanCNIC.contentResolver,
                uri
            )
        }
//                    thumbnail = BitmapFactory.decodeFile(selectedImagePath)

        dismissDialog()
        if (thumbnail != null) {
//            thumbnail = getResizedBitmap(thumbnail!!, 400)
            /* val storageDir: File =
                 this@ScanCNICActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

             val out = FileOutputStream(File.createTempFile("image",".jpg",storageDir))
             thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out)
             out.flush()
             out.close()*/
            detectText(thumbnail!!)

        } else {
            scope.launch(Dispatchers.Main) {

                showShort(this@ActivityScanCNIC, "Unable to Decode file")

            }
        }

    }


    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
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


    companion object {
        var cnicDetected = false
        var cnicValue: String? = ""
        private const val TAG = "MainActivity"
        private const val requestPermissionID = 101
        private fun getCamera(cameraSource: CameraSource): Camera? {
            val declaredFields =
                CameraSource::class.java.declaredFields
            for (field in declaredFields) {
                if (field.type == Camera::class.java) {
                    field.isAccessible = true
                    try {
                        return field[cameraSource] as Camera
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                    break
                }
            }
            return null
        } //    public void dateParsers(String firstDate, String secondDate, String thirdDate){
        //        if(firstDate.contains(",")){
        //            String mergeArray="";
        //            String[] firstDateArray = firstDate.split(",");
        //
        //            try{
        //                mergeArray=firstDateArray[0]+"."+firstDateArray[1]+"."+firstDateArray[2];
        //            }
        //            catch(Exception e){
        //                issue.setText(mergeArray);
        //            }
        //        }
        //
        //        if(secondDate.contains(",")){
        //            String mergeArray="";
        //            String[] secondDateArray = secondDate.split(",");
        //
        //            try{
        //                mergeArray=secondDateArray[0]+"."+secondDateArray[1]+"."+secondDateArray[2];
        //            }
        //            catch(Exception e){
        //                fix.setText(mergeArray);
        //            }
        //        }
        //
        //        if(thirdDate.contains(",")){
        //            String mergeArray="";
        //            String[] thirdDateArray = thirdDate.split(",");
        //
        //            try{
        //                mergeArray=thirdDateArray[0]+"."+thirdDateArray[1]+"."+thirdDateArray[2];
        //            }
        //            catch(Exception e){
        //                fix2.setText(mergeArray);
        //            }
        //        }
        //    }
    }
}