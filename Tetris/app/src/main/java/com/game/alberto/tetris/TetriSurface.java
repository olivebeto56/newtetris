package com.game.alberto.tetris;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.List;


public class TetriSurface extends SurfaceView implements SurfaceHolder.Callback , SensorEventListener {

    TetrisThread thread;
    int x;
    int y;
    boolean move=false;
    boolean push=false;
    Tetris tetris;
    Context contx;

    private float curX = 0, curY = 0, curZ = 0;

    public TetriSurface(Context context){
        super(context);
        getHolder().addCallback(this);
        this.contx=context;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread= new TetrisThread(getHolder(),this);
        thread.setRunning(true);
        thread.start();
        paint = new Paint();
        tetris= new Tetris();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("surfaceDestroyed", "Hilo deteniendose");

        boolean retry = true;

        thread.setRunning(false);

        while(retry){
            try{

                thread.join();

                retry=false;
            }catch (InterruptedException e){}

        }

        Log.e("surfaceDestroyed","Hilo detenido");

        onStop();

    }


    public Paint paint;




    float ancho;
    int xp=0,yp=0;

    @Override
    protected void onDraw(Canvas canvas) {

        boton();
        onResume();
        pos();
        canvas.drawColor(Color.BLACK);

        ancho=canvas.getWidth()/13;

        if(tetris.cont1==0){
            tetris.tokenNumber1 = (int) (7*Math.random());
            tetris.cont1=1;
        }
        if (!tetris.gameOver&&!tetris.pause){
            if(tetris.cont==0){
                tetris.FallingToken();
                tetris.cont++;
                tetris.tokenNumber1 = (int) (7*Math.random());
                System.out.print(tetris.tokenNumber1);

            }
            else if(tetris.cont==1){
                if(!tetris.reachFloor){
                    try { Thread.sleep(tetris.delay); } catch (Exception ignore) {}
                    tetris.shiftDown();
                }
                else{
                    tetris.cont++;
                    tetris.reachFloor=false;
                }
            }
            else if(tetris.cont==2){
                tetris.complete=tetris.checkRowCompletion();
                tetris.clearCompleteRow(tetris.complete);
                tetris.addScore(tetris.complete);
                tetris.cont++;
            }
            else if(tetris.cont==3){
                try { Thread.sleep(100); } catch (Exception ignore) {}
                tetris.shiftDownComplete(tetris.complete);
                tetris.cont=0;
            }
            else{

            }


            paint1(canvas);
        }else {
            if(tetris.gameOver){
                tetris.printGameOver();
            }
            else if(tetris.pause){

            }
        }
        ////////////////////////////////////////////////////////////////////

}
    int aux=0;
    int aux1=0;

    public void pos(){

        if(curX>1.50f){
            if(curX>3.00f){
                if(aux>2)aux=0;
                ////////////////
                if(aux==2){
                    tetris.leftPressed = true;
                    aux=0;
                }else
                    tetris.leftPressed=false;
                aux++;
                ///////////////////
            }else{
                if(aux==6){
                    tetris.leftPressed = true;
                    aux=0;
                }else
                    tetris.leftPressed=false;
                aux++;
            }


        }else{
            aux=0;
            tetris.leftPressed=false;
        }

        if(curX<-1.50f){
            if(curX<-3.00f){
                if(aux1>2)aux1=0;

                //////////////
                if(aux1==2){
                    tetris.rightPressed = true;
                    aux1=0;
                }else
                    tetris.rightPressed=false;
                aux1++;
                //////////////
            }else{
                if(aux1==6){
                    tetris.rightPressed = true;
                    aux1=0;
                }else
                    tetris.rightPressed=false;
                aux1++;
            }

        }else{
            aux1=0;
            tetris.rightPressed=false;
        }

    }

    public void paint1(Canvas canvas){
        for (int x1=0;x1<tetris.occupied.length;x1++)
            for (int y1=0;y1<tetris.occupied[0].length;y1++)
                if (tetris.occupied[x1][y1]==1)
                {
                    paint.setARGB(255, 255, 0, 0);

                    canvas.drawRect(x1*ancho,y1*ancho,(x1*ancho)+ancho,(y1*ancho)+ancho,paint);

                }

    }
    int firts=0;
    int x1,y1=0;
    boolean aux3=false;

    public void boton(){


        if(push){
            if(firts==0){
                x1=x;
                y1=y;
            }

            if(firts==10){
                if((y-y1)>100)
                    aux3=tetris.downPressed=true;
            }

            firts++;
        }
        else{
            firts=0;
        }

        if(move&&!aux3)
            tetris.spacePressed=true;
        else if(!move)
            aux3=false;


        move=false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x=(int) event.getX();
        y=(int) event.getY();

        int action=event.getAction();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                push=true;
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                push=false;
                move=true;


                break;
            case MotionEvent.ACTION_CANCEL:
                push=move=false;

                break;

            case MotionEvent.ACTION_OUTSIDE:
                push=move=false;
                break;

        }

        return true;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            curX= getDeviceDefaultOrientation()==1 ? event.values[0]:event.values[1];

        }


    }

    protected void onResume() {
        SensorManager sm;
        sm = (SensorManager) contx.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }


    protected void onStop() {
        SensorManager sm = (SensorManager) contx.getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getDeviceDefaultOrientation() {

        WindowManager windowManager =  (WindowManager) contx.getSystemService(Context.WINDOW_SERVICE);

        Configuration config = getResources().getConfiguration();

        int rotation = windowManager.getDefaultDisplay().getRotation();

        if ( ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
                config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
            return Configuration.ORIENTATION_LANDSCAPE;
        } else {
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }

}