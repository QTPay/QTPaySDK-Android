package com.example.qianfangdemo.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.entity.SubBank;

import java.util.ArrayList;
import java.util.List;

import qfpay.wxshop.R;

/**
 * Created by Yang on 15/5/7.
 */
public class SubBankListAct extends AppCompatActivity{

    private ListView listView;
    private List<SubBank> banknames;
    private SubBankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        banknames = new ArrayList<>();
        banknames.addAll(App.subBanks);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listView = (ListView) findViewById(R.id.act_list);
        adapter = new SubBankAdapter(banknames, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("SUBBANKINFO", banknames.get(position).getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }



    public class SubBankAdapter extends BaseAdapter{

        private List<SubBank> bankInfos;
        private Context context ;
        private LayoutInflater inflater;
        public SubBankAdapter(List<SubBank> bankInfos , Context context) {
            this.bankInfos = bankInfos;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if(bankInfos == null){
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
            SubBank bank = bankInfos.get(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_act_list, null);
                holder.textView = (TextView) convertView.findViewById(R.id.act_list_item);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.textView.setText(bank.getName());

            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sublist, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) ((menu.findItem(R.id.action_search)).getActionView());
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                banknames.clear();
                for(SubBank bank :App.subBanks){
                    if(bank.getName().contains(newText)){
                        banknames.add(bank);
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
