package com.downloadpdf.girmayilma.downloadpdf;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String URL_PATH = "http://www.office.xerox.com/latest/SFTBR-04.PDF"; // url path for downloading the pdf
    private static final String DIRECTORY = "/sdcard/Download/test.pdf";// path for storing the downloaded pdf
    private static final int REQUEST_READ_WRITE_ACCESS = 1;

    private static String[] PERMISSIONS_READ_WRITE = { Manifest.permission.READ_EXTERNAL_STORAGE,
                                                       Manifest.permission.WRITE_EXTERNAL_STORAGE };
    int id = 0;
    private NotificationManager mNotifyManager;
    private Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void download(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED) {
            requestReadWritePermissions();

        } else {

            downloadPdf();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_READ_WRITE_ACCESS:

                if (verifyPermissions(grantResults)) {
                    // All required permissions have been granted, display contacts fragment.

                    Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();

                    downloadPdf();

                } else {

                    Toast.makeText(MainActivity.this, "Grant was denied", Toast.LENGTH_SHORT).show();
                }
                break;

            default:

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length<1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result!=PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void downloadPdf() {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setContentTitle(getString(R.string.download_mesage))
                .setContentText(getString(R.string.download_mesage_progress))
                .setSmallIcon(R.mipmap.ic_launcher);
        new DownloadTask().execute(URL_PATH);
    }

    private void requestReadWritePermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //  Toast.makeText(MainActivity.this, "Should show request permission reason", Toast.LENGTH_SHORT).show();

            showMessageOKCancel("You need to allow access to file to download pdf", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_READ_WRITE, REQUEST_READ_WRITE_ACCESS);
                }
            });
            return;

        } else {
            // file permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_READ_WRITE, REQUEST_READ_WRITE_ACCESS);
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this).setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
            mBuilder.setAutoCancel(true);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mBuilder.setProgress(100, progress[0], false);
            mNotifyManager.notify(id, mBuilder.build());
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mBuilder.setContentText(getString(R.string.download_complete_mesage));

     /*       Uri path = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                         + "/test.pdf"); // find the path for the downloaded pdf
            File file = new File(path.toString());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent intent = Intent.createChooser(browserIntent, getString(R.string.action_open));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setProgress(0, 1, false);
            mNotifyManager.notify(id, mBuilder.build());*/

            Uri path = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                         + "/test.pdf"); // find the path for the downloaded pdf
            File file = new File(path.toString());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent intent = Intent.createChooser(browserIntent, getString(R.string.action_open));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setProgress(0, 1, false);
            mNotifyManager.notify(id, mBuilder.build());

        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(DIRECTORY);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data))!=-1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength>0) // only if total length is known
                    {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output!=null) {
                        output.close();
                    }
                    if (input!=null) {
                        input.close();
                    }
                } catch (IOException ignored) {
                }

                if (connection!=null) {
                    connection.disconnect();
                }
            }
            return null;
        }

    }

}



