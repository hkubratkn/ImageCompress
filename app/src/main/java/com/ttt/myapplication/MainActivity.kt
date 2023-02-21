package com.ttt.myapplication

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.ttt.myapplication.ui.theme.MyApplicationTheme
import java.io.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen(){
    val context = LocalContext.current
    var imageUrl by remember { mutableStateOf<Uri?>(null)}
    var bitmap by remember { mutableStateOf<Bitmap?>(null)}

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){ uri: Uri? ->
        imageUrl = uri
    }


    Column(){
        Button(
            onClick = {
                launcher.launch("image/*")
            }
        ){Text("hello")}

        imageUrl?.let {
            if (Build.VERSION.SDK_INT < 28){
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
            bitmap.let { bitmap ->
                val kucukBitmap = kucukBitmapOlustur(bitmap!!,300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                val byteDizisi = outputStream.toByteArray()

                FirebaseStorage.getInstance().reference.child("deneme").child("second.jpg")
                    .putBytes(byteDizisi)
//                    .putFile(imageUrl!!).toString().toUri()
                    .addOnCompleteListener {
                        Toast.makeText(context, "done", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}
/**
                statement.bindBlob(3,byteDizisi)
                statement.execute()

                byte [] buf = <your byte array>;
String s = new String(buf, "UTF-8");
Uri uri = Uri.parse(s);
*/

fun kucukBitmapOlustur(kullanicininSectigiBitmap: Bitmap, maximumBoyut: Int) : Bitmap {

    var width = kullanicininSectigiBitmap.width
    var height = kullanicininSectigiBitmap.height

    val bitmapOrani : Double = width.toDouble() / height.toDouble()

    if (bitmapOrani > 1) {
        // görselimiz yatay
        width = maximumBoyut
        val kisaltilmisHeight = width / bitmapOrani
        height = kisaltilmisHeight.toInt()
    } else {
        //görselimiz dikey
        height = maximumBoyut
        val kisaltilmisWidth = height * bitmapOrani
        width = kisaltilmisWidth.toInt()

    }


    return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
}



/**
private fun compressAndSetImage(result: Uri, context: Context){
    val activity = context as Activity
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + job)
    val fileUri = getFilePathFromUri(result, context!!)
    uiScope.launch {
        val compressedImageFile = Compressor.compress(context!!, File(fileUri!!.path)){
            quality(50) // combine with compressor constraint
            format(Bitmap.CompressFormat.JPEG)
        }
        val resultUri = Uri.fromFile(compressedImageFile)

        activity!!.runOnUiThread {
            resultUri?.let {
                //set image here

            }
        }
    }
}

@Throws(IOException::class)
fun getFilePathFromUri(uri: Uri?, context: Context?): Uri? {
    val fileName: String? = getFileName(uri, context)
    val file = File(context?.externalCacheDir, fileName)
    file.createNewFile()
    FileOutputStream(file).use { outputStream ->
        context?.contentResolver?.openInputStream(uri!!).use { inputStream ->
            copyFile(inputStream, outputStream)
            outputStream.flush()
        }
    }
    return Uri.fromFile(file)
}

@Throws(IOException::class)
private fun copyFile(`in`: InputStream?, out: OutputStream) {
    val buffer = ByteArray(1024)
    var read: Int? = null
    while (`in`?.read(buffer).also({ read = it!! }) != -1) {
        read?.let { out.write(buffer, 0, it) }
    }
}//copyFile ends

fun getFileName(uri: Uri?, context: Context?): String? {
    var fileName: String? = getFileNameFromCursor(uri, context)
    if (fileName == null) {
        val fileExtension: String? = getFileExtension(uri, context)
        fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""
    } else if (!fileName.contains(".")) {
        val fileExtension: String? = getFileExtension(uri, context)
        fileName = "$fileName.$fileExtension"
    }
    return fileName
}

fun getFileExtension(uri: Uri?, context: Context?): String? {
    val fileType: String? = context?.contentResolver?.getType(uri!!)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

fun getFileNameFromCursor(uri: Uri?, context: Context?): String? {
    val fileCursor: Cursor? = context?.contentResolver
        ?.query(uri!!, arrayOf<String>(OpenableColumns.DISPLAY_NAME), null, null, null)
    var fileName: String? = null
    if (fileCursor != null && fileCursor.moveToFirst()) {
        val cIndex: Int = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cIndex != -1) {
            fileName = fileCursor.getString(cIndex)
        }
    }
    return fileName
}*/