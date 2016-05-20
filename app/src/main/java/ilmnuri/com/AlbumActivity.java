package ilmnuri.com;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import ilmnuri.com.adapter.AlbumAdpaterDemo;
import ilmnuri.com.event.AudioEvent;
import ilmnuri.com.model.AlbumModel;
import ilmnuri.com.model.Api;
import ilmnuri.com.utility.Utils;


public class AlbumActivity extends BaseActivity {


    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_album_title)
    TextView tvTitle;
    DownloadManager downloadManager;
    private File dir;

    String fileName;

    private AlbumModel albumModel;
    AlbumAdpaterDemo adpaterDemo;

    Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        EventBus.getDefault().register(this);
        mGson = new Gson();

        String albumBody = getIntent().getStringExtra("album");
        Type type = new TypeToken<AlbumModel>() {
        }.getType();

        albumModel = mGson.fromJson(albumBody, type);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        assert toolbar != null;
        dir = new File(getExternalFilesDir(null), "audio");
        if (!dir.exists()) {
            dir.mkdir();
        }


        tvTitle.setText(albumModel.getCategory() + "/" + albumModel.getAlbum());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adpaterDemo = new AlbumAdpaterDemo(this, albumModel, mOnItemClickListener);
        mRecyclerView.setAdapter(adpaterDemo);

    }

    AlbumAdpaterDemo.OnItemClickListener mOnItemClickListener = new AlbumAdpaterDemo.OnItemClickListener() {
        @Override
        public void onDeleteListener(AlbumModel model, int position) {

            String title = model.getAudios().get(position).getTrackName();
            alertDelete(title);
            adpaterDemo.deleteItem(position);
        }

        @Override
        public void onDownloadListener(AlbumModel model, int position) {

            String url = model.getCategory() + "/" + model.getAlbum() + "/" + model.getAudios().get(position).getTrackName();
            int id = model.getAudios().get(position).getTrackId();
            alertDownload(url, id);

        }


    };


    private void alertDelete(final String title) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        Utils.deleteFile(Api.localPath + "/" + title);
                        Utils.showToast(AlbumActivity.this, "Darslik o'chirib tashlandi!");

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AlbumActivity.this);
        builder.setMessage("Bu darsni o'chirib tashlashni xohlaysizmi?")
                .setPositiveButton("Ha", dialogClickListener)
                .setNegativeButton("Yo'q", dialogClickListener).show();
    }

    private void alertDownload(final String url, final int id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        String filePath = Api.BaseUrl + url;
                        fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

                        downloadAudio(filePath, fileName, id);
//                        Intent intent = new Intent(AlbumActivity.this, PlayActivity.class);
//                        intent.putExtra("category", mAlbumModel.getCategory());
//                        intent.putExtra("url", mAlbumModel.getCategory() + "/" + mAlbumModel.getAlbum() + "/" + mAlbumModel.getItems().get(position));
//                        notifyDataSetChanged();
//                        mContext.startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
//                        notifyDataSetChanged();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bu darsni yuklab olishni xohlaysizmi?")
                .setPositiveButton("Albatta", dialogClickListener)
                .setNegativeButton("Yo'q", dialogClickListener).show();
    }

    public void onEvent(AudioEvent event) {
        if (adpaterDemo != null) {
            adpaterDemo.onEvent(event);
        }
    }


    private void downloadAudio(String url, String fileName, final int id) {
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
//        mProgressDialog.show();
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
                    final int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
//                        mProgressDialog.dismiss();
//                        if (readExternalStoragePermission) {
//                            Utils.showToast(PlayActivity.this, "Darslik yuklandi, endi ijro etilmoqda");
//                            initMediaPlayer();
//                        }
                        EventBus.getDefault().post(AudioEvent.stop(id));
                    } else {
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
//                            mProgressDialog.dismiss();
                            Utils.showToast(AlbumActivity.this, "Yuklashda xatolik bo'ldi?");

                        }
                    }

                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
//                    EventBus.getDefault().post(AudioEvent.resume());

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            EventBus.getDefault().post(AudioEvent.resume(id, dl_progress, bytes_total));

                        }
                    });

                    cursor.close();
                }

            }
        }).start();
    }

}
