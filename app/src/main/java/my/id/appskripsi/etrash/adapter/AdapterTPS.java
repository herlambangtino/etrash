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

import androidx.recyclerview.widget.RecyclerView;
import my.id.appskripsi.etrash.ActivityDetailTPS;
import my.id.appskripsi.etrash.R;
import my.id.appskripsi.etrash.list.Tps;

public class AdapterTPS extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Tps> items = new ArrayList<>();

    public Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Tps obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterTPS(Context context, List<Tps> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNmTPS;
        public TextView tvKecamatan;
        public TextView tvAlamat;
        public TextView tvJarak;
        public TextView tvWaktuTempuh;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            tvNmTPS = v.findViewById(R.id.tvNmTPS);
            tvKecamatan = v.findViewById(R.id.tvKecamatan);
            tvAlamat = v.findViewById(R.id.tvAlamat);
            tvJarak = v.findViewById(R.id.tvJarak);
            tvWaktuTempuh = v.findViewById(R.id.tvWaktuTempuh);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tps, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final OriginalViewHolder view = (OriginalViewHolder) holder;
        final Tps p = items.get(position);
        view.tvNmTPS.setText(p.nmtps);
        view.tvKecamatan.setText(p.nmkecamatan);
        view.tvAlamat.setText(p.alamat);
        view.tvJarak.setText(p.jarak);
        view.tvWaktuTempuh.setText(p.waktutempuh);
        view.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ActivityDetailTPS.class);

                intent.putExtra("nmtps", p.nmtps);
                intent.putExtra("latitude", p.latitude);
                intent.putExtra("longitude", p.longitude);
                intent.putExtra("alamat", p.alamat);
                intent.putExtra("jarak", p.jarak);
                intent.putExtra("waktutempuh", p.waktutempuh);
                intent.putExtra("waktujemput", p.waktujemput);
                intent.putExtra("deskripsi", p.deskripsi);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
