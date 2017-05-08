package feifu.com.testview.gridpswview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import feifu.com.testview.R;

/**
 * Created by Administrator on 2017/5/5.
 */

public class GridPswView extends LinearLayout implements IGridPswView {
    private static final String DEFAULT_TRANSFORMATION = "●";
    private EditText mIndexView;
    private LinearLayout mContainer;
    private static final String TAG = "GridPasswordView";
    private int currentIndex = 0;
    private int mMaxLength;
    private String oldText;
    private int mBorderColor;
    private int mTextColor;
    private int mBackgroundColor;
    private String mPswTransformation;
    private float mBroderWidth;
    private float mTextSize;
    private String[] values;
    private View[] viewArr;
    private OnGridPswViewListener mOnGridPswViewListener;
    private boolean mIsPswVisible;

    public GridPswView(Context context) {
        this(context, null);
    }

    public GridPswView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridPswView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView();
    }

    /**
     * 获取属性
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GridPswView);
        if (typedArray != null) {
            mBorderColor = typedArray.getColor(R.styleable.GridPswView_borderColor, Color.BLUE);
            mTextColor = typedArray.getColor(R.styleable.GridPswView_textColor, Color.GRAY);
            mBackgroundColor = typedArray.getColor(R.styleable.GridPswView_backgroundColor, Color
                    .LTGRAY);
            mIsPswVisible = typedArray.getBoolean(R.styleable.GridPswView_isPswVisible, false);
            mPswTransformation = typedArray.getString(R.styleable
                    .GridPswView_passwordTransformation);
            if (TextUtils.isEmpty(mPswTransformation)) {
                mPswTransformation = DEFAULT_TRANSFORMATION;
            }
            mMaxLength = typedArray.getInt(R.styleable.GridPswView_length, 6);
            mBroderWidth = typedArray.getDimension(R.styleable.GridPswView_borderWidth, 3);
            mTextSize = typedArray.getDimension(R.styleable.GridPswView_textSize, 16);
        }
    }


    private void initView() {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT));
        values = new String[mMaxLength];
        viewArr = new View[mMaxLength];
        setOrientation(VERTICAL);
        setBackgroundColor(mBackgroundColor);
        mContainer = (LinearLayout) View.inflate(getContext(), R.layout.inputview, null);
//        mContainer.setBackground(getBackgroundDrawable());
//        mIndexView = (EditText) mContainer.findViewById(R.id.indexView);
        mIndexView = getIndexView();
        mIndexView.setTextSize(mTextSize);
        mIndexView.setTextColor(mTextColor);
        mContainer.addView(getBorderVertical());
        mContainer.addView(mIndexView);
        mContainer.addView(getBorderVertical());
        viewArr[0] = mIndexView;
        for (int i = 0; i < mMaxLength - 1; i++) {
            TextView child = new TextView(getContext());
            child.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams
                    .MATCH_PARENT, 1));
            child.setGravity(Gravity.CENTER);
            child.setTextColor(mTextColor);
            child.setTextSize(mTextSize);
            mContainer.addView(child);
            mContainer.addView(getBorderVertical());
            viewArr[i + 1] = child;
        }
        mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, 0, 1));
        addView(getBorderHorizontal());
        addView(mContainer);
        addView(getBorderHorizontal());
        mIndexView.addTextChangedListener(mIndextViewWatcher);
        mContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GridPswView.this.onClick(v);
                forceInputViewGetFocus();
            }
        });
    }

    /**
     * indexview
     *
     * @return
     */
    private EditText getIndexView() {
        EditText editText = new EditText(getContext());
        editText.setBackground(null);
        editText.setCursorVisible(false);
        editText.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setGravity(Gravity.CENTER);
        editText.setBackgroundColor(Color.RED);
        return editText;
    }

    /**
     * 获取竖直border
     *
     * @return
     */
    private View getBorderVertical() {
        View view = new View(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams((int) mBroderWidth, ViewGroup
                .LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(mBorderColor);
        return view;
    }

    /**
     * 获取水平border
     *
     * @return
     */
    private View getBorderHorizontal() {
        View view = new View(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mBroderWidth));
        view.setBackgroundColor(mBorderColor);
        return view;
    }

    /**
     * 强制获取焦点，并弹出软键盘
     */
    private void forceInputViewGetFocus() {
        mIndexView.setFocusable(true);
        mIndexView.setFocusableInTouchMode(true);
        mIndexView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.showSoftInput(mIndexView, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 监听indexview的内容变化
     */
    private TextWatcher mIndextViewWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            oldText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            GridPswView.this.onTextChanged(s, start, before, count);
            if (s.length() == mMaxLength) {
                onComplete(s.toString());
            }
            Log.e(TAG, "count=" + count + ",start=" + start + ",before=" + before + ",s=" + s);
            //输入内容
            if (count == 1) {
                //文本到达最大长度，不再接受输入，要保证edittext不变
                if (currentIndex >= mMaxLength) {
                    mIndexView.removeTextChangedListener(this);
                    mIndexView.setText(mIsPswVisible ? (s.charAt(0) + "") : mPswTransformation);
                    mIndexView.setSelection(mIndexView.getText().length());//设置光标到最后，保证输入文本顺序
                    mIndexView.addTextChangedListener(this);//设置文本后再次添加监听
                    return;
                }
                if (currentIndex == 0) {
                    //保存内容
                    mIndexView.removeTextChangedListener(this);
                    mIndexView.setText(mIsPswVisible ? s.toString() : mPswTransformation);
                    mIndexView.addTextChangedListener(this);//设置文本后再次添加监听
                    values[currentIndex] = s.toString();
                    currentIndex++;
                } else {
                    String input = s.charAt(s.length() - 1) + "";
                    values[currentIndex] = input;
                    TextView child = (TextView) viewArr[currentIndex++];
                    child.setText(mIsPswVisible ? input : mPswTransformation);
                    //需要及时移除监听器，否则会形成死循环
                    mIndexView.removeTextChangedListener(this);
                    mIndexView.setText(mIsPswVisible ? values[0] : mPswTransformation);
                    mIndexView.setSelection(mIndexView.getText().length());//设置光标到最后，保证输入文本顺序
                    mIndexView.addTextChangedListener(this);//设置文本后再次添加监听
                }
            } else if (count == 0) {
                Log.e(TAG, "currentIndex=" + currentIndex);
                if (currentIndex <= 0) {
                    return;
                }
                //删除内容
                if (currentIndex == 1) {
                    currentIndex--;
                    values[currentIndex] = "";
                } else {
                    TextView child = (TextView) viewArr[currentIndex - 1];
                    currentIndex--;
                    values[currentIndex] = "";
                    child.setText("");
                    mIndexView.removeTextChangedListener(this);
                    mIndexView.setText(mIsPswVisible ? oldText : mPswTransformation);
                    mIndexView.addTextChangedListener(this);//设置文本后再次添加监听
                    mIndexView.setSelection(mIndexView.getText().length());//设置光标位置到最后，否则无法删除
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 必须重写此方法
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
/*    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 50;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

//        //Measure Width
//        if (widthMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            width = Math.min(desiredWidth, widthSize);
//        } else {
//            //Be whatever you want
//            width = desiredWidth;
//        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(widthSize, height);
    }*/

    /**
     * 生成背景
     *
     * @return
     */
    private ShapeDrawable getBackgroundDrawable() {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        shapeDrawable.getPaint().setColor(mBorderColor);
        shapeDrawable.draw(new Canvas());
        return shapeDrawable;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mOnGridPswViewListener != null) {
            mOnGridPswViewListener.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnGridPswViewListener != null) {
            mOnGridPswViewListener.onClick(v);
        }
    }

    @Override
    public void onComplete(String s) {
        if (mOnGridPswViewListener != null) {
            mOnGridPswViewListener.onComplete(s);
        }
    }

    public int getLength() {
        return mMaxLength;
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
        }
        return sb.toString();
    }

    public void setOnGridPswViewListener(OnGridPswViewListener onGridPswViewListener) {
        this.mOnGridPswViewListener = onGridPswViewListener;
    }


}
