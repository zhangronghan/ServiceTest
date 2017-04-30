package com.example.administrator.servicetest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button btnAdd;
    private Button btnUpdate;
    private Button btnDel;
    private EditText edtID;
    private EditText edtUser;
    private EditText edtPass;
    private TextView tvContent;
    private Handler mHandler;
    private OkHttpClient client;
    private Message msg;
    private Gson gson;
    private String path="http://192.168.132.54:8080/MyAndroidService/MyService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();
        init();
        initHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                findAll("findAll");

            }
        }).start();

    }

    private void initHandler() {
        mHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1001:
                        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    break ;

                    case 1002:
                        Toast.makeText(MainActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                        break ;

                    case 1003:
                        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        break ;

                    case 1004:
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        break ;

                    default:
                    break;
                }
                return true;
            }
        });

    }

    private void initViews() {
        btnAdd=(Button) findViewById(R.id.btn_add);
        btnDel= (Button) findViewById(R.id.btn_del);
        edtID= (EditText) findViewById(R.id.edt_id);
        edtUser= (EditText) findViewById(R.id.edt_username);
        edtPass= (EditText) findViewById(R.id.edt_password);
        btnUpdate= (Button) findViewById(R.id.btn_update);
        tvContent= (TextView) findViewById(R.id.tv_content);

        client=new OkHttpClient();
        msg=new Message();
        gson=new Gson();
    }

    private void init() {

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String id=edtID.getText().toString();
                        String username=edtUser.getText().toString();
                        String password=edtPass.getText().toString();
                        String operate="insert";
                        final String result=userData(id,username,password,operate);
                        Log.e("MainActivity","callback:"+result);

                        goToMain(result);

                        if(result!=null){
                            msg.what=1001;
                            mHandler.sendMessage(msg);
                        }
                        
                    }
                }).start();

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String id=edtID.getText().toString();
                        String username=edtUser.getText().toString();
                        String password=edtPass.getText().toString();
                        String operate="update";

                        final String result=userData(id,username,password,operate);
                        Log.e("MainActivity","callback:"+result);

                        goToMain(result);

                        if(result!=null){
                            msg.what=1003;
                            mHandler.sendMessage(msg);
                        }
                    }
                }).start();

            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               deleteOperate();

            }
        });


    }

    private void deleteOperate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String id=edtID.getText().toString();
                    String operate="delete";
                    RequestBody requestBody=new FormBody.Builder()
                            .add("operate",operate)
                            .add("id",id)
                            .build();

                    Request request=new Request.Builder()
                            .url(path)
                            .post(requestBody)
                            .build();

                    Response response=client.newCall(request).execute();
                    String result=response.body().string();

                    goToMain(result);

                    if(response != null){
                        msg.what=1004;
                        mHandler.sendMessage(msg);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void findAll(String operateName) {
        try{
            String operate=operateName;
            RequestBody requestBody=new FormBody.Builder()
                    .add("operate",operate)
                    .build();

            Request request=new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();

            Response response=client.newCall(request).execute();
            final String content=response.body().string().trim();
            Log.e("MainActivity","callback:"+content.toString());

            goToMain(content);

            if(content!=null){
                msg.what=1002;
                mHandler.sendMessage(msg);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void goToMain(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(content.toString());
            }
        });

    }


    private String userData(String id, String username, String password,String operate) {
        try{
            RequestBody requestBody=new FormBody.Builder()
                    .add("id",id)
                    .add("username",username)
                    .add("password",password)
                    .add("operate",operate)
                    .build();

            Request request=new Request.Builder()
                    .url(path)
                    .post(requestBody)
                    .build();
            Response response=client.newCall(request).execute();
            String content=response.body().string();
            return content;

        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
