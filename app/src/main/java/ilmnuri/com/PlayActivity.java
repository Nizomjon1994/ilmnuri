package ilmnuri.com;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import ilmnuri.com.utility.Utils;
import ilmnuri.com.model.Api;
import ilmnuri.com.model.Category;

public class PlayActivity extends BaseActivity {

//    private  String audioPath = Environment.getExternalStorageDirectory().toString() + "/sample.mp3";


    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    //
//    private  RequestQueue mRequestQueue;
//    private  SimpleImageLoader mImageLoader;
    private Context mContext;

    private ImageView imageView;

    private TextView tvTitle;
    private int currentCategory;
    private String url, trackPath;
    private String fileName;
    private File dir;
    boolean readExternalStoragePermission;
    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


        initVariables();
        chechReadStoragePermission();
        initUI();

        dir = new File(getExternalFilesDir(null), "audio");
        if (!dir.exists()) {
            dir.mkdir();
        }

        if (Utils.checkFileExist(dir.getPath() + "/" + fileName)) {
//            playAudio();
            if (readExternalStoragePermission) {
                initMediaPlayer();
            }

        } else {
            if (isNetworkAvailable()) {
//                downloadAudio(url);
            } else {
                Utils.showToast(PlayActivity.this, "INTERNET YO'Q! Yuklay olmaysiz!");
                finish();
            }
        }
    }

    private void downloadAudio(String url) {
//        new DownloadFileAsync().execute(url);

        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri download_uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(download_uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setDescription("Test");
        request.setTitle("Test");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalFilesDir(this, dir.getName(), fileName);
        final long enqueue = downloadManager.enqueue(request);
        mProgressDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;

                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(enqueue);

                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                        mProgressDialog.dismiss();
                        if (readExternalStoragePermission) {
                            Utils.showToast(PlayActivity.this, "Darslik yuklandi, endi ijro etilmoqda");
                            initMediaPlayer();
                        }
                    } else {
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                            mProgressDialog.dismiss();
                            Utils.showToast(PlayActivity.this, "Yuklashda xatolik bo'ldi?");

                        }
                    }

                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
//                    EventBus.getDefault().post(AudioEvent.download());

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            mProgressDialog.setProgress((int) dl_progress);

                        }
                    });

                    cursor.close();
                }

            }
        }).start();
    }

    private void chechReadStoragePermission() {
        int permissinCheck = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            permissinCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissinCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                try {
                    readExternalStoragePermission = true;
                } catch (Exception e) {
                    Utils.showToast(PlayActivity.this, "Diskdan joy berilmaganga o'hshaydi!");
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            200);
                }
            }

        } else {
            readExternalStoragePermission = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readExternalStoragePermission = true;
                } else {
                    Utils.showToast(this, "Diskga yozishga ruxsat bermabsiz!");
                    finish();
                }
        }
    }

    private void initVariables() {
        mContext = this;
//        mRequestQueue = Volley.newRequestQueue(mContext);
//        mImageLoader = new SimpleImageLoader(mRequestQueue, BitmapImageCache.getInstance(null));
        dir = new File(getExternalFilesDir(null), "audio");
        if (!dir.exists()) {
            dir.mkdir();
        }

        readExternalStoragePermission = false;
//        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        trackPath = getIntent().getStringExtra("url");
        url = Api.BaseUrl + trackPath;
        fileName = url.substring(url.lastIndexOf('/') + 1);
        String catetory = getIntent().getStringExtra("category");

        if (catetory.equals(Category.category1)) {
            currentCategory = 0;
        } else if (catetory.equals(Category.category2)) {
            currentCategory = 1;
        } else {
            currentCategory = 2;
        }

    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = (TextView) toolbar.findViewById(R.id.tv_play_title);
        tvTitle.setText(trackPath.replace(".mp3", "").replace("_", " "));

        imageView = (ImageView) findViewById(R.id.iv_play);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading file..");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        loadImage();

    }

    private void loadImage() {

//        String imageUrl = "";
        switch (currentCategory) {
            case 0:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ilm1));
                break;
            case 1:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ilm2));
                break;
            case 2:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ilm3));
                break;

        }

//        ImageLoader.getInstance().displayImage(imageUrl, imageView);

//
//        mImageLoader.get(imageUrl,
//                ImageLoader.getImageListener(imageView,
//                        R.drawable.splash_small,
//                        R.drawable.splash_small));
    }
//
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        switch (id) {
//            case DIALOG_DOWNLOAD_PROGRESS:
//                mProgressDialog = new ProgressDialog(this);
//                mProgressDialog.setMessage("Downloading file..");
//                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                mProgressDialog.setCancelable(false);
//                mProgressDialog.show();
//                return mProgressDialog;
//            default:
//                return null;
//        }
//    }

//    class DownloadFileAsync extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showDialog(DIALOG_DOWNLOAD_PROGRESS);
//        }
//
//        @Override
//        protected String doInBackground(String... aurl) {
//            int count;
//
//            try {
//
//                URL url = new URL(aurl[0]);
//                URLConnection conexion = url.openConnection();
//                conexion.connect();
//
//                int lenghtOfFile = conexion.getContentLength();
//                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
//
//                InputStream input = new BufferedInputStream(url.openStream());
//                OutputStream output = new FileOutputStream(Api.localPath + "/" + fileName);
//
//                byte data[] = new byte[1024];
//
//                long total = 0;
//
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
//                    output.write(data, 0, count);
//                }
//
//                output.flush();
//                output.close();
//                input.close();
//            } catch (Exception e) {
//            }
//            return null;
//
//        }
//
//        protected void onProgressUpdate(String... progress) {
//            Log.d("ANDRO_ASYNC", progress[0]);
//            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
//        }
//
//        @Override
//        protected void onPostExecute(String unused) {
//            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
////            playAudio();
//            if (readExternalStoragePermission) {
//                initMediaPlayer();
//            }
//        }
//    }

    ////play music===============start
    private MediaPlayer mediaPlayer;
    public TextView duration;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    private ImageButton btnStart;

    public void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(dir.getPath() + "/" + fileName));
        mediaPlayer.setOnCompletionListener(mOnCompletionListener);
//        mediaPlayer = MediaPlayer.create(this, R.raw.sample_song);
        finalTime = mediaPlayer.getDuration();
        duration = (TextView) findViewById(R.id.songDuration);
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        seekbar.setMax((int) finalTime);
        seekbar.setClickable(true);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(progress);
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnStart = (ImageButton) findViewById(R.id.media_play);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        if (mediaPlayer != null) {
            play();
        }

    }

    // play mp3 song
    public void play() {
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
//            killMediaPlayer();
        }
    };
    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();

            //set seekbar progress
            seekbar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    private void killMediaPlayer() {
        durationHandler.removeCallbacks(updateSeekBarTime);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // pause mp3 song
    public void pause(View view) {
        mediaPlayer.pause();
    }

    // go forward at forwardTime seconds
    public void forward(View view) {
        //check if we can go forward at forwardTime seconds before song endes
        if ((timeElapsed + forwardTime) <= finalTime) {
            timeElapsed = timeElapsed + forwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    // go backwards at backwardTime seconds
    public void rewind(View view) {
        //check if we can go back at backwardTime seconds after song starts
        if ((timeElapsed - backwardTime) > 0) {
            timeElapsed = timeElapsed - backwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    /////////////////end


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (readExternalStoragePermission) {
            if (mediaPlayer != null)
                killMediaPlayer();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_play, menu);
        return true;
    }


//    private void playAudio() {
////        MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(audioPath));
//        MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(IlmApi.localPath + "/" + fileName));
//        if (mediaPlayer != null) {
//            mediaPlayer.setLooping(false);
//            mediaPlayer.start();
//        }
////        VideoPlay videoPlay = new VideoPlay(PlayActivity.this, videoView, IlmApi.localPath + "/" + fileName);
////        videoPlay.playVideo();
//    }


//    private void downloadAudioFile() {
//
//        dialog = Utils.showProgressDialog(mContext, "Downloading...", false);
//        ///creat download request
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        ////////set destinatin storage path
////        request.setDestinationInExternalPublicDir(PATH, fileName);
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                .setAllowedOverRoaming(false)
//                .setTitle("ILM NURI - " + fileName)
//                .setDestinationInExternalPublicDir(IlmApi.localPath, fileName)
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.allowScanningByMediaScanner();
//
//
//        final long downloadId = manager.enqueue(request);
//
//
//    }
//    BroadcastReceiver onComplete=new BroadcastReceiver() {
//        public void onReceive(Context ctxt, Intent intent) {
//            // your code
//            if (dialog != null) {
//                dialog.dismiss();
//            }
//            playAudio();
//        }
//    };

}