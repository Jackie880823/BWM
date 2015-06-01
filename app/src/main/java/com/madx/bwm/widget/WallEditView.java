package com.madx.bwm.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.madx.bwm.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可以添加不可编辑文字在editText里
 */
public class WallEditView extends EditText implements TextWatcher {

    private static final String TAG = WallEditView.class.getSimpleName();

    /**
     * 控件画笔
     */
    private Paint paint;
    private int viewWidth;
    private int viewHeight;

    //  public static final int TEXT_ALIGN_CENTER            = 0x00000000;
    public static final int TEXT_ALIGN_LEFT = 0x00000001;
    public static final int TEXT_ALIGN_RIGHT = 0x00000010;
    public static final int TEXT_ALIGN_CENTER_VERTICAL = 0x00000100;
    public static final int TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000;
    public static final int TEXT_ALIGN_TOP = 0x00010000;
    public static final int TEXT_ALIGN_BOTTOM = 0x00100000;

    private String oldMemberText = "";
    private String oldGroupText = "";

    /**
     * 文本中轴线X坐标
     */
    private float textCenterX;
    /**
     * 文本baseline线Y坐标
     */
    private float textBaselineY;
    /**
     * 文字的方位
     */
    private int textAlign;
    /**
     * 文字的颜色
     */
    private int textColor;
    /**
     * 文字的大小
     */
    private int textSize = 20;

    private Paint.FontMetrics fm;


    public WallEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initUI(context, attrs);

    }

    public WallEditView(Context context) {
        super(context);


    }

    public WallEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);

    }

    public String getOldGroupText() {
        return oldGroupText;
    }

    public void setOldGroupText(String oldGroupText) {
        this.oldGroupText = oldGroupText;
    }

    public String getOldMemberText() {
        return oldMemberText;
    }

    public void setOldMemberText(String oldMemberText) {
        this.oldMemberText = oldMemberText;
    }

    public void initUI(Context context, AttributeSet attrs) {
        addTextChangedListener(this);
        //        TypedArray mTypedArray;
        //        mTypedArray = context.obtainStyledAttributes(attrs,null);
        //        maxLength=mTypedArray.getInt(R.styleable.WallEditView_max_length, 0);
    }

    public TextChangeListener textChangeListener;

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public interface TextChangeListener {
        int CHANGE_MODE_NORMAL = 0;
        int CHANGE_MODE_DELETE_AT_MEMBER = 1;
        int CHANGE_MODE_DELETE_AT_GROUPS = 2;
        int CHANGE_MODE_DELETE_AT_ALL = 3;
        int CHANGE_MODE_BLACK_CHANGE = 4;

        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s, int change);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(textChangeListener != null) {
            textChangeListener.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        super.onTextChanged(s, start, before, count);
        if(textChangeListener != null) {
            textChangeListener.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.i(TAG, "afterTextChanged& s: " + s + " oldMemberText: " + oldMemberText + "; oldGroupText: " + oldGroupText);

        int change = TextChangeListener.CHANGE_MODE_NORMAL;
        //        if(!isVisible) {
        //            Log.w(TAG, "afterTextChanged& this view not show");
        //            change = TextChangeListener.CHANGE_MODE_BLACK_CHANGE;
        //        } else
        synchronized(this){
            if(!TextUtils.isEmpty(oldMemberText)) {
                SpannableStringBuilder sb = new SpannableStringBuilder(s);
                Pattern p = Pattern.compile(oldMemberText);
                Matcher m = p.matcher(sb.toString());
                if(!m.find() || (m.end() == 0)) {
                    hasAtMember = false;
                    change |= TextChangeListener.CHANGE_MODE_DELETE_AT_MEMBER;
                    oldMemberText = "";
                }
            }
            if(!TextUtils.isEmpty(oldGroupText)) {
                SpannableStringBuilder sb = new SpannableStringBuilder(s);
                Pattern p = Pattern.compile(oldGroupText);
                Matcher m = p.matcher(sb.toString());
                if(!m.find() || (m.end() == 0)) {
                    hasAtGroup = false;
                    change |= TextChangeListener.CHANGE_MODE_DELETE_AT_GROUPS;
                    oldGroupText = "";
                }
            }
        }
        Log.i(TAG, "afterTextChanged& change" + change);
        if(textChangeListener != null) {
            textChangeListener.afterTextChanged(s, change);
        }
    }

    public void addAtDesc(String memberText, String groupText, boolean isVisible) {
        StringBuilder sbLog = new StringBuilder();
        sbLog.append("addAtDesc& oldMemberText: ");
        sbLog.append(oldMemberText);
        sbLog.append("; memberText: ");
        sbLog.append(memberText);
        sbLog.append("; oldGroupText: ");
        sbLog.append(oldGroupText);
        sbLog.append("; groupText: ");
        sbLog.append(groupText);
        sbLog.append("; hasAtMember: ");
        sbLog.append(hasAtMember);
        sbLog.append("; hasAtGroup: ");
        sbLog.append(hasAtGroup);
        Log.i(TAG, sbLog.toString());

        if(!isVisible) {
            Log.w(TAG, "addAtDesc& this view not show");
            if(textChangeListener != null) {
                textChangeListener.afterTextChanged(getText(), TextChangeListener.CHANGE_MODE_BLACK_CHANGE);
            }
            return;
        }

        Editable editable = getText();
        String at = null;
        if(!TextUtils.isEmpty(memberText) && !TextUtils.isEmpty(groupText)) {
            String strDesc = editable.toString();
            int startMember = strDesc.indexOf(oldMemberText);

            if(TextUtils.isEmpty(oldGroupText)) {
                if(startMember + oldMemberText.length() == strDesc.length()) {
                    // @member 后跟着的是@group需要添加 &
                    groupText = "& " + groupText;
                    at = memberText + groupText;
                }
            } else {
                int startGroup = strDesc.indexOf(oldGroupText);
                if(TextUtils.isEmpty(oldMemberText)) {
                    if(startGroup + oldGroupText.length() == strDesc.length()) {
                        // @group 后跟着的是@member需要添加 &
                        memberText = "& " + memberText;
                        at = groupText + memberText;
                    }
                } else {
                    if(startMember + oldMemberText.length() == startGroup) {
                        // @member 后跟着的是@group需要添加 &
                        groupText = "& " + groupText;
                        at = memberText + groupText;
                    } else if(startGroup + oldGroupText.length() == startMember) {
                        // @group 后跟着的是@member需要添加 &
                        memberText = "& " + memberText;
                        at = groupText + memberText;
                    }
                }
            }
        }

        SpannableStringBuilder sb = new SpannableStringBuilder(editable.toString());
        Log.i(TAG, "addAtDesc& 1 sb: " + sb.toString());
        // at member transform about member text
        hasAtMember = setDescSpan(memberText, oldMemberText, hasAtMember, sb);
        oldMemberText = memberText;

        Log.i(TAG, "addAtDesc& 2 sb: " + sb.toString());

        // at group transform about group text
        hasAtGroup = setDescSpan(groupText, oldGroupText, hasAtGroup, sb);
        oldGroupText = groupText;

        if(!TextUtils.isEmpty(at) && hasAtGroup && hasAtMember) {
            setDescSpan(at, at, true, sb);
        }

        Log.i(TAG, "addAtDesc& 3 sb: " + sb.toString());
        this.setText(sb);
        Log.i(TAG, "addAtDesc& oldMemberText: " + oldMemberText + "; oldGroupText: " + oldGroupText);
    }

    private boolean setDescSpan(String desc, String oldDesc, boolean hasAt, SpannableStringBuilder ssbDesc) {
        Log.i(TAG, "addAtDesc& oldDesc: " + oldDesc + "; desc: " + desc + "; hasAt: " + hasAt);

        //        int start = 0;
        //        int end = 0;
        //        if(TextUtils.isEmpty(desc)) {
        //            hasAt = false;
        //            try {
        //                Pattern p = Pattern.compile(oldDesc);
        //                Matcher m = p.matcher(sb.toString());
        //                if(m.find()) {
        //                    start = m.start();
        //                    end = m.end();
        //                    sb.replace(start, end, desc);
        //                }
        //            } catch(Exception e) {
        //                e.printStackTrace();
        //                return false;
        //            }
        //        } else {
        //
        //            if(hasAt) {
        //                try {
        //                    Pattern p = Pattern.compile(oldDesc);
        //                    Matcher m = p.matcher(sb.toString());
        //                    if(m.find()) {
        //                        start = m.start();
        //                        end = m.end();
        //                        sb.replace(start, end, desc);
        //                    }
        //                } catch(Exception e) {
        //                    e.printStackTrace();
        //                    return false;
        //                }
        //            } else {
        //                start = sb.length();
        //                sb.append(desc);
        //                if(start < 0) {
        //                    start = 0;
        //                }
        //                end = sb.length();
        //            }
        //            ImageSpan is = getImageSpanForText(desc);
        //            Log.i(TAG, "addAtDesc& start = " + start + "; end = " + end);
        //            sb.setSpan(is, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //
        //            hasAt = true;
        //        }
        //        this.setText(sb);

        if(!TextUtils.isEmpty(desc)) {
            ImageSpan is = getImageSpanForText(desc);
            SpannableString spanStr = new SpannableString(desc);
            spanStr.setSpan(is, 0, desc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setSpecialText(ssbDesc, oldDesc, spanStr);
            hasAt = true;
        } else {
            hasAt = false;
            try {
                int start;
                int end;
                Pattern p = Pattern.compile(oldDesc);
                Matcher m = p.matcher(ssbDesc.toString());
                if(m.find()) {
                    start = m.start();
                    end = m.end();
                    ssbDesc.replace(start, end, desc);
                }
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        return hasAt;
    }

    /**
     * 设置字符特殊效果
     *
     * @param ssbDesc
     * @param strOld
     * @param spanStr
     */
    private void setSpecialText(SpannableStringBuilder ssbDesc, String strOld, SpannableString spanStr) {
        if(TextUtils.isEmpty(strOld)) {
            ssbDesc.append(spanStr);
        } else {
            try {
                Pattern p = Pattern.compile(strOld);
                Matcher m = p.matcher(ssbDesc.toString());
                if(m.find()) {
                    int start = m.start();
                    int end = m.end();
                    ssbDesc.replace(start, end, spanStr);
                } else {
                    ssbDesc.append(spanStr);
                }
            } catch(Exception e) {
                ssbDesc.append(spanStr);
                e.printStackTrace();
            }
        }
    }

    public String getRelText() {
        String text = getText().toString();
        //        if(!TextUtils.isEmpty(oldMemberText)) {
        //            text = text.replace(oldMemberText, "");
        //        }
        //        if(!TextUtils.isEmpty(oldGroupText)) {
        //            text = text.replace(oldGroupText, "");
        //        }
        return text;
    }

    /**
     * yes or no at member, at anyone value is true;
     */
    private boolean hasAtMember = false;

    private boolean hasAtGroup = false;

    private ImageSpan getImageSpanForText(String desc) {
        TextView tv = createContactTextView(desc);
        tv.setTextColor(getResources().getColor(R.color.tab_color_press4));
        BitmapDrawable bd = convertViewToDrawable(tv);
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        ImageSpan is = new ImageSpan(bd);
        return is;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //        paint = new Paint();
        //        paint.setAntiAlias(true);
        //        paint.setTextAlign(Paint.Align.CENTER);
        //        paint.setColor(Color.BLACK);
        //        paint.setTextSize(20);

        //        canvas.drawText("Some Text", 10, 25, paint);


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        viewWidth = getWidth();
        viewHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    public TextView createContactTextView(String text) {
        TextView tv = new TextView(this.getContext());
        tv.setText(text);
        tv.setTextSize(16);
        Log.i("", "size===" + getResources().getDimensionPixelSize(R.dimen.text_small_size));
        //        tv.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_small_size));
        //        clickableSpan(tv, App.getContextInstance(),qqq);
        return tv;
    }

    public static BitmapDrawable convertViewToDrawable(View view) {
        int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.RGB_565, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(view.getResources(), viewBmp);

    }


    /**
     * 设置前景色
     *
     * @param tv
     */
    private void setForegroundColorSpan(TextView tv) {
        SpannableString spanString = new SpannableString("前景色textview");
        ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * 设置背景色
     *
     * @param tv
     */
    private void setBackgroundColorSpan(TextView tv) {
        SpannableString spanString = new SpannableString("背景色textview");
        BackgroundColorSpan span = new BackgroundColorSpan(Color.YELLOW);
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * 设置粗体样式
     *
     * @param tv
     */
    private void setStyleSpan(TextView tv) {
        SpannableString spanString = new SpannableString("粗体斜体textview");
        StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);
        spanString.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * 设置字体大小
     *
     * @param tv
     */
    private void setRelativeFontSpan(TextView tv) {
        SpannableString spanString = new SpannableString("字体相对大小textview");
        spanString.setSpan(new RelativeSizeSpan(2.5f), 0, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * 设置文本字体
     *
     * @param tv
     */
    private void setTypefaceSpan(TextView tv) {
        SpannableString spanString = new SpannableString("文本字体textview");
        spanString.setSpan(new TypefaceSpan("monospace"), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * 添加URL超链接
     *
     * @param tv
     */
    private void addUrlSpan(TextView tv) {
        SpannableString spanString = new SpannableString("csdntextview");
        URLSpan span = new URLSpan("http://blog.csdn.net/nuptboyzhb");
        spanString.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 在文字中显示图片表情等
     *
     * @param tv
     */
    private void addImageSpan(TextView tv) {
        SpannableString spanString = new SpannableString("文中有图片表情等textview");
        Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        spanString.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * 点击后跳转Activity
     *
     * @param textView
     * @param packageContext
     * @param cls
     */
    private void clickableSpan(TextView textView, final Context packageContext, final Class<?> cls) {
        String text = "显示Activity";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(packageContext, cls);
                WallEditView.this.getContext().startActivity(intent);
            }
            // 表示点击整个text的长度都有效触发这个事件
        }, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 添加下划线
     *
     * @param tv
     */
    private void addUnderLineSpan(TextView tv) {
        SpannableString spanString = new SpannableString("下划线textview");
        UnderlineSpan span = new UnderlineSpan();
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * 添加删除线样式
     *
     * @param tv
     */
    private void addStrikeSpan(TextView tv) {
        SpannableString spanString = new SpannableString("删除线textview");
        StrikethroughSpan span = new StrikethroughSpan();
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }


}
