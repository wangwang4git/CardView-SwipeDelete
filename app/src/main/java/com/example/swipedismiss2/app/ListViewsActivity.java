package com.example.swipedismiss2.app;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewsActivity extends Activity {

    private ArrayList<String> mDataList;
    private ListView mListView;
    private MyListViewAdpter mListViewAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviews);

        mDataList = new ArrayList<String>();
        for (int i = 0; i < 20; ++i) {
            mDataList.add("Item " + i);
        }
        mListView = (ListView) findViewById(R.id.listview);
        mListViewAdpter = new MyListViewAdpter();
        mListView.setAdapter(mListViewAdpter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> paramAdapterView,
                                    View paramView, int paramInt, long paramLong) {
                // TODO Auto-generated method stub
                Toast.makeText(ListViewsActivity.this, mDataList.get(paramInt),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class MyListViewAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDataList.size();
        }

        @Override
        public String getItem(int paramInt) {
            // TODO Auto-generated method stub
            return mDataList.get(paramInt);
        }

        @Override
        public long getItemId(int paramInt) {
            // TODO Auto-generated method stub
            return paramInt;
        }

        @Override
        public View getView(int paramInt, View paramView,
                            ViewGroup paramViewGroup) {
            // TODO Auto-generated method stub
            MyViewHolder myViewHolder;

            if (paramView == null) {
                paramView = getLayoutInflater().inflate(R.layout.swipe2_item,
                        paramViewGroup, false);
                myViewHolder = new MyViewHolder();
                myViewHolder.mTextViewDemo = (TextView) paramView
                        .findViewById(R.id.content);
                myViewHolder.mDeleteDemo = (Button) paramView
                        .findViewById(R.id.delete);
                paramView.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) paramView.getTag();
            }

            myViewHolder.mTextViewDemo.setText(getItem(paramInt));

            return paramView;
        }

    }

    private class MyViewHolder {
        public TextView mTextViewDemo;
        public Button mDeleteDemo;
    }

}
