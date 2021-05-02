package com.prasadthegreat.paymentintegration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button mPay;
    EditText mAmount,mUpid;
    Uri uri;
    TextView mPaymentStatus;

    public static final  String PAYTM_PACKAGE_NAME="net.one97.paytm";
    public static String  upi_id,amount,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaymentStatus=(TextView)findViewById(R.id.payment_status);
        mAmount=(EditText)findViewById(R.id.edit_ammount);
        mUpid=(EditText)findViewById(R.id.edit_upi);
        mPay=(Button)findViewById(R.id.btn_pay);

        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upi_id=mUpid.getText().toString().trim();
                amount=mAmount.getText().toString().trim();

                if (upi_id.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please,Enter Your Upi id",Toast.LENGTH_SHORT).show();
                }if(amount.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please,Enter Your Upi id",Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(getApplicationContext(),amount,Toast.LENGTH_SHORT).show();
                    payment_method(upi_id,amount);
                }

            }
        });
    }

    private void payment_method(String upi_id, String amount)
    {
            uri=GetPaytmUri(amount,upi_id);
        PayWithPayTm(PAYTM_PACKAGE_NAME);
    }

    private   static  Uri GetPaytmUri(String amount,String upiId)
    {
        return  new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

    }

    private void PayWithPayTm(String packageName)
    {
        if (isAppInstalled(this,packageName))
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(packageName);
            startActivityForResult(intent,0);
        }else
        {
            Toast.makeText(getApplicationContext(),"Paytm is not installed Please install and try again",Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isAppInstalled(Context context,String packageName)
    {
        try
        {
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        }catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
        }
        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(MainActivity.this, "Transaction successful." + data.getStringExtra("ApprovalRefNo"), Toast.LENGTH_SHORT).show();
            mPaymentStatus.setText("Transaction successful of ₹" + amount);
            mPaymentStatus.setTextColor(Color.GREEN);

        } else {
            Toast.makeText(MainActivity.this, "Transaction cancelled or failed please try again.", Toast.LENGTH_SHORT).show();
            mPaymentStatus.setText("Transaction Failed of ₹" + amount);
            mPaymentStatus.setTextColor(Color.RED);
        }

    }
}
