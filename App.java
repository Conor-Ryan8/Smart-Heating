package com.smartheat.conor.smartheat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity
{
    public static int BedHeatStatus;
    public static int MainHeatStatus;
    public static int BlanketStatus;
    public static int LightStatus;
    public static int Loop = 1;
    public static int MainTempValue;
    public static int MainHumidValue;
    public static int BedTempValue;
    public static int BedHumidValue;

    ImageView MainImageView;
    ImageView BedImageView;
    ImageView BlanketImageView;
    ImageView LightImageView;
    TextView MainTempText;
    TextView MainHumidText;
    TextView BedTempText;
    TextView BedHumidText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("TESTLOG","Program Starting...");
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_main);

        MainImageView = findViewById(R.id.MainHeatImage);
        BedImageView = findViewById(R.id.BedHeatImage);
        BlanketImageView = findViewById(R.id.BlanketImage);
        LightImageView = findViewById(R.id.LightImage);

        MainImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG","Main Heat Clicked"+ MainHeatStatus);
                clicked("1");
            }
        });
        BedImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG","Bed Heat Clicked");
                clicked("2");

            }
        });
        BlanketImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG","Blanket Clicked");
                clicked("3");
            }
        });
        LightImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG","Light Clicked");
                clicked("4");
            }
        });
        Thread getData = new Thread( new Runnable()
        {
            byte[] receive = new byte[19];
            DatagramSocket socket;
            DatagramPacket packet;
            InetAddress address;
            {
                try
                {
                    address = InetAddress.getByName("34.245.213.25");
                    Log.d("TESTLOG1","Address Found");
                }
                catch (UnknownHostException e)
                {
                    Log.d("TESTLOG1","Finding Address Failed!");
                }
            }
            @SuppressWarnings("unused")
            @Override
            public void run()
            {
                byte[] temp;
                String send = "9";

                try
                {
                    Log.d("TESTLOG1","Attempting to Create Socket");
                    socket = new DatagramSocket();
                    Log.d("TESTLOG1","Socket Created");
                }
                catch (SocketException e)
                {
                    Log.d("TESTLOG1","Socket Create Failed!");
                }
                try
                {
                    Log.d("TESTLOG1","Attempting to Bind on port 9999");
                    socket = new DatagramSocket(9999);
                    Log.d("TESTLOG1","Listening on port 9999");
                }
                catch (SocketException e)
                {
                    Log.d("TESTLOG1","Binding Failed!");
                }

                temp = send.getBytes();


                while (Loop == 1)
                {
                    packet = new DatagramPacket(temp, temp.length, address, 9998);
                    try
                    {
                        socket.setSoTimeout(1000);
                        socket.send(packet);
                        Log.d("TESTLOG1","Sent:"+ send);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        Log.d("TESTLOG1","Send Failed!");
                    }

                    packet = new DatagramPacket(receive, 19);

                    if (socket != null)
                    {
                        try
                        {
                            socket.receive(packet);
                        }
                        catch (IOException e1)
                        {
                            Log.d("TESTLOG", "Receive Failed :(");
                        }

                        String data = new String(packet.getData());
                        Log.d("TESTLOG", "Received: " + data);
                        String[] parts = data.split(",");
                        MainHeatStatus = Integer.parseInt(parts[0]);
                        BedHeatStatus = Integer.parseInt(parts[1]);
                        BlanketStatus = Integer.parseInt(parts[2]);
                        LightStatus = Integer.parseInt(parts[3]);
                        MainTempValue = Integer.parseInt(parts[4]);
                        MainHumidValue = Integer.parseInt(parts[5]);
                        BedTempValue = Integer.parseInt(parts[6]);
                        BedHumidValue = Integer.parseInt(parts[7]);
                    }
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread Graphics = new Thread( new Runnable()
        {
            int DisplayedMainHeatStatus = MainHeatStatus;
            int DisplayedBedHeatStatus = BedHeatStatus;
            int DisplayedBlanketStatus = BlanketStatus;
            int DisplayedMainTempValue = MainTempValue;
            int DisplayedMainHumidValue = MainHumidValue;
            int DisplayedBedTempValue = BedTempValue;
            int DisplayedBedHumidValue = BedHumidValue;
            int DisplayedLightStatus = LightStatus;

            @SuppressWarnings("unused")
            @Override
            public void run()
            {
                while (Loop == 1)
                {
                    if (DisplayedMainHeatStatus != MainHeatStatus)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateMain();
                            }
                        });
                        DisplayedMainHeatStatus = MainHeatStatus;
                        Log.d("TESTLOG","Updated Main Heater Graphic!");
                    }
                    if (DisplayedBedHeatStatus != BedHeatStatus)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateBed();
                            }
                        });
                        DisplayedBedHeatStatus = BedHeatStatus;
                        Log.d("TESTLOG","Updated Bedroom Heater Graphic!");
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
                        Log.d("TESTLOG","Updated Blanket Graphic!");
                    }
                    if (DisplayedMainTempValue != MainTempValue)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateMainTemp();
                            }
                        });
                        DisplayedMainTempValue = MainTempValue;
                        Log.d("TESTLOG","Updated Main Temperature Value!");
                    }
                    if (DisplayedMainHumidValue != MainHumidValue)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateMainHumid();
                            }
                        });
                        DisplayedMainHumidValue = MainHumidValue;
                        Log.d("TESTLOG","Updated Main Humid Value!");
                    }
                    if (DisplayedBedTempValue != BedTempValue)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateBedTemp();
                            }
                        });
                        DisplayedBedTempValue = BedTempValue;
                        Log.d("TESTLOG","Updated Bedroom Temperature Value!");
                    }
                    if (DisplayedBedHumidValue != BedHumidValue)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateBedHumid();
                            }
                        });
                        DisplayedBedHumidValue = BedHumidValue;
                        Log.d("TESTLOG","Updated Bedroom Humid Value!");
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
                        Log.d("TESTLOG","Updated Light Graphic!");
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
        updateMain();
        updateBed();
        updateBlanket();
        updateMainTemp();
        updateMainHumid();
        updateBedTemp();
        updateBedHumid();
        updateLight();
    }
    public void updateMain()
    {
        if (MainHeatStatus == 0)
        {
            MainImageView = findViewById(R.id.MainHeatImage);
            MainImageView.setImageResource(R.drawable.heatoff);
        }
        else if (MainHeatStatus == 1)
        {
            MainImageView = findViewById(R.id.MainHeatImage);
            MainImageView.setImageResource(R.drawable.heaton);
        }
    }
    public void updateBed()
    {
        if (BedHeatStatus == 0)
        {
            BedImageView = findViewById(R.id.BedHeatImage);
            BedImageView.setImageResource(R.drawable.heatoff);
        }
        else if (BedHeatStatus == 1)
        {
            BedImageView = findViewById(R.id.BedHeatImage);
            BedImageView.setImageResource(R.drawable.heaton);
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
    public void updateMainTemp()
    {
        MainTempText = findViewById(R.id.MainTempText);
        String Message = "Living Room is "+ MainTempValue + " Degrees";
        MainTempText.setText(Message);
    }
    public void updateBedTemp()
    {
        BedTempText = findViewById(R.id.bedtemp);
        String Message = "Bedroom is "+ BedTempValue + " Degrees";
        BedTempText.setText(Message);
    }
    public void updateMainHumid()
    {
        MainHumidText = findViewById(R.id.mainhumid);
        String Message = "Living Room is "+ MainHumidValue + "% Humidity";
        MainHumidText.setText(Message);
    }
    public void updateBedHumid()
    {
        BedHumidText = findViewById(R.id.bedhumid);
        String Message = "Bedroom is "+ BedHumidValue + "% Humidity";
        BedHumidText.setText(Message);
    }

    public void clicked(final String data)
    {
        Thread send = new Thread( new Runnable()
        {
            DatagramSocket socket;
            DatagramPacket packet;
            InetAddress address;
            {
                try
                {
                    address = InetAddress.getByName("34.245.213.25");
                    Log.d("TESTLOG","Address Found");
                }
                catch (UnknownHostException e)
                {
                    Log.d("TESTLOG","Finding Address Failed!");
                }
            }
            byte[] temp;
            @SuppressWarnings("unused")
            @Override
            public void run()
            {
                try
                {
                    Log.d("TESTLOG","Attempting to Create Socket");
                    socket = new DatagramSocket();
                    Log.d("TESTLOG","Socket Created");
                }
                catch (SocketException e)
                {
                    Log.d("TESTLOG","Socket Create Failed!");
                }
                temp = data.getBytes();
                packet = new DatagramPacket(temp, temp.length, address, 9998);
                try
                {
                    socket.send(packet);
                    Log.d("TESTLOG","Sent:"+ data);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Log.d("TESTLOG","Send Failed!");
                }
            }
        });
        send.start();
    }
}
