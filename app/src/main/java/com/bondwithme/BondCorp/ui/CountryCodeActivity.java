package com.bondwithme.BondCorp.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.LetterBaseListAdapter;
import com.bondwithme.BondCorp.widget.LetterListView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 待优化
 * TODO
 */
public class CountryCodeActivity extends BaseActivity {
    String[] dataArray = new String[]{};
    public static final String COUNTRY = "country";
    public static final String CODE = "code";

    @Override
    public int getLayout() {
        return R.layout.activity_countrycode;
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_login);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        /**
         * begin QK
         */
        tvTitle.setText(getResources().getString(R.string.title_country_code));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    TestAdapter ataaa2;
    Boolean isSearch = false;

    @Override
    public void initView() {


        dataArray = getResources().getStringArray(R.array.country_code);
        Arrays.sort(dataArray, new ComparatorPinYin());
        final LetterListView letterListView = (LetterListView) findViewById(R.id.letterListView);

        final EditText etSearch = (EditText) findViewById(R.id.et_search);

        final TestAdapter ataaa = new TestAdapter();
        letterListView.setAdapter(ataaa);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(etSearch.getText())) {
                    isSearch = false;
                    letterListView.setAdapter(ataaa);
                } else {
                    List<NameValuePair> dataList = new ArrayList<NameValuePair>();

                    for (int i = 0; i < dataArray.length; i++) {
                        if (dataArray[i].toLowerCase().startsWith(etSearch.getText().toString().toLowerCase())) {
                            NameValuePair pair = new BasicNameValuePair(String.valueOf(i), dataArray[i]);
                            dataList.add(pair);
                        }
                    }

                    ataaa2 = new TestAdapter(dataList);
                    letterListView.setAdapter(ataaa2);
                    isSearch = true;
                }


            }
        });

/*        //创建一个ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,dataArray);
        //获取AutoCompleteTextView对象
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.et_search);
        //将AutoCompleteTextView与ArrayAdapter进行绑定
        autoComplete.setAdapter(adapter);
        //设置AutoCompleteTextView输入1个字符就进行提示
        autoComplete.setThreshold(1);

        autoComplete.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView)view).getText().toString();
                String code = text.substring(text.lastIndexOf(" ")+1,text.length());
                Log.i("", "2----"+text.substring(text.lastIndexOf(" ")+1,text.length()));
                String reg = "[a-zA-Z]";
                boolean isCract = code.matches(reg);
                if(!isCract)
                {
                    Intent intent = new Intent();
                    intent.putExtra("code", code);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });*/

        letterListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (isSearch) {
                    if (ataaa2.getItemViewType(arg2) == 1) {
                        String text = ataaa2.list.get(arg2).getValue().trim();
                        String code = text.substring(text.lastIndexOf(" ") + 1, text.length());
                        String reg = "[a-zA-Z]";
                        boolean isCract = code.matches(reg);
                        if (!isCract) {
                            Intent intent = new Intent();
                            intent.putExtra(COUNTRY,text.substring(0,text.lastIndexOf(" ") + 1));
                            intent.putExtra(CODE, code);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                } else {
                    if (ataaa.getItemViewType(arg2) == 1) {
                        String text = ataaa.list.get(arg2).getValue().trim();
                        String code = text.substring(text.lastIndexOf(" ") + 1, text.length());
                        String reg = "[a-zA-Z]";
                        boolean isCract = code.matches(reg);
                        if (!isCract) {
                            Intent intent = new Intent();
                            intent.putExtra(COUNTRY, text.substring(0,text.lastIndexOf(" ") + 1));
                            intent.putExtra(CODE, code);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }

            }

        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class TestAdapter extends LetterBaseListAdapter<NameValuePair> {

        private static final String LETTER_KEY = "letter";
        LayoutInflater mInflater;

        public TestAdapter() {
            super();
            mInflater = LayoutInflater.from(CountryCodeActivity.this);
            List<NameValuePair> dataList = new ArrayList<NameValuePair>();
            for (int i = 0; i < dataArray.length; i++) {
                NameValuePair pair = new BasicNameValuePair(String.valueOf(i), dataArray[i]);
                dataList.add(pair);
            }
            setContainerList(dataList);
        }

        TestAdapter(List<NameValuePair> arrayList) {
            setContainerList(arrayList);
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public String getItemString(NameValuePair t) {
            return t.getValue();
        }

        @Override
        public NameValuePair create(char letter) {
            return new BasicNameValuePair(LETTER_KEY, String.valueOf(letter));
        }

        @Override
        public boolean isLetter(NameValuePair t) {
            return t.getName().equals(LETTER_KEY);
        }

        @Override
        public View getLetterView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = new TextView(CountryCodeActivity.this);
                ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
                convertView.setBackgroundColor(getResources().getColor(android.R.color.white));
            }

            ((TextView) convertView).setPadding(getResources().getDimensionPixelSize(R.dimen._6dp),
                    0, 0, 0);
            ((TextView) convertView).setTextSize(getResources().getDimensionPixelSize(R.dimen._18dp));
            ((TextView) convertView).setText(list.get(position).getValue());

            return convertView;
        }


        // begin
        // add by qk

        /**
         * 更改电话号码的布局
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getContainerView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(CountryCodeActivity.this).inflate(R.layout.country_list_item, null);
                viewHolder.country_name_tv = (TextView) convertView.findViewById(R.id.country_name_tv);
                viewHolder.country_code_tv = (TextView) convertView.findViewById(R.id.country_code_tv);
//                convertView = new TextView(CountryCodeActivity.this);
//                ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String countryNameAndCode = list.get(position).getValue().trim();
            String countryName = "";
            String countryCode = "";
            if (null != countryNameAndCode && countryNameAndCode.indexOf(" ") != -1) {
                countryName = countryNameAndCode.substring(0, countryNameAndCode.lastIndexOf(" "));
                countryCode = "+" + countryNameAndCode.substring(countryNameAndCode.lastIndexOf(" "));
            }
            viewHolder.country_name_tv.setText(countryName.trim());
            viewHolder.country_code_tv.setText(countryCode.trim());
//            ((TextView) convertView).setTextSize(22);
//            ((TextView) convertView).setPadding(6, 16, 0, 16);
//            ((TextView) convertView).setText(list.get(position).getValue());

            return convertView;
        }

        class ViewHolder {
            TextView country_name_tv;
            TextView country_code_tv;
        }
        //end
    }

    public class PinyinComparator implements Comparator<Object> {
        /**
         * 比较两个字符串
         */
        public int compare(Object o1, Object o2) {
            String[] name1 = (String[]) o1;
            String[] name2 = (String[]) o2;
            String str1 = getPingYin(name1[0]);
            String str2 = getPingYin(name2[0]);
            int flag = str1.compareTo(str2);
            return flag;
        }

        /**
         * 将字符串中的中文转化为拼音,其他字符不变
         *
         * @param inputString
         * @return
         */
        public String getPingYin(String inputString) {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);

            char[] input = inputString.trim().toCharArray();// 把字符串转化成字符数组
            String output = "";

            try {
                for (int i = 0; i < input.length; i++) {
                    // \\u4E00是unicode编码，判断是不是中文
                    if (java.lang.Character.toString(input[i]).matches(
                            "[\\u4E00-\\u9FA5]+")) {
                        // 将汉语拼音的全拼存到temp数组
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                input[i], format);
                        // 取拼音的第一个读音
                        output += temp[0];
                    }
                    // 大写字母转化成小写字母
                    else if (input[i] > 'A' && input[i] < 'Z') {
                        output += java.lang.Character.toString(input[i]);
                        output = output.toLowerCase();
                    }
                    output += java.lang.Character.toString(input[i]);
                }
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return output;
        }
    }

    static class ComparatorPinYin implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return ToPinYinString(o1).compareTo(ToPinYinString(o2));
        }

        private String ToPinYinString(String str) {

            StringBuilder sb = new StringBuilder();
            String[] arr = null;

            for (int i = 0; i < str.length(); i++) {
                arr = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
                if (arr != null && arr.length > 0) {
                    for (String string : arr) {
                        sb.append(string);
                    }
                }
            }

            return sb.toString();
        }
    }
}