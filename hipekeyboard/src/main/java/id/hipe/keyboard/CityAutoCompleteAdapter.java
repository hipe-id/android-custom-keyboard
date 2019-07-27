package id.hipe.keyboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.zuragan.shopkeepr.data.api.model.Lokasi;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

public class CityAutoCompleteAdapter extends ArrayAdapter<Lokasi> {

    private static final int MAX_RESULTS = 10;
    int layoutResourceId;
    private Context mContext;
    private List<Lokasi> items = new ArrayList<>();
    private List<Lokasi> items_All = new ArrayList<>();
    private List<Lokasi> items_Suggestion = new ArrayList<>();

    public CityAutoCompleteAdapter(@NonNull Context mContext, @LayoutRes int resource,
                                   List<Lokasi> items) {
        super(mContext, resource, items);
        this.mContext = mContext;
        this.layoutResourceId = resource;
        this.items = new ArrayList<>(items);
        this.items_All = new ArrayList<>(items);
        this.items_Suggestion = new ArrayList<>();
        Timber.d("lokasiSugesstion %s", items.size());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Lokasi getItem(int index) {
        return items.get(index);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        Lokasi city = items.get(position);

        ((TextView) convertView.findViewById(R.id.text1)).setText(city.fullName);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((Lokasi) resultValue).fullName;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    items_Suggestion.clear();

                    for (Lokasi city : items_All) {
                        items_Suggestion.add(city);
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = items_Suggestion;
                    filterResults.count = items_Suggestion.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                items.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using items.addAll((ArrayList<City) >) results.values);
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof Lokasi) {
                            items.add((Lokasi) object);
                        }
                    }
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    items.addAll(items_All);
                }
                notifyDataSetChanged();
            }
        };
    }
}

