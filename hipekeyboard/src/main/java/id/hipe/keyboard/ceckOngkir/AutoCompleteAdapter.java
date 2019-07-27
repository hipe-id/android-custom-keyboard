package id.hipe.keyboard.ceckOngkir;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.zuragan.shopkeepr.R;
import com.zuragan.shopkeepr.data.api.model.Lokasi;

import java.util.List;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 17/05/18.
 */
public class AutoCompleteAdapter extends RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder> {

    private Context context;
    private List<Lokasi> lokasiList;
    private AutoCompleteCallback callback;

    public AutoCompleteAdapter(Context context, List<Lokasi> lokasiList) {
        this.context = context;
        this.lokasiList = lokasiList;
    }

    public void setCallback(AutoCompleteCallback callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_dropdown_2ine, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(lokasiList.get(position));
    }

    @Override
    public int getItemCount() {
        return lokasiList.size();
    }

    public interface AutoCompleteCallback {
        void onClickItem(Lokasi lokasi);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text1)
        TextView text1;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.list_item)
        public void onClickItem() {
            callback.onClickItem(lokasiList.get(getAdapterPosition()));
        }

        public void bindItem(Lokasi lokasi) {
            text1.setText(lokasi.fullName);
        }
    }
}
