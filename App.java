package com.smartheat.conor.smartheat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity
{
    public static int BedHeat;
    public static int MainHeat;
    public static int Blanket;
    public static int Loop = 1;

    ImageView mainimage;
    ImageView bedimage;
    ImageView blanketimage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("TESTLOG","Program Starting...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainimage = findViewById(R.id.mainimage);
        bedimage = findViewById(R.id.bedimage);
        blanketimage = findViewById(R.id.blanketimage);



        mainimage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG","Main Heat Clicked"+MainHeat);
                clicked("1");
            }
        });
        bedimage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG","Bed Heat Clicked");
                clicked("2");

            }
        });
        blanketimage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("TESTLOG","Blanket Clicked");
                clicked("3");
            }
        });
        Thread Subscribe = new Thread( new Runnable()
        {
            byte[] receive = new byte[5];
            DatagramSocket socket;
            DatagramPacket packet;
            @SuppressWarnings("unused")
            @Override
            public void run()
            {
                try
                {
                    Log.d("TESTLOG","Attempting to Bind on port 9999");
                    socket = new DatagramSocket(9999);
                    Log.d("TESTLOG","Listening on port 9999");
                }
                catch (SocketException e)
                {
                    Log.d("TESTLOG","Binding Failed!");
                }

                packet = new DatagramPacket(receive, 5);
                Log.d("TESTLOG", "Attempting to get data!");

                while (Loop == 1)
                {
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
                        MainHeat = Integer.parseInt(parts[0]);
                        BedHeat = Integer.parseInt(parts[1]);
                        Blanket = Integer.parseInt(parts[2]);
                    }
                    try
                    {
                        Thread.sleep(200);
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
            int CurrentMain = MainHeat;
            int CurrentBed = BedHeat;
            int CurrentBlanket = Blanket;
            @SuppressWarnings("unused")
            @Override
            public void run()
            {


                while (Loop == 1)
                {
                    if (CurrentMain != MainHeat)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateMain();
                            }
                        });
                        CurrentMain = MainHeat;
                        Log.d("TESTLOG","Updated Main Heat Graphic!");
                    }
                    if (CurrentBed != BedHeat)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateBed();
                            }
                        });
                        CurrentBed = BedHeat;
                        Log.d("TESTLOG","Updated Bed Heat Graphic!");
                    }
                    if (CurrentBlanket != Blanket)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateBlanket();
                            }
                        });
                        CurrentBlanket = Blanket;
                        Log.d("TESTLOG","Updated Blanket Graphic!");
                    }
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch(InterruptedException e)
                    {
                        Log.d("TESTLOG","Sleep error in Graphics Thread!");
                    }
                }
            }
        });


        Subscribe.start();
        Graphics.start();
        updateMain();
        updateBed();
        updateBlanket();
    }
    public void updateMain()
    {
        if (MainHeat == 0)
        {
            mainimage = findViewById(R.id.mainimage);
            mainimage.setImageResource(R.drawable.heatoff);
        }
        else if (MainHeat == 1)
        {
            mainimage = findViewById(R.id.mainimage);
            mainimage.setImageResource(R.drawable.heaton);
        }
    }
    public void updateBed()
    {
        if (BedHeat == 0)
        {
            bedimage = findViewById(R.id.bedimage);
            bedimage.setImageResource(R.drawable.heatoff);
        }
        else if (BedHeat == 1)
        {
            bedimage = findViewById(R.id.bedimage);
            bedimage.setImageResource(R.drawable.heaton);
        }
    }
    public void updateBlanket()
    {
        if (Blanket == 0)
        {
            blanketimage = findViewById(R.id.blanketimage);
            blanketimage.setImageResource(R.drawable.bedoff);
        }
        else if (Blanket == 1)
        {
            blanketimage = findViewById(R.id.blanketimage);
            blanketimage.setImageResource(R.drawable.bedon);
        }
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
                    address = InetAddress.getByName("192.168.1.3");
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
