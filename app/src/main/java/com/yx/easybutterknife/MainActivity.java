package com.yx.easybutterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yx.annotations.BindView;
import com.yx.annotations.OnClick;
import com.yx.easybutterknifelib.EasyButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv)
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EasyButterKnife.bind(this);
        tv.setText("詹姆斯");

    }

    @OnClick({R.id.tv2,R.id.tv})
    public void go(View v){
        Toast.makeText(this, "gogogogogogogog", Toast.LENGTH_SHORT).show();
    }
}
