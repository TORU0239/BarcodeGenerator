package sg.toru.barcodegenerator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var ed:EditText

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    private lateinit var submitBtn:Button
    private lateinit var barcodeImg: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        submitBtn = findViewById(R.id.btn_generate_code)
        submitBtn.setOnClickListener {
            triggerBarcodeGenerator()
        }

        ed = findViewById(R.id.ed_number)
        ed.addTextChangedListener {
            submitBtn.isEnabled = it.toString().isNotEmpty()
        }
        barcodeImg = findViewById(R.id.img_generated_barcode)
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    private fun triggerBarcodeGenerator(){
        coroutineScope.launch{
            val test = generateBarcode(ed.text.toString()).await()
            test?.let { barcode ->
                barcodeImg.setImageBitmap(barcode)
            }
        }
    }

    private fun generateBarcode(digits:String) = coroutineScope.async {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val barcodeMatrix = multiFormatWriter.encode(digits, BarcodeFormat.CODE_128, 300, 200)
            val barcodeEncoder = BarcodeEncoder()
            return@async barcodeEncoder.createBitmap(barcodeMatrix)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return@async null
    }
}