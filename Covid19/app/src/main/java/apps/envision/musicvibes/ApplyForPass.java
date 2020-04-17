package apps.envision.musicvibes;

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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;
import apps.envision.musicvibes.R;

public class ApplyForPass extends AppCompatActivity {

    private ImageView idprooffront,idproofback,supportingdocument,profilepic;
    private ImageView commonImg;
    private String imageType="";
    private Map<String,String> finalDataMap=new HashMap();
    private RelativeLayout rr_subcategory;
    public static TextView pass_date;

    private Spinner spinner_state,spinner_district,spinner_passtype,sppinner_category,spinner_sub_category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_pass);
        init();
    }


    private void init()
    {
        rr_subcategory =findViewById(R.id.rr_subcategory);
        spinner_state = findViewById(R.id.spinner_state);
         spinner_district = findViewById(R.id.spinner_district);
        spinner_passtype =findViewById(R.id.spinner_passtype);
         sppinner_category=findViewById(R.id.sppinner_category);
         spinner_sub_category= findViewById(R.id.spinner_sub_category);



        final Spinner spinner_documenttype= findViewById(R.id.spinner_documenttype);
        final Spinner spinner_nationality=findViewById(R.id.spinner_nationality);
        final Spinner spinner_vehicletype= findViewById(R.id.spinner_vehicletype);

        final EditText pass_address= findViewById(R.id.pass_address);
        final EditText pass_name= findViewById(R.id.pass_name);
        final EditText pass_email= findViewById(R.id.pass_email);
        final EditText pass_mobile= findViewById(R.id.pass_mobile);
        final EditText pass_vehiclenumer= findViewById(R.id.pass_vehiclenumer);
        final EditText pass_frompalace= findViewById(R.id.pass_frompalace);
        final EditText pass_topalalce= findViewById(R.id.pass_topalalce);
         pass_date= findViewById(R.id.pass_date);
        final EditText pass_reason= findViewById(R.id.pass_reason);
        final CheckBox checkbox_terms= findViewById(R.id.checkbox_terms);
        final TextView apply= findViewById(R.id.apply);


        pass_mobile.setText(getIntent().getStringExtra("mobile"));
        pass_mobile.setFocusable(false);

        //document

         idprooffront= findViewById(R.id.idprooffront);
         idproofback= findViewById(R.id.idproofback);
         supportingdocument= findViewById(R.id.supportingdocument);
         profilepic= findViewById(R.id.profilepic);



        idprooffront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                commonImg=idprooffront;
                imageType="identity_front";
                imgOptionDialog();
            }
        });

        idproofback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commonImg=idproofback;
                imageType="identity_back";
                imgOptionDialog();
            }
        });

        supportingdocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonImg=supportingdocument;
                imageType="supported_doc";
                imgOptionDialog();
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonImg=profilepic;
                imageType="profile_pic";
                imgOptionDialog();
            }
        });




        ArrayList<String> stateAr=new ArrayList<>();
        stateAr.add("Select State");

        ArrayList<String> typeAr=new ArrayList<>();
        typeAr.add("Select Pass Type");

        ArrayList<String> districtAr=new ArrayList<>();
        districtAr.add("Select District");

        ArrayList<String> categoryAr=new ArrayList<>();
        categoryAr.add("Select Category");

        ArrayList<String> subCategoryAr=new ArrayList<>();
        subCategoryAr.add("Select Sub Category");



        spinner_state.setAdapter(new ArrayAdapter<String>
                (this, R.layout.spinner_item,stateAr));


        spinner_passtype.setAdapter(new ArrayAdapter<String>
                (this, R.layout.spinner_item,typeAr));


        spinner_district.setAdapter(new ArrayAdapter<String>
                (this, R.layout.spinner_item,districtAr));


        sppinner_category.setAdapter(new ArrayAdapter<String>
                (this, R.layout.spinner_item,categoryAr));


        spinner_sub_category.setAdapter(new ArrayAdapter<String>
                (this, R.layout.spinner_item,subCategoryAr));







        ArrayList<String> documentTypeAr=new ArrayList<String>();

        documentTypeAr.add("Select Document Proof");
        documentTypeAr.add("Adhar Card");
        documentTypeAr.add("Pan Card");
        documentTypeAr.add("Passport");
        documentTypeAr.add("Voter Id Crd");



        ArrayList<String> array_vehicletypeAr=new ArrayList<String>();
        array_vehicletypeAr.add("Select Vehicle Type");
        array_vehicletypeAr.add("Two Wheeler");
        array_vehicletypeAr.add("Three Wheeler");
        array_vehicletypeAr.add("Four Wheeler");
        array_vehicletypeAr.add("Heavy Vehicle");


        ArrayList<String> array_nationalityAr=new ArrayList<String>();
        array_nationalityAr.add("Select Nationality Type");
        array_nationalityAr.add("Indian");
        array_nationalityAr.add("Foreigner");




        ArrayAdapter<String> spinnerDocument = new ArrayAdapter<String>
                (this, R.layout.spinner_item,documentTypeAr);
        spinner_documenttype.setAdapter(spinnerDocument);


        ArrayAdapter<String> spinnerVehicleType = new ArrayAdapter<String>
                (this, R.layout.spinner_item,array_vehicletypeAr);
        spinner_vehicletype.setAdapter(spinnerVehicleType);


        ArrayAdapter<String> spinnerNationalityAdapter = new ArrayAdapter<String>
                (this, R.layout.spinner_item,array_nationalityAr);
        spinner_nationality.setAdapter(spinnerNationalityAdapter);



        spinner_documenttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    finalDataMap.put("document_type",spinner_documenttype.getSelectedItem()+"");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_vehicletype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    finalDataMap.put("vehicle_type",spinner_vehicletype.getSelectedItem()+"");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_nationality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    finalDataMap.put("nationality",spinner_nationality.getSelectedItem()+"");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pass_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });



        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_state.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select state",Toast.LENGTH_LONG).show();
                    return;
                }

                if(spinner_district.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select district",Toast.LENGTH_LONG).show();
                    return;
                }
                if(spinner_passtype.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select pass type",Toast.LENGTH_LONG).show();
                    return;
                }

                if(sppinner_category.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select Pass Category ",Toast.LENGTH_LONG).show();
                    return;
                }

                if(rr_subcategory.getVisibility()==View.VISIBLE) {
                    if (spinner_sub_category.getSelectedItemPosition() == 0) {
                        Toast.makeText(ApplyForPass.this, "Select Pass Sub-Category ", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if(pass_address.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter Address",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass_name.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter Pass holder name",Toast.LENGTH_LONG).show();
                    return;
                }

                if(pass_email.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter Pass holder Email",Toast.LENGTH_LONG).show();
                    return;
                }

                if(pass_mobile.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter Pass holder mobile",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass_frompalace.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter From place",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass_topalalce.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter To Place",Toast.LENGTH_LONG).show();
                    return;
                }

                if(spinner_nationality.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select Nationality ",Toast.LENGTH_LONG).show();
                    return;
                }



                if(spinner_documenttype.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select document type",Toast.LENGTH_LONG).show();
                    return;
                }

                //image proff here




                if(spinner_documenttype.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select document type",Toast.LENGTH_LONG).show();
                    return;
                }


                if(idprooffront.getTag().toString().toString().equalsIgnoreCase("0"))
                {
                    Toast.makeText(ApplyForPass.this,"Upload Identity proof front image",Toast.LENGTH_LONG).show();
                    return;
                }

                if(idproofback.getTag().toString().equalsIgnoreCase("0"))
                {
                    Toast.makeText(ApplyForPass.this,"Upload Identity proof back image",Toast.LENGTH_LONG).show();
                    return;
                }
                if(profilepic.getTag().toString().equalsIgnoreCase("0"))
                {
                    Toast.makeText(ApplyForPass.this,"Upload profile picture",Toast.LENGTH_LONG).show();
                    return;
                }
                if(spinner_vehicletype.getSelectedItemPosition()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Select vehicle type",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass_vehiclenumer.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter vehicle number",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pass_date.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter Date",Toast.LENGTH_LONG).show();
                    return;
                }


                if(pass_reason.getText().toString().length()==0)
                {
                    Toast.makeText(ApplyForPass.this,"Enter pass reason",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!checkbox_terms.isChecked())
                {
                    Toast.makeText(ApplyForPass.this,"Select terms and condition",Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    finalDataMap.put("address",pass_address.getText()+"");
                    finalDataMap.put("name",pass_name.getText()+"");
                    finalDataMap.put("email",pass_email.getText()+"");
                    finalDataMap.put("mobile",pass_mobile.getText()+"");
                    finalDataMap.put("identity_front",idprooffront.getTag()+"");
                    finalDataMap.put("identity_back",idproofback.getTag()+"");
                    finalDataMap.put("supported_doc",supportingdocument.getTag()+"");
                    finalDataMap.put("profile_pic",profilepic.getTag()+"");
                    finalDataMap.put("vehiclenumber",pass_vehiclenumer.getText()+"");

                    finalDataMap.put("from_place",pass_frompalace.getText()+"");
                    finalDataMap.put("to_place",pass_topalalce.getText()+"");

                    finalDataMap.put("date",pass_date.getText()+"");
                    finalDataMap.put("reason",pass_reason.getText()+"");
                    System.out.println("map data===="+finalDataMap);



                    new ServerHandler().sendToServer(ApplyForPass.this, "save_epass_data", finalDataMap, 0, new CallBack() {
                        @Override
                        public void getRespone(String dta, ArrayList<Object> respons) {

                            try {

                                JSONObject obj=new JSONObject(dta);
                                if(obj.getString("status").equalsIgnoreCase("true"))
                                {
                                    new SaveImpPrefrences().savePrefrencesData(ApplyForPass.this,"applied",CConstant.key_PassStatus);
                                    Toast.makeText(ApplyForPass.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(ApplyForPass.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
        getInitiaData();

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
            uploading = ProgressDialog.show(ApplyForPass.this, "Uploading....", "Please wait...", false, false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            uploading.dismiss();
            try {
                if (s.equalsIgnoreCase("500")) {
                    Toast.makeText(ApplyForPass.this, "This file is not supported.Please select another image", Toast.LENGTH_LONG).show();

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
                                    Toast.makeText(ApplyForPass.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                                  }
                                else {
                                    commonImg.setTag("0");
                                    Toast.makeText(ApplyForPass.this, obj.getString("message"), Toast.LENGTH_LONG).show();

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
            String msg = u.uploadVideo(path1, imageType, ApplyForPass.this, "9988525607", CConstant.baseUrl+"upload_epass_proof","epass_proof");

            System.out.println("messga==============" + msg);


            return msg;
        }
    }





    private JSONArray jstatesAr, jepass_typeAr,jepass_categoryAr;
    private void getInitiaData()
    {

        new ServerHandler().sendToServer(ApplyForPass.this, "epass_ini_data", new HashMap<String, String>(), 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {
                try {

                    final JSONObject obj=new JSONObject(dta);
                    if(obj.getString("status").equalsIgnoreCase("true"))
                    {
                         jstatesAr=obj.getJSONArray("states");
                         jepass_typeAr=obj.getJSONArray("epass_type");
                         jepass_categoryAr=obj.getJSONArray("epass_category");

                        final ArrayList<String> stateAr=new ArrayList<>();
                        stateAr.add("Select State");
                        for(int x=0;x<jstatesAr.length();x++)
                        {
                            stateAr.add(jstatesAr.getJSONObject(x).getString("name"));
                        }

                        ArrayList<String> epassAr=new ArrayList<>();
                        epassAr.add("Select Pass Type");
                        for(int x=0;x<jepass_typeAr.length();x++)
                        {
                            epassAr.add(jepass_typeAr.getJSONObject(x).getString("name"));
                        }

                        ArrayList<String> epassCategory=new ArrayList<>();
                        epassCategory.add("Select Pass Category");
                        for(int x=0;x<jepass_categoryAr.length();x++)
                        {
                            epassCategory.add(jepass_categoryAr.getJSONObject(x).getString("name"));
                        }



                        spinner_state.setAdapter(new ArrayAdapter<String>
                                (ApplyForPass.this, R.layout.spinner_item,stateAr));
                        spinner_passtype.setAdapter(new ArrayAdapter<String>
                                (ApplyForPass.this, R.layout.spinner_item,epassAr));

                        sppinner_category.setAdapter(new ArrayAdapter<String>
                                (ApplyForPass.this, R.layout.spinner_item,epassCategory));


                        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position>0)
                                {
                                    try {
                                        JSONObject obj=jstatesAr.getJSONObject(position-1);
                                        finalDataMap.put("state",obj.getString("id"));
                                        loadCity(obj.getString("id"));

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });



                        spinner_passtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position>0)
                                {
                                    try {
                                        JSONObject obj=jepass_typeAr.getJSONObject(position-1);
                                        finalDataMap.put("passtype",obj.getString("id"));
                                        loadSubCategory(obj.getString("id"));
                                       }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        sppinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position>0)
                                {
                                    try {



                                        JSONObject obj=jepass_categoryAr.getJSONObject(position-1);
                                        finalDataMap.put("category",obj.getString("id"));
                                        loadSubCategory(obj.getString("id"));

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });






                    }
                    else
                    {
                        Toast.makeText(ApplyForPass.this,obj.getString("message"),Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadCity(String id)
    {
        Map<String,String> m=new HashMap<>();
        m.put("state_id",id);
        new ServerHandler().sendToServer(ApplyForPass.this, "load_districts", m, 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {

                try {

                    JSONObject dataObj=new JSONObject(dta);
                    if(dataObj.getString("status").equalsIgnoreCase("true"))
                    {
                       final JSONArray jDistrct=dataObj.getJSONArray("districts");
                       ArrayList<String> districAr=new ArrayList<>();
                       districAr.add("Select district");
                       for(int x=0;x<jDistrct.length();x++)
                       {
                           districAr.add(jDistrct.getJSONObject(x).getString("name"));
                       }
                        ArrayAdapter<String> districtArdapter = new ArrayAdapter<String>
                                (ApplyForPass.this, R.layout.spinner_item,districAr);
                        spinner_district.setAdapter(districtArdapter);

                        spinner_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    try {
                                        JSONObject obj = jDistrct.getJSONObject(position - 1);
                                        finalDataMap.put("district", obj.getString("id"));


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    private void loadSubCategory(String id)
    {
        Map<String,String> m=new HashMap<>();
        m.put("category_id",id);
        new ServerHandler().sendToServer(ApplyForPass.this, "load_subcategory", m, 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {

                try {

                    JSONObject dataObj=new JSONObject(dta);
                    if(dataObj.getString("status").equalsIgnoreCase("true"))
                    {
                       final JSONArray jSubCtegory=dataObj.getJSONArray("sub_categories");
                        ArrayList<String> districAr=new ArrayList<>();
                        districAr.add("Select Subcategory");

                        if(jSubCtegory.length()==0)
                        {
                            rr_subcategory.setVisibility(View.GONE);
                        }
                        else
                        {
                            rr_subcategory.setVisibility(View.VISIBLE);
                        }
                        for(int x=0;x<jSubCtegory.length();x++)
                        {
                            districAr.add(jSubCtegory.getJSONObject(x).getString("name"));
                        }
                        spinner_sub_category.setAdapter(new ArrayAdapter<String>
                                (ApplyForPass.this, R.layout.spinner_item,districAr));

                        spinner_sub_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    try {
                                        JSONObject obj = jSubCtegory.getJSONObject(position - 1);
                                        finalDataMap.put("subcategory", obj.getString("id"));
                                       }
                                     catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }



                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return  dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            pass_date.setText(day+"-"+(month+1)+"-"+year);

        }
    }
}
