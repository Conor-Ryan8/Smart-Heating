package com.smartheat.conor.smartheat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity
{
    public static int HeatStatus;
    public static int LightStatus;
    public static int BlanketStatus;
    public static int TempValue;
    public static int HumidValue;
    public static int Loop = 1;

    ImageView HeatImageView;
    ImageView LightImageView;
    ImageView BlanketImageView;
    TextView TempText;
    TextView HumidText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("TESTLOG", "Program Starting...");
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_main);
        HeatImageView = findViewById(R.id.HeatImage);
        BlanketImageView = findViewById(R.id.BlanketImage);
        LightImageView = findViewById(R.id.LightImage);

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(getApplicationContext(), "tcp://192.168.0.131:1883",clientId);

        HeatImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG", "Heat Clicked");

                try
                {
                    client.connect().setActionCallback(new IMqttActionListener()
                    {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken)
                        {

                            Log.d("MQTT", "Connected to Mosquito");

                            String ID = "1";
                            MqttMessage message = new MqttMessage(ID.getBytes());
                            try
                            {
                                client.publish("Toggle",message);
                            }
                            catch (MqttException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                        {
                            Log.d("MQTT", "Failed to Connect to Mosquito");
                        }
                    });
                } catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        });

        LightImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("MQTT", "Light Clicked");

                try
                {
                    client.connect().setActionCallback(new IMqttActionListener()
                    {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken)
                        {

                            Log.d("MQTT", "Connected to Mosquito");

                            String ID = "2";
                            MqttMessage message = new MqttMessage(ID.getBytes());
                            try
                            {
                                client.publish("Toggle",message);
                            }
                            catch (MqttException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                        {
                            Log.d("MQTT", "Failed to Connect to Mosquito");
                        }
                    });
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        });

        BlanketImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG", "Blanket Clicked");
                try
                {
                    client.connect().setActionCallback(new IMqttActionListener()
                    {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken)
                        {

                            Log.d("MQTT", "Connected to Mosquito");

                            String ID = "3";
                            MqttMessage message = new MqttMessage(ID.getBytes());
                            try
                            {
                                client.publish("Toggle",message);
                            }
                            catch (MqttException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                        {
                            Log.d("MQTT", "Failed to Connect to Mosquito");
                        }
                    });
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        });

        Thread getData = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String clientId = MqttClient.generateClientId();
                final MqttAndroidClient client = new MqttAndroidClient(getApplicationContext(),"tcp://192.168.0.131:1883",clientId);

                try
                {
                    client.connect().setActionCallback(new IMqttActionListener()
                    {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken)
                        {
                            Log.d("MQTT", "Connected to Mosquito");
                            String Topic = "DEVICES";

                            IMqttMessageListener iMqttMessageListener = new IMqttMessageListener()
                            {
                                @Override
                                public void messageArrived(String topic, MqttMessage message) throws Exception
                                {

                                    String data = message.toString();
                                    String[] parts = data.split(",");
                                    HeatStatus = Integer.parseInt(parts[0]);
                                    LightStatus = Integer.parseInt(parts[1]);
                                    BlanketStatus = Integer.parseInt(parts[2]);
                                    TempValue = Integer.parseInt(parts[3]);
                                    HumidValue = Integer.parseInt(parts[4]);
                                }
                            };
                            try
                            {
                                client.subscribe(Topic, 2, iMqttMessageListener);
                            }
                            catch (MqttException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                        {
                            Log.d("MQTT", "Something went wrong e.g. connection timeout or firewall problems");
                        }
                    });
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        });

        Thread Graphics = new Thread(new Runnable()
        {
            int DisplayedHeatStatus = HeatStatus;
            int DisplayedLightStatus = LightStatus;
            int DisplayedBlanketStatus = BlanketStatus;
            int DisplayedTempValue = TempValue;
            int DisplayedHumidValue = HumidValue;

            @Override
            public void run()
            {
                while (Loop == 1)
                {
                    if (DisplayedHeatStatus != HeatStatus)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateHeat();
                            }
                        });
                        DisplayedHeatStatus = HeatStatus;
                        Log.d("TESTLOG", "Updated Heater Graphic!");
                    }
                    if (DisplayedLightStatus != LightStatus)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateLight();
                            }
                        });
                        DisplayedLightStatus = LightStatus;
                        Log.d("TESTLOG", "Updated Light Graphic!");
                    }
                    if (DisplayedBlanketStatus != BlanketStatus)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateBlanket();
                            }
                        });
                        DisplayedBlanketStatus = BlanketStatus;
                        Log.d("TESTLOG", "Updated Blanket Graphic!");
                    }
                    if (DisplayedTempValue != TempValue)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateTemp();
                            }
                        });
                        DisplayedTempValue = TempValue;
                        Log.d("TESTLOG", "Updated Temperature Value!");
                    }
                    if (DisplayedHumidValue != HumidValue)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateHumid();
                            }
                        });
                        DisplayedHumidValue = HumidValue;
                        Log.d("TESTLOG", "Updated Humid Value!");
                    }
                    try
                    {
                        Thread.sleep(250);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        getData.start();
        Graphics.start();
        updateHeat();
        updateLight();
        updateBlanket();
        updateTemp();
        updateHumid();
    }

    public void updateHeat()
    {
        if (HeatStatus == 0)
        {
            HeatImageView = findViewById(R.id.HeatImage);
            HeatImageView.setImageResource(R.drawable.heatoff);
        } 
        else if (HeatStatus == 1)
        {
            HeatImageView = findViewById(R.id.HeatImage);
            HeatImageView.setImageResource(R.drawable.heaton);
        }
    }
    public void updateBlanket()
    {
        if (BlanketStatus == 0)
        {
            BlanketImageView = findViewById(R.id.BlanketImage);
            BlanketImageView.setImageResource(R.drawable.bedoff);
        } 
        else if (BlanketStatus == 1)
        {
            BlanketImageView = findViewById(R.id.BlanketImage);
            BlanketImageView.setImageResource(R.drawable.bedon);
        }
    }
    public void updateLight()
    {
        if (LightStatus == 0)
        {
            LightImageView = findViewById(R.id.LightImage);
            LightImageView.setImageResource(R.drawable.lightoff);
        }
        else if (LightStatus == 1)
        {
            LightImageView = findViewById(R.id.LightImage);
            LightImageView.setImageResource(R.drawable.lighton);
        }
    }
    public void updateTemp()
    {
        TempText = findViewById(R.id.TempText);
        String Message = TempValue + "°";
        TempText.setText(Message);
    }
    public void updateHumid()
    {
        HumidText = findViewById(R.id.HumidText);
        String Message = HumidValue + "% Humidity";
        HumidText.setText(Message);
    }
}
