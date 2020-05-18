package fcm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import apps.envision.musicvibes.BuildConfig;
import apps.envision.musicvibes.CConstant;
import apps.envision.musicvibes.MapsActivity;
import apps.envision.musicvibes.R;
import communication.SaveImpPrefrences;
import interfaces.DownloadCallback;


@SuppressWarnings("ConstantConditions")
public abstract class BaseActivity extends AppCompatActivity {


    public ProgressDialog mProgressDialog;
    protected  Context mContext;
    private AlertDialog downloadDialog;
    protected ProgressBar downloadProgressBar;
    protected TextView progressPercentage;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;

    private long downloadID;
    private String filename="CovidControlR.apk";
    private String downloadfilename="CovidControlR.apk";
    private File file;
    private String installationath="application/vnd.android.package-archive";
    private String Apkurl="";




    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mContext = this;
        }
        public  boolean isDownloading(Context context)
    {
        long dowloadedid=Long.parseLong(new SaveImpPrefrences().reterivePrefrence(BaseActivity.this,CConstant.key_downloadid)+"");
        System.out.println("downloaded is==="+dowloadedid);

        boolean isDownload= getStatus(context , dowloadedid) == DownloadManager.STATUS_RUNNING;
        if(isDownload)
        {
            file = new File(getExternalFilesDir(null), filename);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            getDownloadPercentage(dowloadedid,downloadManager);
        }
        return  isDownload;
    }



    private int getStatus(Context context , long downloadId)
    {
        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);// filter your download bu download Id
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            c.close();
            return status;
        }

        return -1;
    }

    public int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            // Context mContext=getApplicationContext();
            versionCode = mContext.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }




    public void alertDialogForUpdate(String url)
     {
        Apkurl=url;
        AlertDialog.Builder alertDialogJoinBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.update, null);
        alertDialogJoinBuilder.setView(convertView);
        final TextView tv_join_contest = convertView.findViewById(R.id.updatenow);
        final AlertDialog alertDialogJoin = alertDialogJoinBuilder.create();
        alertDialogJoin.setCancelable(false);
        tv_join_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogJoin.dismiss();
                checkPermsissionTo();
            }
        });


        alertDialogJoin.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    private void checkPermsissionTo() {
        if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {

                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }


    private void proceedAfterPermission() {
        //8295310486         rasab singh



        // registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
         file = new File(getExternalFilesDir(null), filename);


        if(file.exists())
            file.delete();

        System.out.println("apk ur;ll="+Apkurl+downloadfilename);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Apkurl+downloadfilename))
                .setTitle(getResources().getString(R.string.app_name))// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setMimeType(installationath)
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network


        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);//
        new SaveImpPrefrences().savePrefrencesData(BaseActivity.this,downloadID+"",CConstant.key_downloadid);


        getDownloadPercentage(downloadID,downloadManager);

    }


//    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //Fetching the download id received with the broadcast
//            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//            if (downloadID == id) {
//                Toast.makeText(BaseActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
//                installApk();
//            }
//        }
//    };




    public void installApk() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(
                    BaseActivity.this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
            );
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            install.setData(contentUri);
            startActivity(install);
        }
        else {

            Uri apkUri = Uri.fromFile(file);
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            install.setDataAndType(
                    apkUri,
                    installationath
            );
            startActivity(install);


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                proceedAfterPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void getDownloadPercentage(final  long downloadId,final DownloadManager manager)
    {
        showDownloadProgressDialog();
        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;

                while (downloading)
                   {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);

                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressPercentage.setText(dl_progress+"%");
                            downloadProgressBar.setProgress(dl_progress);

                            System.out.println("Download percentage===="+dl_progress);
                          if(dl_progress==100)
                          {
                              downloadDialog.dismiss();
                              installApk();
                          }

                        }
                    });
                    cursor.close();
                }

            }
        }).start();
    }




    private void showDownloadProgressDialog()
    {
        if(downloadDialog == null)
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_download_progress, null);
            dialogBuilder.setView(dialogView);

            downloadProgressBar = dialogView.findViewById(R.id.downloadProgressBar);
            progressPercentage = dialogView.findViewById(R.id.progressPercentage);

            downloadDialog = dialogBuilder.setCancelable(false).create();
            downloadDialog.show();
        }


    }







 }
