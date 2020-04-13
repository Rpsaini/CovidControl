package apps.envision.musicvibes;

import androidx.appcompat.app.AppCompatActivity;
import communication.CallBack;
import communication.ServerHandler;
import apps.envision.musicvibes.R;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MobileNumberActivity extends AppCompatActivity {
    private TextView mobil_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);
        ArrayList<TextView> keyBoardArray=new ArrayList<>();
        keyBoardArray.add((TextView)findViewById(R.id.one));
        keyBoardArray.add((TextView)findViewById(R.id.two));
        keyBoardArray.add((TextView)findViewById(R.id.three));
        keyBoardArray.add((TextView)findViewById(R.id.four));
        keyBoardArray.add((TextView)findViewById(R.id.five));
        keyBoardArray.add((TextView)findViewById(R.id.six));
        keyBoardArray.add((TextView)findViewById(R.id.seven));
        keyBoardArray.add((TextView)findViewById(R.id.eight));
        keyBoardArray.add((TextView)findViewById(R.id.nine));
        keyBoardArray.add((TextView)findViewById(R.id.zero));

        final ArrayList<String> numberlist=new ArrayList<>();

        mobil_number=findViewById(R.id.mobilenumber_text);
        for(int x=0;x<keyBoardArray.size();x++)
        {
            keyBoardArray.get(x).setTag(x);
            keyBoardArray.get(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    numberlist.add(((TextView)v).getText().toString());
                    mobil_number.setText("");
                    for(int x=0;x<numberlist.size();x++)
                    {
                        mobil_number.setText(mobil_number.getText()+""+numberlist.get(x));
                    }

                }
            });
        }

        mobil_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(numberlist.size()>0) {
                    numberlist.remove(numberlist.size() - 1);
                    mobil_number.setText("");
                    for(int x=0;x<numberlist.size();x++)
                    {

                        mobil_number.setText(mobil_number.getText()+""+numberlist.get(x));
                    }
                }
            }
        });



        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String,String> m=new HashMap<>();
                m.put("mobile",mobil_number.getText().toString());
                m.put("epass",getIntent().getStringExtra("epass"));

                new ServerHandler().sendToServer(MobileNumberActivity.this, "login", m, 0, new CallBack() {
                    @Override
                    public void getRespone(String dta, ArrayList<Object> respons) {

                        try {


                            JSONObject obj=new JSONObject(dta);

                            if(obj.getString("status").equalsIgnoreCase("true"))
                            {


                                Intent i=new Intent(MobileNumberActivity.this, VerifyOtpActivity.class);
                                i.putExtra("mobile",mobil_number.getText().toString());
                                i.putExtra("otp",obj.getString("otp"));
                                i.putExtra("epass",getIntent().getStringExtra("epass"));
                                startActivity(i);
                                finish();

                            }
                            else
                            {
                                Toast.makeText(MobileNumberActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }




                    }
                });
            }
        });

    }





}

