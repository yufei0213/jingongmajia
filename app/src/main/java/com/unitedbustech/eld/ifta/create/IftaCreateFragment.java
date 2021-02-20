package com.unitedbustech.eld.ifta.create;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.MainActivity;
import com.unitedbustech.eld.activity.WebActivity;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.ifta.common.IftaParamsKey;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.Arith;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LocationUtil;
import com.unitedbustech.eld.util.PictureUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.view.DatePickerDialog;
import com.unitedbustech.eld.view.LoadingDialog;
import com.unitedbustech.eld.view.PromptDialog;
import com.unitedbustech.eld.view.TabMenu;
import com.unitedbustech.eld.view.ThumbnailContainerView;
import com.unitedbustech.eld.view.TitleBar;
import com.unitedbustech.eld.view.UIWebView;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * @author yufei0213
 * @date 2018/6/25
 * @description IftaCreateFragment
 */
public class IftaCreateFragment extends BaseFragment implements TitleBar.TitleBarListener,
        IftaCreateContract.View,
        ThumbnailContainerView.OnAddImageListener,
        View.OnClickListener {

    private static final int REQUEST_VEHICLE = 1;
    private static final int REQUEST_STATE = 2;
    private static final int REQUEST_FUEL = 3;
    private static final int REQUEST_PIC = 4;

    private static final int DEFAULT_DECIMAL_NUMBER = 3;
    private static final InputFilter[] INPUT_FILTER_ARRAY = new InputFilter[]{new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            String lastInputContent = dest.toString();
            if (lastInputContent.contains(".")) {

                int index = lastInputContent.indexOf(".");
                if (dend - index >= DEFAULT_DECIMAL_NUMBER + 1) {

                    return "";
                }
            }
            return null;
        }
    }};

    private static final String NUMBER_REGEX = "^\\d+(\\.\\d+)?$";

    private Animation loadingAnim;

    private TitleBar titleBar;

    private EditText driverNameInput;
    private EditText vehicleIdInput;
    private EditText purchaseDateInput;
    private EditText stateInput;
    private ImageView loadStateBtn;
    private EditText fuelTypeInput;
    private EditText unitPriceInput;
    private EditText purchasedGallonstInput;
    private EditText totalPriceInput;

    private ImageView fuelTypeArrowView;

    private TextView vehicleIdWarningView;
    private TextView stateWarningView;
    private TextView fuelWarningView;
    private TextView unitWarningView;
    private TextView gallonsWarningView;
    private TextView totalPriceWarningView;

    private ThumbnailContainerView thumbnailContainerView;

    private Button saveBtn;

    private LoadingDialog loadingDialog;

    private boolean isFuelTypeEditAble;
    private boolean isUpdateTotalPrice;
    private boolean isUpdateUnitPriceOrGallons;
    private boolean isUpdateGallons;
    private String recentPicPath;

    private String unitPriceWarningEmpty;
    private String unitPriceWarningNumber;
    private String gallonsWarningEmpty;
    private String gallonsWarningNumber;
    private String totalPriceWarning;
    private String totalPriceWarningEmpty;
    private String totalPriceWarningNumber;

    private Map<String, String> paramsMap;

    private IftaCreateContract.Presenter presenter;

    public static IftaCreateFragment newInstance() {

        Bundle args = new Bundle();

        IftaCreateFragment fragment = new IftaCreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 单价输入框监听器
     */
    private TextWatcher unitPriceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!isUpdateUnitPriceOrGallons) {

                isUpdateTotalPrice = true;
                updateTotalPrice();
                isUpdateTotalPrice = false;
            }
        }
    };

    /**
     * 油量输入框监听器
     */
    private TextWatcher purchasedGallonsWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!isUpdateUnitPriceOrGallons) {

                isUpdateTotalPrice = true;
                updateTotalPrice();
                isUpdateTotalPrice = false;
            }
        }
    };

    /**
     * 总价格输入监听器
     */
    private TextWatcher totalPriceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!isUpdateTotalPrice) {

                isUpdateUnitPriceOrGallons = true;
                updateUnitPriceOrGallons();
                isUpdateUnitPriceOrGallons = false;
            }
        }
    };

    @Override
    protected void initVariables() {

        paramsMap = new HashMap<>();
    }

    @Override
    protected void initStatusBar() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ifta_create, null);
        view.findViewById(R.id.input_container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        titleBar = view.findViewById(R.id.title_bar);
        titleBar.setListener(this);

        driverNameInput = view.findViewById(R.id.driver_name_input);
        vehicleIdInput = view.findViewById(R.id.vehicle_id_input);
        purchaseDateInput = view.findViewById(R.id.purchase_date_input);
        stateInput = view.findViewById(R.id.state_input);
        loadStateBtn = view.findViewById(R.id.load_state_btn);
        fuelTypeInput = view.findViewById(R.id.fuel_type_input);
        unitPriceInput = view.findViewById(R.id.unit_price_input);
        purchasedGallonstInput = view.findViewById(R.id.purchased_gallonst_input);
        totalPriceInput = view.findViewById(R.id.total_price_input);

        vehicleIdWarningView = view.findViewById(R.id.vehicle_warning);
        stateWarningView = view.findViewById(R.id.state_warning);
        fuelWarningView = view.findViewById(R.id.fuel_warning);
        unitWarningView = view.findViewById(R.id.unit_warning);
        gallonsWarningView = view.findViewById(R.id.gallons_warning);
        totalPriceWarningView = view.findViewById(R.id.total_price_warning);

        thumbnailContainerView = view.findViewById(R.id.add_receipt);
        thumbnailContainerView.setListener(this);

        fuelTypeArrowView = view.findViewById(R.id.fuel_type_arrow);

        saveBtn = view.findViewById(R.id.save_btn);

        loadingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loading_location_anim);
        LinearInterpolator lin = new LinearInterpolator();
        loadingAnim.setInterpolator(lin);

        loadingDialog = new LoadingDialog(getContext());

        vehicleIdInput.setOnClickListener(this);
        purchaseDateInput.setOnClickListener(this);
        stateInput.setOnClickListener(this);
        loadStateBtn.setOnClickListener(this);
        fuelTypeInput.setOnClickListener(this);

        saveBtn.setOnClickListener(this);

        initInputContent();

        //必须先初始化输入框，再添加监听
        unitPriceInput.addTextChangedListener(unitPriceWatcher);
        purchasedGallonstInput.addTextChangedListener(purchasedGallonsWatcher);
        totalPriceInput.addTextChangedListener(totalPriceWatcher);
        totalPriceInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                isUpdateGallons = hasFocus ? isUpdateGallons : false;
            }
        });
        unitPriceInput.setFilters(INPUT_FILTER_ARRAY);
        purchasedGallonstInput.setFilters(INPUT_FILTER_ARRAY);
        totalPriceInput.setFilters(INPUT_FILTER_ARRAY);

        return view;
    }

    @Override
    public void onLeftBtnClick() {

        getActivity().finish();
    }

    @Override
    public void setPresenter(IftaCreateContract.Presenter presenter) {

        this.presenter = presenter;
    }

    @Override
    public void onAddImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //如果文件夹不存在，创建文件夹
        File file = new File(Constants.CAMERA_PATH);
        if (!file.exists()) {

            file.mkdirs();
        }
        recentPicPath = Constants.CAMERA_PATH + new Date().getTime() + Constants.PIC_SUFFIX;

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            uri = FileProvider.getUriForFile(getContext(), Constants.FILE_PROVIDER, new File(recentPicPath));
        } else {

            uri = Uri.fromFile(new File(recentPicPath));
        }

        //为拍摄的图片指定一个存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_PIC);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.vehicle_id_input:

                JSONObject vehicleObject = new JSONObject();
                vehicleObject.put(WebActivity.BACK_TYPE_KEY, WebActivity.START_FOR_RESULT);
                Intent vehicleIntent = WebActivity.newIntent(getContext(),
                        "select-vehicle.html",
                        getString(R.string.ifta_vehicle_title),
                        JsonUtil.toJSONString(vehicleObject));

                startActivityForResult(vehicleIntent, REQUEST_VEHICLE);
                break;
            case R.id.purchase_date_input:

                new DatePickerDialog.Builder(getContext())
                        .initDate(purchaseDateInput.getText().toString())
                        .listener(new DatePickerDialog.OnDoneClickListener() {
                            @Override
                            public void onDone(String date) {

                                purchaseDateInput.setText(date);
                                paramsMap.put(IftaParamsKey.DATE, date);
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.state_input:

                JSONObject stateObject = new JSONObject();
                stateObject.put(WebActivity.BACK_TYPE_KEY, WebActivity.START_FOR_RESULT);
                final Intent stateIntent = WebActivity.newIntent(getContext(),
                        "select-state.html",
                        getString(R.string.ifta_state_title),
                        JsonUtil.toJSONString(stateObject));

                startActivityForResult(stateIntent, REQUEST_STATE);
                break;
            case R.id.load_state_btn:

                loadStateBtn.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.ic_location_loading));
                loadStateBtn.startAnimation(loadingAnim);

                ThreadUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {

                        Location location = LocationHandler.getInstance().getCurrentLocation();
                        String autoAddress = null;
                        if (location != null) {

                            autoAddress = LocationUtil.getGeoState(location);
                        }

                        final String address = autoAddress;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!TextUtils.isEmpty(address)) {

                                    stateInput.setText(address);

                                    paramsMap.put(IftaParamsKey.STATE, address);
                                    stateWarningView.setVisibility(View.GONE);
                                }

                                loadStateBtn.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.ic_location_gps));
                                loadStateBtn.clearAnimation();
                            }
                        });
                    }
                });
                break;
            case R.id.fuel_type_input:

                if (this.isFuelTypeEditAble) {

                    JSONObject fuelObject = new JSONObject();
                    fuelObject.put(WebActivity.BACK_TYPE_KEY, WebActivity.START_FOR_RESULT);
                    Intent fuelIntent = WebActivity.newIntent(getContext(),
                            "select-fuel-type.html",
                            getString(R.string.ifta_fuel_type_title),
                            JsonUtil.toJSONString(fuelObject));

                    startActivityForResult(fuelIntent, REQUEST_FUEL);
                }

                break;
            case R.id.save_btn:

                if (checkInput()) {

                    loadingDialog.show();
                    this.presenter.createFuel(paramsMap, thumbnailContainerView.getPicPath());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_VEHICLE:

                    String vehicleResult = data.getExtras().getString(UIWebView.EXTRA_RESULT);
                    if (!TextUtils.isEmpty(vehicleResult)) {

                        JSONObject vehicleObj = JsonUtil.parseObject(vehicleResult);
                        vehicleIdInput.setText(JsonUtil.getString(vehicleObj, "code"));

                        paramsMap.put(IftaParamsKey.VEHICLE_ID, JsonUtil.getString(vehicleObj, "id"));
                        vehicleIdWarningView.setVisibility(View.GONE);

                        String fuelType = JsonUtil.getString(vehicleObj, "fuelType");
                        if (!TextUtils.isEmpty(fuelType)) {

                            fuelTypeInput.setText(fuelType);
                            paramsMap.put(IftaParamsKey.FUEL_TYPE, fuelType);
                            fuelWarningView.setVisibility(View.GONE);

                            isFuelTypeEditAble = false;
                            fuelTypeArrowView.setVisibility(View.GONE);
                        } else {

                            fuelTypeArrowView.setVisibility(View.VISIBLE);
                            isFuelTypeEditAble = true;
                        }
                    }
                    break;
                case REQUEST_STATE:

                    String stateResult = data.getExtras().getString(UIWebView.EXTRA_RESULT);
                    if (!TextUtils.isEmpty(stateResult)) {

                        stateInput.setText(stateResult);

                        paramsMap.put(IftaParamsKey.STATE, stateResult);
                        stateWarningView.setVisibility(View.GONE);
                    }
                    break;
                case REQUEST_FUEL:

                    String fuelResult = data.getExtras().getString(UIWebView.EXTRA_RESULT);
                    if (!TextUtils.isEmpty(fuelResult)) {

                        fuelTypeInput.setText(fuelResult);

                        paramsMap.put(IftaParamsKey.FUEL_TYPE, fuelResult);
                        fuelWarningView.setVisibility(View.GONE);
                    }
                    break;
                case REQUEST_PIC:

                    if (!TextUtils.isEmpty(recentPicPath)) {

                        PictureUtil.compress(recentPicPath);
                        thumbnailContainerView.addPic(recentPicPath);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void createFuelSuccess() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingDialog.hide();
                new PromptDialog.Builder(getContext())
                        .listener(new PromptDialog.OnHideListener() {
                            @Override
                            public void onHide() {

                                Intent intent = MainActivity.newIntent(getContext());
                                intent.putExtra(TabMenu.TAB_INDEX, TabMenu.DASHBORAD);
                                startActivity(intent);
                            }
                        })
                        .type(PromptDialog.SUCCESS)
                        .build()
                        .show();
            }
        });
    }

    @Override
    public void createFuelFailed(int code, String msg) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingDialog.hide();
                new PromptDialog.Builder(getContext())
                        .listener(new PromptDialog.OnHideListener() {
                            @Override
                            public void onHide() {

                            }
                        })
                        .type(PromptDialog.FAILURE)
                        .build()
                        .show();
            }
        });
    }

    /**
     * 初始化输入框内容
     */
    private void initInputContent() {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                final Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(user.getDriverId());
                final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(user.getVehicleId());

                final String nowDateStr = TimeUtil.utcToLocal(new Date().getTime(), user.getTimeZone(), TimeUtil.US_FORMAT);

                paramsMap.put(IftaParamsKey.DRIVER_ID, Integer.toString(driver.getId()));
                if (vehicle != null) {

                    paramsMap.put(IftaParamsKey.VEHICLE_ID, Integer.toString(vehicle.getId()));
                    String fuelType = vehicle.getFuelType();
                    if (!TextUtils.isEmpty(fuelType)) {

                        paramsMap.put(IftaParamsKey.FUEL_TYPE, fuelType);

                        isFuelTypeEditAble = false;
                        fuelTypeArrowView.setVisibility(View.GONE);
                    } else {

                        isFuelTypeEditAble = true;
                        fuelTypeArrowView.setVisibility(View.VISIBLE);
                    }
                } else {

                    isFuelTypeEditAble = true;
                    fuelTypeArrowView.setVisibility(View.VISIBLE);
                }
                paramsMap.put(IftaParamsKey.DATE, nowDateStr);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        driverNameInput.setText(driver.getName());
                        if (vehicle != null) {

                            vehicleIdInput.setText(vehicle.getCode());
                            fuelTypeInput.setText(vehicle.getFuelType());
                        }

                        purchaseDateInput.setText(nowDateStr);
                    }
                });
            }
        });

        //模拟点击
        loadStateBtn.performClick();

        unitPriceWarningEmpty = getString(R.string.ifta_unit_price_empty);
        unitPriceWarningNumber = getString(R.string.ifta_unit_price_number);
        gallonsWarningEmpty = getString(R.string.ifta_gallons_empty);
        gallonsWarningNumber = getString(R.string.ifta_gallons_number);
        totalPriceWarningEmpty = getString(R.string.ifta_total_empty);
        totalPriceWarning = getString(R.string.ifta_total_price);
        totalPriceWarningNumber = getString(R.string.ifta_total_number);
    }

    /**
     * 更新单价
     */
    private void updateUnitPriceOrGallons() {

        String unitPriceStr = unitPriceInput.getText().toString();
        String gallonsStr = purchasedGallonstInput.getText().toString();
        String totalPriceStr = totalPriceInput.getText().toString();

        paramsMap.put(IftaParamsKey.TOTAL_PRICE, totalPriceStr);

        if (!TextUtils.isEmpty(totalPriceStr) && totalPriceStr.matches(NUMBER_REGEX)) {

            BigDecimal price = new BigDecimal(Double.parseDouble(totalPriceStr));
            BigDecimal limit = new BigDecimal(1000d);
            if (price.compareTo(limit) != 1) {

                totalPriceWarningView.setVisibility(View.GONE);
            }
        } else {

            return;
        }

        if (!TextUtils.isEmpty(gallonsStr) && gallonsStr.matches(NUMBER_REGEX) && !isUpdateGallons) {

            paramsMap.put(IftaParamsKey.GALLONS, gallonsStr);
            gallonsWarningView.setVisibility(View.GONE);

            try {

                double totalPrice = Double.parseDouble(totalPriceStr);
                double gallons = Double.parseDouble(gallonsStr);
                double unitPrice = Arith.div(totalPrice, gallons);

                unitPrice = ConvertUtil.decimal3Point(unitPrice);

                unitPriceInput.setText(ConvertUtil.doubleTrans(unitPrice));
                paramsMap.put(IftaParamsKey.UNIT_PRICE, ConvertUtil.doubleTrans(unitPrice));
                unitWarningView.setVisibility(View.GONE);
            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {

            if (!TextUtils.isEmpty(unitPriceStr) && unitPriceStr.matches(NUMBER_REGEX)) {

                isUpdateGallons = true;

                paramsMap.put(IftaParamsKey.UNIT_PRICE, unitPriceStr);
                unitWarningView.setVisibility(View.GONE);

                double totalPrice = Double.parseDouble(totalPriceStr);
                double unitPrice = Double.parseDouble(unitPriceStr);
                double gallons = Arith.div(totalPrice, unitPrice);

                gallons = ConvertUtil.decimal3Point(gallons);

                purchasedGallonstInput.setText(ConvertUtil.doubleTrans(gallons));
                paramsMap.put(IftaParamsKey.GALLONS, ConvertUtil.doubleTrans(gallons));
                gallonsWarningView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新总价格
     */
    private void updateTotalPrice() {

        String unitPriceStr = unitPriceInput.getText().toString();
        String gallonsStr = purchasedGallonstInput.getText().toString();

        paramsMap.put(IftaParamsKey.UNIT_PRICE, unitPriceStr);
        paramsMap.put(IftaParamsKey.GALLONS, gallonsStr);

        if (!TextUtils.isEmpty(unitPriceStr) && unitPriceStr.matches(NUMBER_REGEX)) {

            unitWarningView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(gallonsStr) && gallonsStr.matches(NUMBER_REGEX)) {

            gallonsWarningView.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(unitPriceStr) ||
                (!TextUtils.isEmpty(unitPriceStr) && !unitPriceStr.matches(NUMBER_REGEX)) ||
                TextUtils.isEmpty(gallonsStr) ||
                !TextUtils.isEmpty(gallonsStr) && !gallonsStr.matches(NUMBER_REGEX)) {

            return;
        }

        try {

            double unitPrice = Double.parseDouble(unitPriceStr);
            double gallons = Double.parseDouble(gallonsStr);

            double totalPrice = Arith.mul(unitPrice, gallons);
            totalPrice = ConvertUtil.decimal3Point(totalPrice);

            totalPriceInput.setText(ConvertUtil.doubleTrans(totalPrice));
            paramsMap.put(IftaParamsKey.TOTAL_PRICE, ConvertUtil.doubleTrans(totalPrice));

            totalPriceWarningView.setVisibility(View.GONE);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 检验输入是否完整
     *
     * @return 表单是否完整
     */
    private boolean checkInput() {

        boolean result = true;

        String vehicleId = paramsMap.get(IftaParamsKey.VEHICLE_ID);
        String state = paramsMap.get(IftaParamsKey.STATE);
        String fuelType = paramsMap.get(IftaParamsKey.FUEL_TYPE);
        String unitPrice = paramsMap.get(IftaParamsKey.UNIT_PRICE);
        String gallons = paramsMap.get(IftaParamsKey.GALLONS);
        String totalPrice = paramsMap.get(IftaParamsKey.TOTAL_PRICE);

        if (TextUtils.isEmpty(vehicleId)) {

            vehicleIdWarningView.setVisibility(View.VISIBLE);
            result = false;
        }
        if (TextUtils.isEmpty(state)) {

            stateWarningView.setVisibility(View.VISIBLE);
            result = false;
        }
        if (TextUtils.isEmpty(fuelType)) {

            fuelWarningView.setVisibility(View.VISIBLE);
            result = false;
        }
        if (TextUtils.isEmpty(unitPrice)) {

            unitWarningView.setText(unitPriceWarningEmpty);
            unitWarningView.setVisibility(View.VISIBLE);
            result = false;
        } else {

            if (!unitPrice.matches(NUMBER_REGEX)) {

                unitWarningView.setText(unitPriceWarningNumber);
                unitWarningView.setVisibility(View.VISIBLE);
                result = false;
            }
        }
        if (TextUtils.isEmpty(gallons)) {

            gallonsWarningView.setText(gallonsWarningEmpty);
            gallonsWarningView.setVisibility(View.VISIBLE);
            result = false;
        } else {

            if (!gallons.matches(NUMBER_REGEX)) {

                gallonsWarningView.setText(gallonsWarningNumber);
                gallonsWarningView.setVisibility(View.VISIBLE);
                result = false;
            }
        }
        if (!TextUtils.isEmpty(totalPrice)) {

            if (totalPrice.matches(NUMBER_REGEX)) {

                BigDecimal price = new BigDecimal(Double.parseDouble(totalPrice));
                BigDecimal limit = new BigDecimal(1000d);
                if (price.compareTo(limit) == 1) {

                    totalPriceWarningView.setText(totalPriceWarning);
                    totalPriceWarningView.setVisibility(View.VISIBLE);
                    result = false;
                }
            } else {

                totalPriceWarningView.setText(totalPriceWarningNumber);
                totalPriceWarningView.setVisibility(View.VISIBLE);
                result = false;
            }
        } else {

            totalPriceWarningView.setText(totalPriceWarningEmpty);
            totalPriceWarningView.setVisibility(View.VISIBLE);
            result = false;
        }

        return result;
    }
}
