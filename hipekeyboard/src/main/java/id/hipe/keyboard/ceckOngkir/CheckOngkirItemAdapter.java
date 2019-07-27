package id.hipe.keyboard.ceckOngkir;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.zuragan.shopkeepr.R;
import com.zuragan.shopkeepr.data.api.model.Cost;
import com.zuragan.shopkeepr.utility.StringUtils;

import java.util.List;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 15/05/18.
 */
public class CheckOngkirItemAdapter
        extends RecyclerView.Adapter<CheckOngkirItemAdapter.ViewHolder> {

    private Context context;
    private List<Cost> costs;

    public CheckOngkirItemAdapter(Context context, List<Cost> costs) {
        this.context = context;
        this.costs = costs;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_cek_resi_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(costs.get(position));

    }

    @Override
    public int getItemCount() {
        return costs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lbl_service_courir)
        TextView lblServiceCourir;
        @BindView(R.id.lbl_price)
        TextView lblPrice;
        @BindView(R.id.lbl_est_day)
        TextView lblEstDay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Cost cost) {
            lblServiceCourir.setText("\u2022" + cost.service);
            lblPrice.setText("Rp" + StringUtils.decimalFormatter(cost.cost.get(0).value));
            lblEstDay.setText("\u2022" + cost.cost.get(0).etd);
        }
    }
}
