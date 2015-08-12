package com.example.qianfangdemo.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.qianfangdemo.Utils.DBHelper;
import com.example.qianfangdemo.entity.Province;
import com.example.qianfangdemo.entity.ProvinceAdaper;

import java.util.List;

import qfpay.wxshop.R;

/**
 * Created by Yang on 15/5/7.
 */
public class ProvinceListAct extends AppCompatActivity{

    private ListView listView;
    private List<Province> provinces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        SQLiteDatabase database = DBHelper.getDatabase();
        provinces = DBHelper.getProvinces(database);

        listView = (ListView) findViewById(R.id.act_list);
        listView.setAdapter(new ProvinceAdaper(provinces, this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProvinceListAct.this, CityListAct.class);
                intent.putExtra("PROV_CODE", provinces.get(position).getProv_code());
                intent.putExtra("PROV_NAME", provinces.get(position).getProv_name());
                startActivityForResult(intent, 0x00);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0x00) {
            setResult(RESULT_OK, data);
            finish();
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
