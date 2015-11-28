package com.example.murakoshi.animationcalendarsplash;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by murakoshi on 15/11/04.
 */
public class CustomAdapter extends ArrayAdapter<String> {

  private int mLastAnimated = -1;

  static class ViewHolder {
    TextView labelText;
  }

  private LayoutInflater inflater;

  // コンストラクタ
  public CustomAdapter(Context context,int textViewResourceId, ArrayList<String> labelList) {
    super(context,textViewResourceId, labelList);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder;
    View view = convertView;

    // Viewを再利用している場合は新たにViewを作らない
    if (view == null) {
      inflater =  (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.item_leyout, null);
      TextView label = (TextView)view.findViewById(R.id.tv);
      holder = new ViewHolder();
      holder.labelText = label;
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    // 特定の行のデータを取得
    String str = getItem(position);

    if (!TextUtils.isEmpty(str)) {
      // テキストビューにラベルをセット
      holder.labelText.setText(str);
    }


    if(position > mLastAnimated) {
      // XMLで定義したアニメーションを読み込む
      Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.item_motion);
      // リストアイテムのアニメーションを開始
      view.startAnimation(anim);
      mLastAnimated = position;
    }

    return view;
  }

  public ArrayList<String> getList() {
    ArrayList<String> list = new ArrayList<>();
    int itemCount = getCount();
    for(int i = 0; i < itemCount; i++) {
      list.add(getItem(i));
    }
    return list;
  }
}

