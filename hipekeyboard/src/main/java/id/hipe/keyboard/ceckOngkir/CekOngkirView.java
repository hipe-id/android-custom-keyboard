package id.hipe.keyboard.ceckOngkir;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.*;
import com.zuragan.shopkeepr.R;
import com.zuragan.shopkeepr.data.api.ApiClient;
import com.zuragan.shopkeepr.data.api.AppServices;
import com.zuragan.shopkeepr.data.api.model.Cost;
import com.zuragan.shopkeepr.data.api.model.Cost_;
import com.zuragan.shopkeepr.data.api.model.Lokasi;
import com.zuragan.shopkeepr.data.api.model.Result;
import com.zuragan.shopkeepr.data.api.model.repository.Kurir;
import com.zuragan.shopkeepr.data.api.response.CheckOngkirResponse;
import com.zuragan.shopkeepr.data.api.sender.SearchOngkir;
import com.zuragan.shopkeepr.data.api.sender.SearchQuery;
import com.zuragan.shopkeepr.data.database.RoomDB;
import com.zuragan.shopkeepr.utility.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 24/04/18.
 */
public class CekOngkirView extends RelativeLayout
        implements View.OnClickListener, AutoCompleteAdapter.AutoCompleteCallback,
        AutoCompleteDestinationAdapter.AutoCompleteCallback, CheckOngkirAdapter.onSelectListener {

    public final static int MIN_HEIGHT = 190;
    public final static int MAX_HEIGHT = 450;
    public AppCompatImageView btnBack;
    public LayoutInflater inflater;
    View view;
    Lokasi lokasiOrigin = null;
    Lokasi lokasiDestination = null;
    String textClipboard = "";
    StringBuilder stringBuilder = new StringBuilder();
    Double weight = 0.0;
    RoomDB roomDB;
    CompositeDisposable compositeDisposable;
    String imei = PreferenceUtils.getDataStringFromSP(getContext(), "imei", "");
    private SearchQuery searchQuery = new SearchQuery();
    private List<Result> resultList = new ArrayList<>();
    private AppServices apiService;
    private CompositeDisposable disposable = new CompositeDisposable();
    private SearchOngkir searchOngkir = new SearchOngkir();
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerview;
    private CustomSuggestionRecyclerview rvSugesstion, rvSugesstionDestination;
    private RelativeLayout containerResult;
    private ClearableEditTextView weightInput, originInput, destinationInput;
    private TextView lblSearch;
    private ProgressBar progressBarSearch;
    private FrameLayout btnSearch;
    private InputConnection icWeigth, icOrigin, icDestination;
    private AppCompatCheckBox cbSelectAll;
    private checkOngkirCallback callback;
    private List<Lokasi> listOrigin = new ArrayList<>();
    private List<Lokasi> listDestination = new ArrayList<>();
    private List<Kurir> kurirList = new ArrayList<>();
    private AutoCompleteAdapter autoCompleteAdapter;
    private AutoCompleteDestinationAdapter autoCompleteDestinationAdapter;
    private CheckOngkirAdapter adapter;
    private boolean isSelected = false;
    private boolean isOriginEditable = true;
    private boolean isDestinationEditable = true;
    private List<Result> copyList = new ArrayList<>();

    public CekOngkirView(Context context) {
        super(context);
        initViews(context);
    }

    public CekOngkirView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    @Override
    public void onClick(View view) {
        if (callback != null) {
            switch (view.getId()) {
                case R.id.btn_back_cek_ongkir:
                    clearText();
                    isSelected = false;
                    setButtonSearchState();
                    recyclerview.setVisibility(GONE);
                    rvSugesstion.setVisibility(GONE);
                    rvSugesstionDestination.setVisibility(GONE);
                    containerResult.setVisibility(GONE);
                    callback.onBackFromCheckOngkir();
                    break;
                case R.id.btn_search:
                    if (isSelected) {
                        checkSelected();
                        copyClipBoard();
                    } else {
                        validationInput();
                        isSelected = false;
                    }

                    break;
            }
        }
    }

    //handler input make sure input doesn't empty
    public void validationInput() {
        //int weight = Integer.parseInt(weightInput.getText().toString());
        weight = Double.valueOf(weightInput.getText().toString());

        if (TextUtils.isEmpty(weightInput.getText().toString())) {
            showMessage(getContext().getResources().getString(R.string.empty_input_weight));
        } else if (weightInput.getText().toString().equalsIgnoreCase("0")) {
            showMessage(getContext().getResources().getString(R.string.weight_greater));
        } else if (TextUtils.isEmpty(originInput.getText().toString())) {
            showMessage(getContext().getResources().getString(R.string.empty_input_deliver_from));
            searchQuery.origin_id = 0;
            searchQuery.origin_type = "";
        } else if (TextUtils.isEmpty(destinationInput.getText().toString())) {
            showMessage(getContext().getResources().getString(R.string.empty_input_deliver_to));
            searchQuery.destination_id = 0;
            searchQuery.destination_type = "";
        } else {
            rvSugesstion.setVisibility(GONE);
            rvSugesstionDestination.setVisibility(GONE);

            //save last used origin location for default
            PreferenceUtils.setDataIntTOSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_ID,
                    searchQuery.origin_id);

            PreferenceUtils.setDataStringToSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_NAME,
                    searchQuery.origin_name);

            PreferenceUtils.setDataStringToSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_FULLNAME,
                    searchQuery.origin_full_name);

            PreferenceUtils.setDataStringToSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_TYPE,
                    searchQuery.origin_type);
            weight = weight * 10000;
            checkOngkir();
        }
    }

    //handler item click suggestion origin location
    @Override
    public void onClickItem(Lokasi lokasi) {
        lokasiOrigin = lokasi;
        searchQuery.origin_id = lokasi.id;
        searchQuery.origin_name = lokasi.name;
        searchQuery.origin_type = lokasi.type;

        searchQuery.origin_full_name = lokasi.fullName;
        rvSugesstion.setVisibility(GONE);

        Timber.d("onClickItem origin");
        if (originInput.isFocused()) {
            originInput.setText(lokasi.fullName.toUpperCase());
            originInput.setSelection(originInput.getText().length());
            isOriginEditable = false;
        }
    }

    //handler item click suggeston destination location
    @Override
    public void onClickItemDestination(Lokasi lokasi) {
        lokasiDestination = lokasi;
        searchQuery.destination_id = lokasi.id;
        searchQuery.destination_name = lokasi.name;
        searchQuery.destination_type = lokasi.type;

        searchQuery.destination_full_name = lokasi.fullName;
        rvSugesstionDestination.setVisibility(GONE);

        Timber.d("onClickItem destination");
        if (destinationInput.isFocused()) {
            destinationInput.setText(lokasi.fullName.toUpperCase());
            destinationInput.setSelection(destinationInput.getText().length());
            isDestinationEditable = false;
        }
    }

    //handler item click courir
    @Override
    public void onSelected(int position) {
        checkSelected();
    }

    public void setListener(checkOngkirCallback ongkirCallback) {
        this.callback = ongkirCallback;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews(final Context context) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.zuragan_cek_ongkir_view, this, true);

        nestedScrollView = view.findViewById(R.id.nested_scrollview);
        weightInput = view.findViewById(R.id.input_berat);
        originInput = view.findViewById(R.id.input_asal);
        destinationInput = view.findViewById(R.id.input_tujuan);
        containerResult = view.findViewById(R.id.container_result_ongkir);
        recyclerview = view.findViewById(R.id.recyclerview);
        rvSugesstion = view.findViewById(R.id.rv_sugesstion_origin);
        rvSugesstionDestination = view.findViewById(R.id.rv_sugesstion_destination);
        lblSearch = view.findViewById(R.id.lbl_search);
        progressBarSearch = view.findViewById(R.id.progressbar);
        btnSearch = view.findViewById(R.id.btn_search);
        btnBack = view.findViewById(R.id.btn_back_cek_ongkir);
        cbSelectAll = view.findViewById(R.id.checkbox_select_all);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adapter.setSelectAll(true);
                isSelected = true;
            } else {
                adapter.setSelectAll(false);
                isSelected = false;
            }
            setButtonSearchState();
        });

        nestedScrollView.getParent().requestChildFocus(nestedScrollView, nestedScrollView);

        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        icWeigth = weightInput.onCreateInputConnection(new EditorInfo());
        icOrigin = originInput.onCreateInputConnection(new EditorInfo());
        icDestination = destinationInput.onCreateInputConnection(new EditorInfo());

        weightInput.setFocusableInTouchMode(true);
        originInput.setFocusableInTouchMode(true);
        destinationInput.setFocusableInTouchMode(true);

        roomDB = RoomDB.getInstance(getContext());
        compositeDisposable = new CompositeDisposable();

        weightInput.setOnTouchListener((view, motionEvent) -> {
            callback.onShowKeyboardNumber();
            rvSugesstion.setVisibility(GONE);
            rvSugesstionDestination.setVisibility(GONE);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 190,
                    getResources().getDisplayMetrics());
            nestedScrollView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            return false;
        });

        //handler if keyboard hide after search ongkir
        originInput.setOnTouchListener((view, motionEvent) -> {
            callback.onShowKeyboard();
            rvSugesstionDestination.setVisibility(GONE);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 190,
                    getResources().getDisplayMetrics());
            nestedScrollView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

            return false;
        });

        //handler if keyboard hide after search ongkir
        destinationInput.setOnTouchListener((view, motionEvent) -> {
            callback.onShowKeyboard();
            rvSugesstion.setVisibility(GONE);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 190,
                    getResources().getDisplayMetrics());
            nestedScrollView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            return false;
        });

        originInput.setListener(() -> isOriginEditable = true);
        destinationInput.setListener(() -> isDestinationEditable = true);

        //setHeight(MIN_HEIGHT);
        //getAllCourir();
        //handlerOriginAutocomplete(view);
        //handlerDestiantioAutocomplete(view);
        //initAutoComplete();
        //setDefaultOriginLocation();
    }

    public void show() {
        if (this.getVisibility() == GONE || this.getVisibility() == INVISIBLE) {

            searchQuery.origin_id =
                    PreferenceUtils.getDataIntFromSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_ID, 0);
            searchQuery.origin_name = PreferenceUtils.getDataStringFromSP(getContext(),
                    PreferenceUtils.DEF_ORIGIN_LOCATION_NAME, "");
            searchQuery.origin_full_name = PreferenceUtils.getDataStringFromSP(getContext(),
                    PreferenceUtils.DEF_ORIGIN_LOCATION_FULLNAME, "");
            searchQuery.origin_type = PreferenceUtils.getDataStringFromSP(getContext(),
                    PreferenceUtils.DEF_ORIGIN_LOCATION_TYPE, "");

            weightInput.setText("1");
            weightInput.setSelection(weightInput.getText().length());
            if (searchQuery.origin_id != 0) {
                originInput.setText(searchQuery.origin_full_name.toUpperCase());
                originInput.setSelection(originInput.getText().length());
            }

            icWeigth = weightInput.onCreateInputConnection(new EditorInfo());
            icOrigin = originInput.onCreateInputConnection(new EditorInfo());
            icDestination = destinationInput.onCreateInputConnection(new EditorInfo());

            weightInput.setFocusableInTouchMode(true);
            originInput.setFocusableInTouchMode(true);
            destinationInput.setFocusableInTouchMode(true);

            weightInput.requestFocus();
            originInput.requestFocus();
            destinationInput.requestFocus();

            //clear flag selection on list item check ongkir
            isSelected = false;
            setButtonSearchState();
            setHeight(MIN_HEIGHT);
            getAllCourir();
            handlerOriginAutocomplete(view);
            handlerDestiantioAutocomplete(view);
            initAutoComplete();

            this.setVisibility(VISIBLE);
        }
    }

    //initial default weight and origin location
    public void setDefaultOriginLocation() {
        searchQuery.origin_id =
                PreferenceUtils.getDataIntFromSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_ID, 0);
        searchQuery.origin_name =
                PreferenceUtils.getDataStringFromSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_NAME,
                        "");
        searchQuery.origin_full_name = PreferenceUtils.getDataStringFromSP(getContext(),
                PreferenceUtils.DEF_ORIGIN_LOCATION_FULLNAME, "");
        searchQuery.origin_type =
                PreferenceUtils.getDataStringFromSP(getContext(), PreferenceUtils.DEF_ORIGIN_LOCATION_TYPE,
                        "");

        weightInput.setText("1");
        weightInput.setSelection(weightInput.getText().length());
        if (searchQuery.origin_id != 0) {
            originInput.setText(searchQuery.origin_full_name.toUpperCase());
            originInput.setSelection(originInput.getText().length());
        }
        isSelected = false;
        setButtonSearchState();
    }

    public void setHeight(int h) {
        nestedScrollView.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        StringUtils.getDp(getContext(), h)));
    }

    public void setHeightSuggestionItem(RecyclerView recyclerview, int h) {
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        StringUtils.getDp(getContext(), h));

        layoutParams.topMargin = StringUtils.getDp(getContext(), 44);
        recyclerview.setLayoutParams(layoutParams);
    }

    private void setupAdapter() {

        //setup adapter result
        adapter = new CheckOngkirAdapter(getContext(), resultList, kurirList);
        adapter.setListener(this);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setAdapter(adapter);
    }

    private void initAutoComplete() {
        //setup adapter suggestion origin
        autoCompleteAdapter = new AutoCompleteAdapter(getContext(), listOrigin);
        autoCompleteAdapter.setCallback(this);
        rvSugesstion.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSugesstion.setAdapter(autoCompleteAdapter);

        //setup adapter suggestion destination
        autoCompleteDestinationAdapter =
                new AutoCompleteDestinationAdapter(getContext(), listDestination);
        autoCompleteDestinationAdapter.setCallback(this);
        rvSugesstionDestination.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSugesstionDestination.setAdapter(autoCompleteDestinationAdapter);
    }

    private void handlerOriginAutocomplete(View view) {

        originInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Timber.d("afterTextChanged() %s", editable.toString());
                if (searchQuery.origin_full_name != null) {
                    if (!editable.toString().equalsIgnoreCase(searchQuery.origin_full_name)) {
                        searchQuery.origin_full_name = null;
                    }
                }

                if (TextUtils.isEmpty(editable.toString())) {
                    Timber.d("empty textchange");
                    rvSugesstion.setVisibility(GONE);
                    searchQuery.origin_full_name = null;
                } else {
                    getLokasiByQuery(editable.toString());
                }
            }
        });
    }

    public void handlerDestiantioAutocomplete(View view) {

        destinationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Timber.d("afterTextChanged() %s", editable.toString());
                if (searchQuery.destination_full_name != null) {
                    if (!editable.toString().equalsIgnoreCase(searchQuery.destination_full_name)) {
                        searchQuery.destination_full_name = null;
                    }
                }

                if (TextUtils.isEmpty(editable.toString())) {
                    Timber.d("empty textchange");
                    rvSugesstionDestination.setVisibility(GONE);
                    searchQuery.destination_full_name = null;
                } else {
                    getLokasiByType(editable.toString());
                }
            }
        });
    }

    public void getAllCourir() {
        compositeDisposable.add(roomDB.courirDao()
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<List<Kurir>>() {
                    @Override
                    public void onSuccess(List<Kurir> kurirs) {
                        Timber.d("getAllCourir() DB %s", kurirs.size());

                        kurirList.addAll(kurirs);
                        setupAdapter();
                        Timber.d("getAllCourir() List %s", kurirList.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("getAllCourir() %s", e.getLocalizedMessage());
                    }
                }));
    }

    public void getLokasiByQuery(String q) {
        Timber.d("autoCompleteLocation() %s");

        compositeDisposable.add(roomDB.locationDao()
                .getLokasibyType(q, "city")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<List<Lokasi>>() {
                    @Override
                    public void onSuccess(List<Lokasi> lokasis) {
                        Timber.d("getAutoComplete() %s", lokasis.size());
                        showCities(lokasis, "origin", q);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("getAutoComplete() %s", e.getLocalizedMessage());
                    }
                }));
    }

    public void getLokasiByType(String q) {
        Timber.d("autoCompleteLocation() %s");

        compositeDisposable.add(roomDB.locationDao()
                .getLokasibyQuery(q)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<List<Lokasi>>() {
                    @Override
                    public void onSuccess(List<Lokasi> lokasis) {
                        Timber.d("getAutoComplete() %s", lokasis.size());
                        showCities(lokasis, "all", q);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("getAutoComplete() %s", e.getLocalizedMessage());
                    }
                }));
    }

    public void showCities(List<Lokasi> lokasis, String which, String q) {

        if (which.equals("origin")) {
            if (lokasis.size() > 0) {

                nestedScrollView.post(() -> {
                    nestedScrollView.fullScroll(View.FOCUS_DOWN);
                    originInput.requestFocus();
                });

                if (listOrigin.size() > 0) {
                    listOrigin.clear();
                }

                listOrigin.addAll(lokasis);
                autoCompleteAdapter.notifyDataSetChanged();

                //handling for after click item sugesstion
                if (listOrigin.size() > 1 && !originInput.getText()
                        .toString()
                        .equalsIgnoreCase(listOrigin.get(0).fullName)) {

                    if (listOrigin.size() == 1) {
                        Timber.d("setHeight 50");
                        setHeightSuggestionItem(rvSugesstion, 50);
                    }

                    rvSugesstion.setVisibility(VISIBLE);
                }
            } else {
                rvSugesstion.setVisibility(GONE);
                AnalyticUtils.getInstance(getContext()).cekOngkirOriginNotFound(imei, q);
            }
        } else {
            if (lokasis.size() > 0) {

                if (recyclerview.getVisibility() == GONE) {
                    nestedScrollView.post(new Runnable() {
                        public void run() {
                            nestedScrollView.fullScroll(View.FOCUS_DOWN);
                            destinationInput.requestFocus();
                        }
                    });
                }
                if (listDestination.size() > 0) {
                    listDestination.clear();
                }

                listDestination.addAll(lokasis);
                autoCompleteDestinationAdapter.notifyDataSetChanged();

                //handling for after click item sugesstion
                if (listDestination.size() > 1 && !destinationInput.getText()
                        .toString()
                        .equalsIgnoreCase(listDestination.get(0).fullName)) {
                    rvSugesstionDestination.setVisibility(VISIBLE);
                }

                if (listDestination.size() == 1) {
                    Timber.d("setHeight 40");
                    setHeightSuggestionItem(rvSugesstionDestination, 50);
                }
            } else {
                rvSugesstionDestination.setVisibility(GONE);
                AnalyticUtils.getInstance(getContext()).cekOngkirDestinationNotFound(imei, q);
            }
        }
    }

    public void checkOngkir() {

        progressBarSearch.setVisibility(VISIBLE);
        lblSearch.setVisibility(GONE);

        weight = Double.valueOf(weightInput.getText().toString()) * 1000;
        searchOngkir.origin = searchQuery.origin_id;
        searchOngkir.originType = searchQuery.origin_type;
        searchOngkir.destination = searchQuery.destination_id;
        searchOngkir.destinationType = searchQuery.destination_type;
        searchOngkir.courier = "jne:pos:tiki:wahana:sicepat:jnt";
        searchOngkir.weight = weight.intValue();
        searchOngkir.height = 1;
        searchOngkir.width = 1;
        searchOngkir.length = 1;

        apiService = ApiClient.getClient(getContext()).create(AppServices.class);
        disposable.add(apiService.getTariff(searchOngkir)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<CheckOngkirResponse>() {
                    @Override
                    public void onNext(CheckOngkirResponse response) {

                        if (response.code == 200) {

                            if (response.data.results.size() > 0) {
                                showResult(response.data.results);
                            }
                        } else {
                            showMessage(getContext().getString(R.string.server_down));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBarSearch.setVisibility(GONE);
                        lblSearch.setVisibility(VISIBLE);
                        Timber.e("setDataCheckOngkir() %s", e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("setDataCheckOngkirComplete()");
                        progressBarSearch.setVisibility(GONE);
                        lblSearch.setVisibility(VISIBLE);
                    }
                }));
    }

    public void showResult(List<Result> data) {
        if (resultList.size() > 0) resultList.clear();

        resultList.addAll(data);
        recyclerview.setVisibility(View.VISIBLE);
        containerResult.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        callback.onShowResult();

        setHeight(MAX_HEIGHT);
    }

    public void setButtonSearchState() {
        lblSearch.setText(isSelected ? getResources().getString(R.string.copy)
                : getResources().getString(R.string.check_postal_fee));
    }

    public void checkSelected() {
        copyList = new ArrayList<>();
        ArrayList<String> courirListCopy = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            if (adapter.isItemSelected(i)) {
                isSelected = true;
                courirListCopy.add(resultList.get(i).code);
                copyList.add(resultList.get(i));
            }
        }

        if (copyList.size() == 0) isSelected = false;
        AnalyticUtils.getInstance(getContext()).cekOngkirCheckedCouriers(imei, courirListCopy);
        setButtonSearchState();
        Timber.d("copyList %s,", copyList.size());
        stringBuilder = new StringBuilder();
        stringBuilder.append(searchQuery.origin_name + " \u2192 " + searchQuery.destination_name);
        stringBuilder.append(" - " + weightInput.getText().toString() + " kg" + "\n");
        for (Result result : copyList) {
            for (Cost cost : result.costs) {
                stringBuilder.append(result.code.toUpperCase());
                stringBuilder.append(" " + cost.service);
                stringBuilder.append(" ");
                for (Cost_ cost_ : cost.cost) {
                    stringBuilder.append("\u2022 " + cost_.etd);
                    stringBuilder.append(" ");
                    stringBuilder.append("\u2022" + " Rp" + StringUtils.decimalFormatter(cost_.value));
                    stringBuilder.append("\n");
                }
            }
        }
    }

    public void copyClipBoard() {
        textClipboard = stringBuilder.toString();
        ClipboardManager clipboard =
                (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Cek Ongkir", textClipboard);
        clipboard.setPrimaryClip(clip);
        showMessage(getResources().getString(R.string.success_copy_postal_fee));
        callback.onPasteClipBoard(textClipboard);
        Timber.d("copyClipboard() %s", textClipboard);
        AnalyticUtils.getInstance(getContext()).cekOngkirCopyResult(imei);
    }

    public boolean isFocused() {
        return weightInput.isFocused() || originInput.isFocused() || destinationInput.isFocused();
    }

    public void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public int getCapsMode(EditorInfo attr) {
        if (weightInput.isFocused()) {
            return icWeigth.getCursorCapsMode(attr.inputType);
        }
        if (originInput.isFocused()) {
            return icOrigin.getCursorCapsMode(attr.inputType);
        }
        if (destinationInput.isFocused()) {
            return icDestination.getCursorCapsMode(attr.inputType);
        }
        return 0;
    }

    public void insert(int keyCode) {
        if (weightInput.isFocused()) {
            weightInput.getText().insert(weightInput.getSelectionStart(), String.valueOf((char) keyCode));

            AnalyticUtils.getInstance(getContext()).cekOngkirCorrectWeight();
        }
        if (originInput.isFocused() && isOriginEditable) {
            originInput.getText().insert(originInput.getSelectionStart(), String.valueOf((char) keyCode));
        }
        if (destinationInput.isFocused() && isDestinationEditable) {
            destinationInput.getText()
                    .insert(destinationInput.getSelectionStart(), String.valueOf((char) keyCode));
        }
    }

    public ClearableEditTextView getEditText() {
        if (weightInput.isFocused()) {
            return weightInput;
        }

        if (originInput.isFocused() && isOriginEditable) {
            return originInput;
        }
        if (destinationInput.isFocused() && isDestinationEditable) {
            return destinationInput;
        }
        return null;
    }

    public void clearText() {
        //clear text
        weightInput.setText("");
        originInput.setText("");
        destinationInput.setText("");

        // clear focus for reset cursor to focus on edittext other App
        weightInput.clearFocus();
        originInput.clearFocus();
        destinationInput.clearFocus();

        isOriginEditable = true;
        isDestinationEditable = true;
    }

    public interface checkOngkirCallback {

        void onShowKeyboard();

        void onShowKeyboardNumber();

        void onShowResult();

        void onPasteClipBoard(String clipboard);

        void onBackFromCheckOngkir();
    }
}
