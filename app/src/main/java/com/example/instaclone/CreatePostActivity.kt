package com.example.instaclone

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.instaclone.databinding.ActivityCreatePostBinding

private const val TAG = "CreatePostActivity"
private const val PICK_PHOTO_CODE = 1234

class CreatePostActivity : AppCompatActivity() {

    private var photoUri : Uri? = null

    private lateinit var binding : ActivityCreatePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgPickerBtn.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            val imgPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgPickerIntent.type = "image/*"

            // check if there is an application on the device to handle this
            // request. ie if there is a gallery on the device.
            if(imgPickerIntent.resolveActivity(packageManager) != null ){
                startActivityForResult(imgPickerIntent, PICK_PHOTO_CODE)
            }
            else Log.i(TAG, "no app to handle the image request on this device")


        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_CODE){
            if (resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                Log.i(TAG, "photoUri: $photoUri")
                binding.imageIV.setImageURI(photoUri)

            }
            else{
                Toast.makeText(this, "Image picker action canceled", Toast.LENGTH_LONG).show()
            }

        }
    }

}