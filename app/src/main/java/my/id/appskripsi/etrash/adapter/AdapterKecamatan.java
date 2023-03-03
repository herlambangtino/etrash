package my.id.appskripsi.etrash.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import my.id.appskripsi.etrash.ActivityTps;
import my.id.appskripsi.etrash.R;
import my.id.appskripsi.etrash.list.Kecamatan;

public class AdapterKecamatan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Kecamatan> items = new ArrayList<>();

    public Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Kecamatan obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterKecamatan(Context context, List<Kecamatan> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNmKecamatan;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            tvNmKecamatan = v.findViewById(R.id.tvNmKecamatan);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kecamatan, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final OriginalViewHolder view = (OriginalViewHolder) holder;
        final Kecamatan p = items.get(position);
        view.tvNmKecamatan.setText(p.nmkecamatan);
        view.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("pilihkecamatan");
                intent.putExtra("kecamatan", p.idkecamatan);
                intent.putExtra("nmkecamatan", p.nmkecamatan);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
