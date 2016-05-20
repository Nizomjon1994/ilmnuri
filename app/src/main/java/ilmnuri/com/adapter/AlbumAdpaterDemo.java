package ilmnuri.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ilmnuri.com.PlayActivity;
import ilmnuri.com.R;
import ilmnuri.com.event.AudioEvent;
import ilmnuri.com.model.AlbumModel;
import ilmnuri.com.model.Api;
import ilmnuri.com.model.Audio;
import ilmnuri.com.utility.Utils;

/**
 * Created by User on 19.05.2016.
 */
public class AlbumAdpaterDemo extends RecyclerView.Adapter<AlbumAdpaterDemo.ViewHolder> {

    private Context mContext;
    private AlbumModel mAlbumModel;
    private List<Audio> mAudios;
    private OnItemClickListener mOnItemClickListener;
    private List<ViewHolder> mViewHolders = new ArrayList<>();
    Handler handler;
    private int currentSize;

    public AlbumAdpaterDemo(Context context, AlbumModel albumModel, OnItemClickListener listener) {
        mContext = context;
        mAlbumModel = albumModel;
        this.mOnItemClickListener = listener;
    }

    public void deleteItem(int position) {
        mAlbumModel.getAudios().remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        mViewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Audio audio = getItem(position);

        if (holder.tvAlbumTitle != null) {
            holder.tvAlbumTitle.setText(audio.getTrackName().replace(".mp3", "").replace("_", " "));
        }
        if (holder.audioSize != null) {
            holder.audioSize.setText(audio.getTrackSize());
        }

        if (Utils.checkFileExist(Api.localPath + "/" + mAlbumModel.getAudios().get(position).getTrackName())) {
            if (holder.btnDownload != null) {
                holder.btnDownload.setVisibility(View.GONE);
            }
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
        } else {
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.GONE);
            }
            if (holder.btnDownload != null) {
                holder.btnDownload.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAlbumModel.getAudios().size();
    }

    private Audio getItem(int position) {
        if (position >= 0 && position < mAlbumModel.getAudios().size()) {
            return mAlbumModel.getAudios().get(position);
        }
        return null;
    }

    public void onEvent(AudioEvent event) {

        switch (event.getType()) {
            case DOWNLOAD:
                for (ViewHolder vh : mViewHolders) {
                    if (vh instanceof ViewHolder) {
                        Audio audio = mAlbumModel.getAudios().get(vh.getAdapterPosition());
                        if (audio != null && audio.getTrackId() == event.getId()) {
                            currentSize = event.getCurrent_size();
                            vh.download();
                        }
                    }
                }
                break;
            case STOP:
                for (ViewHolder vh : mViewHolders) {
                    if (vh instanceof ViewHolder) {
                        Audio audio = mAlbumModel.getAudios().get(vh.getAdapterPosition());
                        if (audio != null && audio.getTrackId() == event.getId()) {
                            vh.stop();
                        }
                    }
                }
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.rl_item_album)
        LinearLayout mLinearLayout;

        @Nullable
        @Bind(R.id.tv_item_album)
        TextView tvAlbumTitle;

        @Nullable
        @Bind(R.id.btn_delete)
        ImageButton btnDelete;

        @Nullable
        @Bind(R.id.btn_download)
        ImageButton btnDownload;

        @Nullable
        @Bind(R.id.audioSize)
        TextView audioSize;

        @Nullable
        @Bind(R.id.progressBar)
        SeekBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler = new Handler();
        }


        @OnClick(R.id.rl_item_album)
        void clickItem() {
            Intent intent = new Intent(mContext, PlayActivity.class);
            intent.putExtra("category", mAlbumModel.getCategory());
            intent.putExtra("url", mAlbumModel.getCategory() + "/" + mAlbumModel.getAlbum() + "/" + mAlbumModel.getAudios().get(getAdapterPosition()).getTrackName());
            notifyDataSetChanged();
            mContext.startActivity(intent);
        }

        @OnClick({R.id.btn_download, R.id.btn_delete})
        void options(View view) {
            switch (view.getId()) {
                case R.id.btn_delete:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onDeleteListener(mAlbumModel, getAdapterPosition());
                    }
                    break;
                case R.id.btn_download:
                    mOnItemClickListener.onDownloadListener(mAlbumModel, getAdapterPosition());

                    break;
            }
        }

        public void download() {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            handler.postDelayed(updateProgressRunnable, 100);
        }

        public void stop() {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
            if (btnDelete != null) {
                btnDelete.setVisibility(View.VISIBLE);
            }
            if (btnDownload != null) {
                btnDownload.setVisibility(View.GONE);
            }

            handler.removeCallbacks(updateProgressRunnable);

        }

        private Runnable updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mProgressBar != null) {
                    mProgressBar.setProgress(currentSize);
                }
            }
        };


    }


    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public interface OnItemClickListener {
        void onDeleteListener(AlbumModel model, int position);

        void onDownloadListener(AlbumModel model, int position);
    }
}
