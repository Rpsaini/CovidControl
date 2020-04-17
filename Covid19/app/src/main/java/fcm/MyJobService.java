package fcm;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MyJobService extends JobService
{
    public static Handler hnd;
   static Runnable runnable;
    private AsyncTask mBackgroundTask;
    @Override
    public boolean onStartJob(final JobParameters job)
    {

        System.out.println("new instance created=====>");
        if(hnd!=null)
        {
            hnd.removeCallbacks(runnable);
        }
        hnd=new Handler();
        runnable =new Runnable() {
            @Override
            public void run() {
                mBackgroundTask = new AsyncTask()
                {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        System.out.println("inside location update===="+System.currentTimeMillis());
                        LocationTracker tracker=new LocationTracker(getApplicationContext());
                        tracker.execute();
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object o)
                    {
                        // jobFinished(jobParameters, false);
                    }
                };
                mBackgroundTask.execute();
                hnd.postDelayed(this,30000);
            }
        };
        hnd.postDelayed(runnable,100);
        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }


}
