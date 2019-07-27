package id.hipe.keyboard.ceckOngkir;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.zuragan.shopkeepr.R;
import com.zuragan.shopkeepr.data.api.model.Cost;
import com.zuragan.shopkeepr.data.api.model.Result;
import com.zuragan.shopkeepr.data.api.model.repository.Kurir;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 15/05/18.
 */
public class CheckOngkirAdapter extends RecyclerView.Adapter<CheckOngkirAdapter.ViewHolder> {

    private Context context;
    private List<Result> resultList;
    private List<Kurir> kurirList;
    private SparseBooleanArray sparseArrSelection;
    private onSelectListener mListener;

    public CheckOngkirAdapter(Context context, List<Result> resultList, List<Kurir> kurirList) {
        this.context = context;
        this.resultList = resultList;
        this.kurirList = kurirList;
        this.sparseArrSelection = new SparseBooleanArray(1);
    }

    public void setListener(onSelectListener mListener) {
        this.mListener = mListener;
    }

    public boolean isItemSelected(int position) {
        return sparseArrSelection.get(position);
    }

    public void setItemSelected(int position, boolean isChecked) {
        if (isChecked) {
            sparseArrSelection.put(position, isChecked);
        } else {
            sparseArrSelection.delete(position);
        }
    }

    public void setSelectAll(boolean isChecked) {
        for (int i = 0; i < resultList.size(); i++) {
            sparseArrSelection.put(i, isChecked);
            notifyDataSetChanged();
        }
    }

    public void clearChoices() {
        sparseArrSelection.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_courir_checkongkir_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.initData(resultList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public String getLogo(String code) {
        String url = "";

        if (code.equalsIgnoreCase("J&T")) code = "jnt";
        for (Kurir kurir : kurirList) {

            if (kurir.code.equalsIgnoreCase(code)) {
                url = kurir.logo;
                break;
            }
        }
        return url;
    }

    public interface onSelectListener {
        void onSelected(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.logo)
        ImageView logo;
        @BindView(R.id.check_courir)
        CheckBox cbCourir;
        @BindView(R.id.recyclerview)
        RecyclerView recyclerview;
        @BindView(R.id.item_courir)
        LinearLayout itemView;
        CheckOngkirItemAdapter adapter;
        List<Cost> costList = new ArrayList<>();


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            adapter = new CheckOngkirItemAdapter(context, costList);
            recyclerview.setLayoutManager(new LinearLayoutManager(context));
            recyclerview.setAdapter(adapter);
        }

        public void initData(Result data, int position) {
            if (sparseArrSelection.get(position)) {
                cbCourir.setChecked(true);
            } else {
                cbCourir.setChecked(false);
            }

            cbCourir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        cbCourir.setChecked(true);
                    } else {
                        cbCourir.setChecked(false);
                    }
                    Timber.d("onClickItem checkBox");
                    setItemSelected(position, isChecked);
                    mListener.onSelected(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onSelected(getAdapterPosition());
                    Timber.d("onClickItem itemView");
                    if (cbCourir.isChecked()) {
                        cbCourir.setChecked(false);
                    } else {
                        cbCourir.setChecked(true);
                    }
                }
            });

            if (costList.size() > 0) costList.clear();
            costList.addAll(data.costs);
            adapter.notifyDataSetChanged();
            Glide.with(context).load(getLogo(data.code)).into(logo);
        }
    }
}
