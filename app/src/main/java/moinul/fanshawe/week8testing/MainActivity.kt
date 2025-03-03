package moinul.fanshawe.week8testing

import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.alarm -> {
                changeFragment(AlarmFragment())
                Toast.makeText(this, "Alarm clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.calling -> {
                changeFragment(CallFragment())
                Toast.makeText(this, "Call clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.web -> {
                changeFragment(WebFragment())
                Toast.makeText(this, "Web clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.file -> {
                changeFragment(FileFragment())
                Toast.makeText(this, "File clicked", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()

    }

    fun onSetAlarm(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                // Show selected time as toast
                Toast.makeText(this, "Selected Time: $time", Toast.LENGTH_SHORT).show()
                setAlarm(selectedHour, selectedMinute)
            }, hour, minute, false)
        timePickerDialog.show()
    }

    private fun setAlarm(hour: Int, minute: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "Office Meeting")
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false) // Optionally skip the UI of the alarm clock
        }
        startActivity(intent)
    }

    fun onClickCall(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as CallFragment

        fragment.view?.let { fragmentView ->
            val editText = fragmentView.findViewById<EditText>(R.id.editTextCall)
            val callIntent: Intent = Uri.parse("tel:${editText.text.toString()}").let { number ->
                Intent(Intent.ACTION_DIAL, number)
            }
            startActivity(callIntent)
        }
    }
    fun onClickWeb(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as WebFragment

        fragment.view?.let { fragmentView ->
            val editText = fragmentView.findViewById<EditText>(R.id.editTextWeb)
            val webIntent: Intent = Uri.parse("https://${editText.text.toString()}").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)

        }
    }
    fun onClickFile(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as FileFragment

        fragment.view?.let { fragmentView ->
            val editText = fragmentView.findViewById<EditText>(R.id.editTextFile)

            try {
                // to save to file "test.txt" in data/data/packagename/File
                val ofile = openFileOutput("data.txt", MODE_PRIVATE)
                val osw = OutputStreamWriter(ofile)
                osw.write(editText.getText().toString())
                osw.flush()
                osw.close()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }

            val file = File(this.filesDir, "data.txt")

            // Generate the URI for the file using the FileProvider
            val uri = FileProvider.getUriForFile(
                this,
                "${this.packageName}.provider",
                file
            )

            // Create the share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/*" // Or the appropriate MIME type of the file
                putExtra(Intent.EXTRA_STREAM, uri)
                // Grant temporary read permission to the content URI
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //addCategory(Intent.CATEGORY_OPENABLE)
                //for Intent.ACTION_SEND, the category CATEGORY_OPENABLE is not usually necessary because this action indicates you're sending data to another component, not requesting data that needs to be openable. The recipient app will handle the content URI as it sees fit.
            }

            // Create a chooser intent
            val chooserIntent = Intent.createChooser(shareIntent, "Share File")



            // Try to invoke the intent.
            try {
                startActivity(chooserIntent)
            } catch (e: ActivityNotFoundException) {
                // Define what your app should do if no activity can handle the intent.
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

        }
    }

}