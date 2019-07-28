package id.hipe.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dika Putra on 07/05/18.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> data = new ArrayList<>();
    protected ItemCallback<T> itemCallback;
    protected ListCallback<T> listCallback;

    @LayoutRes
    protected abstract int setView(int viewType);

    protected abstract RecyclerView.ViewHolder itemViewHolder(View view, int viewType);

    public void setItemCallback(ItemCallback<T> itemCallback) {
        this.itemCallback = itemCallback;
    }

    public void setListCallback(ListCallback<T> listCallback) {
        this.listCallback = listCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(setView(viewType), parent, false);
        return itemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            if (listCallback != null)
                listCallback.onReachTop(get(position));
        } else if (position == data.size() - 1) {
            if (listCallback != null)
                listCallback.onReachEnd(position, get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(T t) {
        data.add(t);
        notifyItemInserted(getItemCount() - 1);
    }

    public void add(T t, int position) {
        data.add(position, t);
        notifyItemInserted(position);
    }

    public void addAll(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void addNew(List<T> data) {
        int oldSize = this.data.size();
        this.data.addAll(data);
        notifyItemRangeInserted(oldSize, this.data.size());
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public List<T> getAll() {
        return data;
    }

    public T get(int position) {
        return data.get(position);
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public interface ItemCallback<T> {
        void onAdapterClick(View view, int position, T t, Bundle bundle);
    }

    public interface ListCallback<T> {
        void onReachTop(T t);

        void onReachEnd(int position, T t);
    }
}
