package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.wlgusdn.mobile.LoginActivity.Companion.auth
import com.example.wlgusdn.mobile.LoginActivity.Companion.user
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

class AccountActivity : AppCompatActivity()
{

    lateinit var et_Name : EditText
    lateinit var bu : Button
    lateinit var image : ImageView
    var bitmap : Bitmap?=null
    var ud : UserData? = null
    var filePath : Uri? = null
    var img : ImageView?=null
    var l : loading?=null

    val REQUEST_TEXT_GALLERY = 4

    val database = FirebaseDatabase.getInstance().getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        l = loading(this@AccountActivity)


        Accountac=this

        LoginActivity.auth = FirebaseAuth.getInstance()

        et_Name = findViewById(R.id.Account_Edit_Name)
        bu = findViewById(R.id.Account_Button_Apply)
        image = findViewById(R.id.Account_Image)

        database.child("Account").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {



            }

            override fun onDataChange(p0: DataSnapshot) {
                //계정이 있는지 확인
                Log.d("checkkk",p0.child(et_Name.text.toString()).value.toString())
                Log.d("kkaaoo",user!!.uid)
                if(p0.child(user!!.uid).exists())
                {//계정이 있는지 확인

                    l!!.show()//Progress Dialog실행


                    myname = p0.child(auth!!.currentUser!!.uid).child("name").value.toString()

                    val hd = Handler()
                    hd.postDelayed(splashhandler(), 1000)

                    val intent : Intent = Intent(this@AccountActivity,LobbyActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

                    startActivity(intent)
                }

            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {

            var permissionResult = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)

            if(permissionResult == PackageManager.PERMISSION_DENIED) {


                if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_PHONE_STATE)) {


                    requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), 1000)


                } else {
                    //Toast.makeText(this, "됐냐", Toast.LENGTH_SHORT).show()
                }
            }
        }



            image.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val intent = Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_TEXT_GALLERY);

            }
        })

        bu.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {

                    var permissionResult = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)

                    if(permissionResult == PackageManager.PERMISSION_DENIED)
                    {





                        requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), 1000)



                    }
                    else
                    {





                        var a : ArrayList<String> = ArrayList<String>()
                        a.add("약속리스트 초기화")
                        var b : ArrayList<FriendData> = ArrayList<FriendData>()
                        b.add(FriendData("친구리스트 초기화",null,null))
                        ud = UserData(et_Name.text.toString(),getPhoneNumber(),b,a,FirebaseInstanceId.getInstance().token!!)
                        myname = et_Name.text.toString()

                        database.child("Account").child(auth!!.currentUser!!.uid).setValue(ud)

                        if (filePath != null)
                        {
                            //업로드 진행 Dialog 보이기
                            val progressDialog = ProgressDialog(this@AccountActivity);
                            progressDialog.setTitle("업로드중...");
                            progressDialog.show();

                            //storage
                            var storage = FirebaseStorage.getInstance();

                            //Unique한 파일명을 만들자.
                            var formatter = SimpleDateFormat("yyyyMMHH_mmss");
                            var now = Date();
                            var filename = formatter.format(now) + ".png";
                            //storage 주소와 폴더 파일명을 지정해 준다.
                           var storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Account/${(LoginActivity.auth!!.currentUser!!.uid!!)}/" + filename);
                            //올라가거라...
                            storageRef.putFile(filePath!!)
                                    //성공시
                                    .addOnSuccessListener( OnSuccessListener<UploadTask.TaskSnapshot>() {

                                       fun onSuccess( taskSnapshot:UploadTask.TaskSnapshot) {
                                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    //실패시
                                    .addOnFailureListener( OnFailureListener() {
                                       fun onFailure(  e:Exception) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    //진행중
                                    .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot>() {
                                        fun onProgress( taskSnapshot:UploadTask.TaskSnapshot) {

                                           var progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                                            //dialog에 진행률을 퍼센트로 출력해 준다
                                            progressDialog.setMessage("Uploaded " + (progress) + "% ...");
                                        }
                                    });

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                        }

                                        val intent : Intent = Intent(this@AccountActivity,LobbyActivity::class.java)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

                                        startActivity(intent)
                                    }
                                }


                            }
                        })























    }

    private inner class splashhandler : Runnable {
        override fun run() {

            l!!.dismiss()
            l!!.show()//Progress Dialog 종료
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_TEXT_GALLERY)
            {

                val selectedImage = data?.getData();
                filePath=selectedImage
                var filePathColumn : Array<String> = arrayOf(MediaStore.Images.Media.DATA)
                var cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val picturePath = cursor.getString(column_index);

                cursor.close();
                // String picturePath contains the path of selected Image
                var matrix = Matrix();
                val bmp = BitmapFactory.decodeStream(FileInputStream(picturePath), null, null)
                var bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);



                bitmap = bm
                image.setImageBitmap(bm)



            }

    }
    @SuppressLint("MissingPermission")
    fun getPhoneNumber(): String {
        val telephony = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phoneNumber = ""

        try {
            if (telephony.line1Number != null) {
                phoneNumber = telephony.line1Number
            } else {
                if (telephony.simSerialNumber != null) {
                    phoneNumber = telephony.simSerialNumber
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (phoneNumber.startsWith("+82")) {
            phoneNumber = phoneNumber.replace("+82", "0") // +8210xxxxyyyy 로 시작되는 번호
        }
        phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber)

        return phoneNumber
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==1000)
        {
            if(grantResults.count()>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {

                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)
                {



                }

            }
            else
            {
                Toast.makeText(this,"거부됨",Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object
    {
        var Accountac : Activity?=null
        var myname : String?=null
    }

}