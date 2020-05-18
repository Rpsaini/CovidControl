package apps.envision.musicvibes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;
import fcm.FusedLocationNew;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {

    private ImageView serviceimage,commonImg;
    private String imageType="request_image",selected_service="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        getSupportActionBar().hide();
        MainActivityNew.isSelfieUploaded=false;
        init();
    }
    private void init()
    {
        final Spinner spinner_service= findViewById(R.id.spinner_service);
        final EditText service_comment= findViewById(R.id.service_comment);
        serviceimage= findViewById(R.id.serviceimage);
        commonImg=serviceimage;
        TextView checkorderstatus= findViewById(R.id.checkorderstatus);



        serviceimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                commonImg=serviceimage;
                imageType="";
                imgOptionDialog();
            }
        });

        ArrayList<String> servicearray=new ArrayList<>();
        servicearray.add("Select Service type");
        servicearray.add("Food");
        servicearray.add("Medicine");
        servicearray.add("Essential");
        servicearray.add("Other");



        spinner_service.setAdapter(new ArrayAdapter<String>
                       (this, R.layout.spinner_item,servicearray));



        spinner_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    selected_service=spinner_service.getSelectedItem()+"";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_service.getSelectedItemPosition()==0)
                {
                    Toast.makeText(RequestActivity.this,"Select service type",Toast.LENGTH_LONG).show();
                    return;
                }

                if(service_comment.getText().toString().length()==0)
                {
                    Toast.makeText(RequestActivity.this,"Enter remarks ",Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Map<String,String> finalDataMap=new HashMap<>();

                    finalDataMap.put("image",commonImg.getTag()+"");
                    finalDataMap.put("type",selected_service+"");
                    finalDataMap.put("remarks",service_comment.getText()+"");
                    finalDataMap.put("lat", FusedLocationNew.mCurrentLocation.getLatitude()+"");
                    finalDataMap.put("lng",FusedLocationNew.mCurrentLocation.getLongitude()+"");
                    finalDataMap.put("mobile",new SaveImpPrefrences().reterivePrefrence(RequestActivity.this,"Nu_mobile")+"");



                    new ServerHandler().sendToServer(RequestActivity.this, "submit_service_request", finalDataMap, 0, new CallBack() {
                        @Override
                        public void getRespone(String dta, ArrayList<Object> respons) {

                            try {

                                JSONObject obj=new JSONObject(dta);
                                if(obj.getString("status").equalsIgnoreCase("true"))
                                {

                                  //  Toast.makeText(RequestActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(RequestActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });


        checkorderstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RequestActivity.this,CheckorderStatusActivity.class);
                startActivity(i);
            }
        });

    }


    private void imgOptionDialog()
    {

        final Dialog optionDialog = new Dialog(this);
        optionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        optionDialog.setContentView(R.layout.upload_layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = optionDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        optionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setAttributes(lp);
        optionDialog.show();

        TextView textOff = optionDialog.findViewById(R.id.logoutyes);
        //textOff.setVisibility(View.INVISIBLE);

        TextView texton = optionDialog.findViewById(R.id.logoutno);
        TextView popuptitle = optionDialog.findViewById(R.id.popuptitle);

        TextView logoutMaintitle = optionDialog.findViewById(R.id.logoutMaintitle);
        logoutMaintitle.setText("Upload Image");
        popuptitle.setText("Browse image from");
        textOff.setText("Camera");
        texton.setText("Gallery");

        textOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                selectImage(0);
                optionDialog.dismiss();
            }
        });
        texton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(1);
                optionDialog.dismiss();
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
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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

        Uri selectedImage = null;
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    try {
                        Bitmap  bmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                        UploadVideo uv = new UploadVideo(getRealPathFromURI(getImageUri(this, bmap)), imageType);
                        uv.execute();
                        commonImg.setImageBitmap(bmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        if (imageReturnedIntent != null) {
                            try {
                                selectedImage = imageReturnedIntent.getData();
                                InputStream image_stream = getContentResolver().openInputStream(selectedImage);
                                Bitmap  bmap = BitmapFactory.decodeStream(image_stream);
                                UploadVideo uv = new UploadVideo(getRealPathFromURI(selectedImage), imageType);
                                uv.execute();
                                commonImg.setImageBitmap(bmap);

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

        int permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int readExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
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
                        "apps.envision.musicvibes.fileprovider",
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

    class UploadVideo extends AsyncTask<Void, Void, String> {
        ProgressDialog uploading;
        String path1 = "", imageType1 = "";


        public UploadVideo(String path, String imageType) {
            path1 = path;
            imageType1 = imageType;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uploading = ProgressDialog.show(RequestActivity.this, "Uploading....", "Please wait...", false, false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            uploading.dismiss();
            try {
                if (s.equalsIgnoreCase("500")) {
                    Toast.makeText(RequestActivity.this, "This file is not supported.Please select another image", Toast.LENGTH_LONG).show();

                } else {
                    final JSONObject obj = new JSONObject(s);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String status = obj.get("status") + "";
                                if (status.equalsIgnoreCase("true"))
                                {
                                    commonImg.setTag(obj.getString("image_name"));
                                    //Toast.makeText(RequestActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    commonImg.setTag("0");
                                    Toast.makeText(RequestActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Upload u = new Upload();
            String msg = u.uploadVideo(path1, imageType, RequestActivity.this, "", CConstant.baseUrl+"upload_serivce_request_media","service_request_image");

            System.out.println("messga==============" + msg);


            return msg;
        }
    }









}
