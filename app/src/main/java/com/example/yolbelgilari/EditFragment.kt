package com.example.yolbelgilari

import Adapters.ViewPager2Adapter
import DB.MyDBHelper
import Models.Belgi
import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.yolbelgilari.databinding.FragmentAddBinding
import com.example.yolbelgilari.databinding.FragmentEditBinding
import com.github.florent37.runtimepermission.kotlin.askPermission
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentEditBinding
    lateinit var myDbHelper: MyDBHelper
    lateinit var belgi: Belgi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBinding.inflate(LayoutInflater.from(context))


        binding.imageAdd.setOnClickListener {
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
        return binding.root
    }
    var absolutePath = ""
    val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
            uri ?: return@registerForActivityResult
            binding.imageAdd.setImageURI(uri)
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

    override fun onStart() {
        super.onStart()
        myDbHelper = MyDBHelper(context)
        belgi = arguments?.getSerializable("label") as Belgi
        val viewPager2BelgiAdapter = arguments?.getSerializable("adapter") as ViewPager2Adapter
        binding.edtBelgiNomi.setText(belgi.name)
        binding.edtBelgiTarifi.setText(belgi.matni)
        binding.spinnerTuri.setSelection(belgi.category)
        binding.imageAdd.setImageURI(Uri.parse(belgi.imagePath))
        absolutePath = belgi.imagePath!!

        binding.btnSave.setOnClickListener {

            val labelName = binding.edtBelgiNomi.text.toString().trim()
            val labelTarifi = binding.edtBelgiTarifi.text.toString().trim()
            val category = binding.spinnerTuri.selectedItemPosition

            if (absolutePath!="" && labelName!="" && labelTarifi!=""){
                belgi.name = labelName
                belgi.matni = labelTarifi
                belgi.category = binding.spinnerTuri.selectedItemPosition
                belgi.imagePath = absolutePath
                myDbHelper.editLabel(belgi)
                viewPager2BelgiAdapter.notifyDataSetChanged()
                Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show()
                onDestroy()
            }else{
                Toast.makeText(context, "Ma'lumot yetarli emas", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}