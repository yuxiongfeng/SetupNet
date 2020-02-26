package com.proton.setupnet.veiw;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proton.setupnet.R;
import com.proton.setupnet.utils.DensityUtils;


/**
 * Created by yuxiongfeng on 2018-3-5.
 */

public class PopSpinnerView extends RelativeLayout {

    /**
     * 自定义属性的参数
     *
     * @param context
     */
    private String textName;//未选择时提示文字
    private int itemWidth;//空间宽度
    private ImageView mIv_arrow;
    private TextView mTv_content;
    private TextView txt_divider;
    private RelativeLayout rlRoot;
    private ListView lv;
    private int height;
    private int listSize;
    private int curIndex = -1;
    private NameFilterSpinner nameFilterSpinner;
    private PopupWindow popupWindow;


    public PopSpinnerView(Context context) {
        super(context);
        initView(context);
    }

    public PopSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initText(attrs);
    }

    public PopSpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initText(attrs);
    }

    public void init(int listSize, int itemWidth, NameFilterSpinner nameFilter) {
        this.itemWidth = itemWidth;
        this.listSize = listSize;
        this.nameFilterSpinner = nameFilter;
    }


    private void initView(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.pop_spinner_view, this);
        mIv_arrow = this.findViewById(R.id.iv_arrow);
        mTv_content = this.findViewById(R.id.tv_content);
        txt_divider = this.findViewById(R.id.txt_divider);
        rlRoot = this.findViewById(R.id.rl_root);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewPop = LayoutInflater.from(context).inflate(R.layout.pop_layout, null);
                initListView(context, viewPop);
                initPopWindow(viewPop, context);
                if (listner != null) {
                    listner.onHomeTabClick(listSize);
                }
                arrowRotation(0, -180f);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chageTextBackground(R.drawable.shape_button_gray_nobottom);
                        txt_divider.setVisibility(VISIBLE);
                    }
                }, 200);
            }
        });
    }

    public void setTextContent(String text) {
        if (mTv_content != null) {
            mTv_content.setText(text);
        }
    }

    private void initText(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PopSpinnerView);
        textName = typedArray.getString(R.styleable.PopSpinnerView_textName);
        if (!TextUtils.isEmpty(textName)) {
            mTv_content.setText(textName);
        }
        itemWidth = typedArray.getInt(R.styleable.PopSpinnerView_popWidth, 0);
        typedArray.recycle();
    }

    private void initListView(Context context, View viewPop) {
        lv = viewPop.findViewById(R.id.lv);
        lv.setAdapter(new MyAdapter(context));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curIndex = position;
                mTv_content.setText(nameFilterSpinner.filter(position));
                popupWindow.dismiss();
                nameFilterSpinner.onItemClickListner(position);
            }
        });
    }

    private void initPopWindow(View view, Context context) {
        height = listSize >= 8 ? 387 : (listSize * 80 - 50) < 80 ? 80 : (listSize * 80 - 75);
        popupWindow = new PopupWindow(view, itemWidth, DensityUtils.dp2px(context, height));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(mTv_content);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                txt_divider.setVisibility(GONE);
                chageTextBackground(R.drawable.shape_button_gray_normal);
                arrowRotation(-180f, 0);
            }
        });
    }


    private void arrowRotation(float angleStart, float angleEnd) {
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(mIv_arrow, "rotation", angleStart, angleEnd);
        rotationAnimator.setDuration(500);
        rotationAnimator.start();
    }

    private void chageTextBackground(int res) {
        int paddingLeft = mTv_content.getPaddingLeft();
        rlRoot.setBackgroundResource(res);
        mTv_content.setPadding(paddingLeft, 0, 0, 0);
    }

    /**
     * 获取当前选择的item
     */
    public int getSelectIndex() {
        return curIndex;
    }

    /**
     * 内容回调接口
     */
    public interface NameFilterSpinner {
        String filter(int position);

        void onItemClickListner(int position);
    }

    public OnHomeTabClickListner listner;

    public void setOnHomeClickListner(OnHomeTabClickListner listner) {
        this.listner = listner;
    }

    public interface OnHomeTabClickListner {
        void onHomeTabClick(int size);
    }


    class MyAdapter extends BaseAdapter {
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return listSize;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list_pop_spinner, null);
                holder.tv = convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(nameFilterSpinner.filter(position));
            return convertView;
        }
    }

    class ViewHolder {
        TextView tv;
    }

}
