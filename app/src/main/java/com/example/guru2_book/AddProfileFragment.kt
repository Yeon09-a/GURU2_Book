package com.example.guru2_book

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView


class AddProfileFragment : Fragment() {

    // 기본 정보 변수
    private var userEmail : String? = null // 사용자 이메일
    private var profileNum : Int = 0 // 사용자 프로필 번호

    // activity, context 변수
    var activity : MainActivity? = null
    lateinit var fContext : Context

    // 위젯 변수
    lateinit var edtName : EditText // 프로필 이름 에디트텍스트
    lateinit var profileImage : CircleImageView // 프로필 사진 이미지뷰
    lateinit var btnBack : Button // 뒤로가기 버튼
    lateinit var btnOkay : Button // 추가 버튼

    // 데이터베이스 변수
    lateinit var dbManager: DBManager
    lateinit var bookDB : SQLiteDatabase

    // 기타 변수
    var imageUri: Uri? = null // 프로필 이미지 uri

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as MainActivity // MainActivity 가져오기
        fContext = context // content 가져오기
    }

    override fun onDetach() {
        super.onDetach()
        activity = null // activity null로 설정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("USEREMAIL") // 사용자 이메일 받기
            profileNum = it.getInt("PROFILENUM") // 프로필 번호 받기
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_profile, container, false)

        dbManager = DBManager(activity, "BookDB", null, 1) // SQLiteOpenHelper 객체 생성

        // 위젯 연결
        edtName = view.findViewById<EditText>(R.id.edtName)
        profileImage = view.findViewById<CircleImageView>(R.id.profileImage)
        btnBack = view.findViewById<Button>(R.id.btnBack)
        btnOkay = view.findViewById<Button>(R.id.btnOkay)

        // 리스너 연결
        btnBack.setOnClickListener { // 뒤로가기 버튼
            activity?.fragmentChangeInFragment(MyPageFragment.newInstance(userEmail)) // 마이페이지 프래그먼트로 변경
        }
        btnOkay.setOnClickListener {
            var pName : String = edtName.text.toString()
            var pImage : String? = null
            if(pName.trim().isEmpty()){ // 빈 입력 칸이 있을 경우
                Toast.makeText(fContext, "이름을 입력해주십시오.", Toast.LENGTH_SHORT).show()

            } else {
                // 이미지 변경을 했을 경우
                if (imageUri != null) {
                    pImage = "'" + imageUri.toString() + "'"
                }

                bookDB = dbManager.writableDatabase // 데이터베이스 불러오기
                bookDB.execSQL("INSERT INTO Profile VALUES ('$userEmail', profileNum, '$pName', '$String', $pImage);")
            }

            bookDB.close()
        }
        profileImage.setOnClickListener {
            openGallery()
        }
        return view
    }

    // 갤러리 열기 함수
    fun openGallery(){
        // 갤러리 열기
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        activityResultLauncher.launch(intent);

    }

    // activityResultLauncher로 받은 값 처리(갤러리로부터 선택한 이미지 uri 가져오기)
    var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) { // 이미지를 제대로 가져올 경우
            val intent = result.data
            imageUri = intent!!.data
            profileImage.setImageURI(imageUri) // 프로필 이미지 설정
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(email : String?, num : Int) =
            AddProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("USEREMAIL", email)
                    putInt("PROFILENUM", num)
                }
            }
    }
}