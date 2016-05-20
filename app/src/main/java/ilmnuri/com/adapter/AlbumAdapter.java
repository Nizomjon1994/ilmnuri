//package ilmnuri.com.adapter;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import ilmnuri.com.PlayActivity;
//import ilmnuri.com.R;
//import ilmnuri.com.Utility.Utils;
//import ilmnuri.com.model.AlbumModel;
//import ilmnuri.com.model.Api;
//
//
//public class AlbumAdapter extends BaseAdapter {
//    private AlbumModel albumModel;
//    private Context context;
//
//    public AlbumAdapter(Context context, AlbumModel albumModel) {
//        this.context = context;
//        this.albumModel = albumModel;
//    }
//
//    @Override
//    public int getCount() {
//        return albumModel.getItems().size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        View v = convertView;
//        if (convertView == null) {
//            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
//        }
//
//        LinearLayout relativeLayout = (LinearLayout) v.findViewById(R.id.rl_item_album);
//        TextView textView = (TextView) v.findViewById(R.id.tv_item_album);
//        ImageButton btnDelete = (ImageButton) v.findViewById(R.id.btn_delete);
//        ImageButton btnDownload = (ImageButton) v.findViewById(R.id.btn_download);
//        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
//
//
//
//        textView.setText(albumModel.getItems().get(position).replace(".mp3", "").replace("_", " "));
//
//        if (Utils.checkFileExist(Api.localPath + "/" + albumModel.getItems().get(position))) {
//            btnDownload.setVisibility(View.GONE);
//            btnDelete.setVisibility(View.VISIBLE);
//        } else {
//            btnDelete.setVisibility(View.GONE);
//            btnDownload.setVisibility(View.VISIBLE);
//        }
//
//        btnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDownload(position);
//            }
//        });
//
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, PlayActivity.class);
//                intent.putExtra("category", albumModel.getCategory());
//                intent.putExtra("url", albumModel.getCategory() + "/" + albumModel.getAlbum() + "/" + albumModel.getItems().get(position));
//                notifyDataSetChanged();
//                context.startActivity(intent);
//            }
//        });
//
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertMessage(position);
//            }
//        });
//        return v;
//    }
//
//    private void alertMessage(final int position) {
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case DialogInterface.BUTTON_POSITIVE:
//                        // Yes button clicked
//                        Utils.deleteFile(Api.localPath + "/" + albumModel.getItems().get(position));
//                        Utils.showToast(context, "Darslik o'chirib tashlandi!");
//                        notifyDataSetChanged();
//
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        // No button clicked
//                        // do nothing
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Bu darsni o'chirib tashlashni xohlaysizmi?")
//                .setPositiveButton("Ha", dialogClickListener)
//                .setNegativeButton("Yo'q", dialogClickListener).show();
//    }
//
//    private void alertDownload(final int position) {
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case DialogInterface.BUTTON_POSITIVE:
//                        // Yes button clicked
//                        Intent intent = new Intent(context, PlayActivity.class);
//                        intent.putExtra("category", albumModel.getCategory());
//                        intent.putExtra("url", albumModel.getCategory() + "/" + albumModel.getAlbum() + "/" + albumModel.getItems().get(position));
//                        notifyDataSetChanged();
//                        context.startActivity(intent);
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        // No button clicked
//                        // do nothing
//                        notifyDataSetChanged();
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Bu darsni yuklab olishni xohlaysizmi?")
//                .setPositiveButton("Albatta", dialogClickListener)
//                .setNegativeButton("Yo'q", dialogClickListener).show();
//    }
//}