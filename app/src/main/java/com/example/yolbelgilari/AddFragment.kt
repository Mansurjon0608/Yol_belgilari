package com.example.yolbelgilari

import DB.MyDBHelper
import Models.Belgi
import android.Manifest
import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.yolbelgilari.databinding.FragmentAddBinding
import com.github.florent37.runtimepermission.kotlin.askPermission
import java.io.File
import java.io.FileOutputStream
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddFragment : Fragment() {



    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentAddBinding
    lateinit var myDbHelper: MyDBHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddBinding.inflate(LayoutInflater.from(context))
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding.imageBack.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.imageSign.setOnClickListener {
            askPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                //all permissions already granted or just granted
                getImageContent.launch("image/*")
            }.onDeclined { e ->
                if (e.hasDenied()) {

                    AlertDialog.Builder(context)
                        .setMessage("Please accept our permissions")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain();
                        } //ask again
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss();
                        }
                        .show();
                }

                if (e.hasForeverDenied()) {
                    //the list of forever denied permissions, user has check 'never ask again'

                    // you need to open setting manually if you really need it
                    e.goToSettings();
                }
            }

        }

        return  binding.root
    }
    var absolutePath = ""
    @RequiresApi(Build.VERSION_CODES.N)
    val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
            uri ?: return@registerForActivityResult
            binding.imageSign.setImageURI(uri)
            val resolver = requireActivity().contentResolver
            val inputStream = resolver?.openInputStream(uri)
            val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(activity?.filesDir, "$format.jpg")
            val fileOutputStream = FileOutputStream(file)
            inputStream?.copyTo(fileOutputStream)
            inputStream?.close()
            fileOutputStream.close()
            absolutePath = file.absolutePath
            Toast.makeText(context, "$absolutePath", Toast.LENGTH_SHORT).show()
        }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onStart() {
        super.onStart()
        Log.d("OnStart", "QoshishFragment")
        myDbHelper = MyDBHelper(context)
        binding.btnSave.setOnClickListener {

            val labelName = binding.belgiNomi.text.toString().trim()
            val labelTarifi = binding.belgiTarifi.text.toString().trim()
            val category = binding.spinnerTurlar.selectedItemPosition

            if (absolutePath!="" && labelName!="" && labelTarifi!=""){
                myDbHelper.addLabel(Belgi(labelName, labelTarifi, absolutePath, 0, category))
                Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.homeFragment)
            }else{
                Toast.makeText(context, "Ma'lumot yetarli emas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
