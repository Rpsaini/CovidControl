package apps.envision.musicvibes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import communication.SaveImpPrefrences;
import apps.envision.musicvibes.R;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UloadSelfieActivity extends AppCompatActivity {
    private String user_id = "0";
    private ImageView profileimage;
    private Bitmap bmap;
    private ProgressDialog progressdlg;
    private String mobileNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uload_selfie);
        try {


            JSONObject jObj = new JSONObject(new SaveImpPrefrences().reterivePrefrence(this, "user_data") + "");
            mobileNumber = jObj.getString("mobile");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MapsActivity.isSelfieUploaded = false;


        profileimage = findViewById(R.id.selfie);


        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(0);
            }
        });


        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(0);
            }
        });


    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void selectImage(int actionCode) {
        if (checkAndRequestPermissions() == 0) {
            if (actionCode == 0) {
                dispatchTakePictureIntent();
            } else if (actionCode == 1) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(pickPhoto, actionCode);
            }
        } else if (checkAndRequestPermissions() == 1) {
            //  showtoast.showToast(PersonalDetails.this, "Go to  Cabs App settings and enable required Permission");
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        System.out.println("inside============" + requestCode + "===" + resultCode);
        bmap = null;
        Uri selectedImage = null;
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    try {

                        bmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                        ImageView selfie = findViewById(R.id.selfie);
                        selfie.setImageBitmap(bmap);
                        String path = getRealPathFromURI(getImageUri(this, bmap));

                        UloadSelfieActivity.UploadVideo uv = new UploadVideo(path, "selfie");
                        uv.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        if (imageReturnedIntent != null) {
                            try {
                                selectedImage = imageReturnedIntent.getData();
                                String s = getRealPathFromURI(selectedImage);
                                InputStream image_stream = getContentResolver().openInputStream(selectedImage);
                                bmap = BitmapFactory.decodeStream(image_stream);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
            }


        }
    }

    private int checkAndRequestPermissions() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int readExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (readExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writeExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return 1;
        }

        return 0;
    }


    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    class ConvertImage extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdlg = new ProgressDialog(UloadSelfieActivity.this);
            progressdlg.setCancelable(false);
            progressdlg.setMessage("Uploading Selfie....");
            progressdlg.show();
        }

        @Override
        protected String doInBackground(String... params) {


            bmap = getResizedBitmap(bmap, 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();

            return Base64.encodeToString(b, Base64.DEFAULT);

        }


        @Override
        protected void onPostExecute(String str) {

            Map<String, String> m = new HashMap<>();
            m.put("mobile", mobileNumber);
            m.put("imagetype", "profile_pic");
            m.put("filename", user_id + "profilepic.png");
            m.put("userfile", str);
            sendToServer("upload_selfie", m);

            super.onPostExecute(str);


        }
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 0;
    Uri photoURI;


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("inside exception===" + ex.getMessage());

            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "apps.envision.musicvibes.fullsizeimage.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    public void sendToServer(final String url, final Map data) {

        try {
            SaveImpPrefrences imp = new SaveImpPrefrences();
            final String url1 = imp.reterivePrefrence(UloadSelfieActivity.this, "url") + url;

            System.out.println("urls===" + url1);

            data.put("device_type", "android");
            data.put("device_info", getDeviceName());
            data.put("device_token", "12345");


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                progressdlg.dismiss();

                                profileimage.setImageBitmap(bmap);


                                Toast.makeText(UloadSelfieActivity.this, "Selfie Image Uploaded successfully", Toast.LENGTH_LONG).show();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("inside error voly========" + error.getLocalizedMessage());

                            Toast.makeText(UloadSelfieActivity.this, "Selfie not Uploaded !", Toast.LENGTH_LONG).show();
                            progressdlg.dismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    return data;
                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    try {
                        ArrayList<String> headerRet = getHashKey(url1);
                        params.put("Authstring", headerRet.get(0));
                        params.put("Timestamp", headerRet.get(1));
                        params.put("x-api-key", "CODEX@123");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }

                ;
            };


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    100000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            RequestQueue requestQueue = Volley.newRequestQueue(UloadSelfieActivity.this);
            requestQueue.add(stringRequest);


            stringRequest.setShouldCache(true);
        } catch (Exception e) {
            progressdlg.dismiss();


        }
    }


    private ArrayList<String> getHashKey(String url) {
        String[] urlArray = url.split("/");
        String methodName = urlArray[urlArray.length - 1];
        String key = "AGSDF34DFGSDFG345DFGDF344545DFGDFG6756fg";
        long timeStamp = System.currentTimeMillis();
        System.out.println("method name==" + methodName + "===" + key + "==" + timeStamp);
        ArrayList<String> headerArray = new ArrayList<>();
        headerArray.add(md5(methodName + key + timeStamp));
        headerArray.add(timeStamp + "");
        return headerArray;
    }

    public String md5(String s) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(s.getBytes(), 0, s.length());
            String pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return password;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    //show image=====================================

//    private void showImage(String imageUrl) {
//        Picasso.with(UserPersonalDetails.this)
//                .load(imageUrl)
//                .into(profileimage, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                    }
//
//                    @Override
//                    public void onError() {
//                        Picasso.with(UserPersonalDetails.this)
//                                .load(R.mipmap.uploadcopy)
//                                .into(profileimage);
//                    }
//                });
//    }
//

    class UploadVideo extends AsyncTask<Void, Void, String> {
        ProgressDialog uploading;
        String path1 = "", imageType1 = "";


        public UploadVideo(String path, String imageType) {
            path1 = path;
            imageType1 = imageType;

            System.out.println("image path===" + path1);
            System.out.println("image name===" + imageType1);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uploading = ProgressDialog.show(UloadSelfieActivity.this, "Uploading....", "Please wait...", false, false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            uploading.dismiss();
            try {
                if (s.equalsIgnoreCase("500")) {
                    Toast.makeText(UloadSelfieActivity.this, "This file is not supported.Please select another image", Toast.LENGTH_LONG).show();

                } else {
                    final JSONObject obj = new JSONObject(s);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String status = obj.get("status") + "";
                                if (status.equalsIgnoreCase("true")) {
                                    //  showImage.setImageBitmap(bmap);
                                    // showImage.setTag("1");
                                    //  imgcallbac.getImagename(obj.getString("data"));
                                    Toast.makeText(UloadSelfieActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                    MapsActivity.isSelfieUploaded = true;
                                    finish();
                                } else {

                                    Toast.makeText(UloadSelfieActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                    // showtoast.showToast(DriverRegistrationNext.this, obj.getString("msg"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    if (obj.getString("status").equals("true")) {
                        if (obj.has("filename")) {
                            String filename = obj.getString("filename");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Upload u = new Upload();
            String msg = u.uploadVideo(path1, imageType1, UloadSelfieActivity.this, mobileNumber,CConstant.baseUrl+"/upload_selfie","selfie");

            System.out.println("messga==============" + msg);


            return msg;
        }
    }
}
