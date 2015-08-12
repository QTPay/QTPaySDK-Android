package com.example.qianfangdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.entity.BankInfo;

import java.util.List;

import qfpay.wxshop.R;

/**
 * Created by Yang on 15/5/7.
 */
public class BankListAct extends AppCompatActivity{

    private ListView listView;
    private List<BankInfo> banknames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        banknames = App.bankInfos;

        listView = (ListView) findViewById(R.id.act_list);
        listView.setAdapter(new BankAdapter(banknames, this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("BANKINFO", banknames.get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }



    public class BankAdapter extends BaseAdapter{

        private List<BankInfo> bankInfos;
        private Context context ;
        private LayoutInflater inflater;
        public BankAdapter(List<BankInfo> bankInfos , Context context) {
            this.bankInfos = bankInfos;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if(bankInfos==null){
                return 0;
            }
            return bankInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BankInfo bank = bankInfos.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_act_list, null);
                holder.textView = (TextView) convertView.findViewById(R.id.act_list_item);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.textView.setText(bank.getHeadbankname());

            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
