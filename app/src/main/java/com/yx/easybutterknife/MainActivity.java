package com.yx.easybutterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yx.annotations.BindView;
import com.yx.annotations.OnClick;
import com.yx.easybutterknifelib.EasyButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.lv)
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EasyButterKnife.bind(this);
        tv.setText("詹姆斯");
        tv2.setText("浓眉");

    }

    @OnClick({R.id.tv2,R.id.tv})
    public void go(View v){
        switch (v.getId()){
            case R.id.tv:
                Toast.makeText(this, "詹姆斯", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv2:
                Toast.makeText(this, "浓眉", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
