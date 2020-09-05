package com.example.tp02;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSService extends Service {
    public static final String TAG = "SMSService";
    private segundoHilo hilo2;

    public static void startCount(int cantidadSMS, Context context) {
        Intent intent = new Intent(context, SMSService.class);
        intent.setAction("com.example.tp02.CONTAR_ACTION");
        intent.putExtra("CANTIDAD_CONTAR", cantidadSMS);
        context.startService(intent);
    }


    private final class segundoHilo extends Handler {
        Uri uri = Uri.parse("content://sms/inbox");
        //CONSULTA
        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{"_id", "address", "date_sent", "body"},
                null,
                null,
                "date DESC"+" LIMIT 6");

        public segundoHilo(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "El hilo (Thread) actual es " + Thread.currentThread().getName());

            Intent request = (Intent) msg.obj;
            int cantidad = request.getIntExtra("CANTIDAD_CONTAR", 0);
            contarHasta(cantidad);

            // Detendremos el servicio usando el startId mediante stopSelf, si saldria del contarHasta()
            stopSelf(msg.arg1);
        }

        private void contarHasta(int limite) {

            while (true) {
                try {
                    Thread.sleep(9000);
                    leerSMS(limite);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

            }


        }
        //Nota: se podria usar la variable int limite para marcar un tope de 5 mensajes
        private void leerSMS(int limite) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                Date date = new Date(cursor.getLong(2));
                String formattedDate = new SimpleDateFormat("MM/dd/yyyy").format(date);

                Log.d("SMSService", cursor.getString(0) + ": " + "Fecha: " + formattedDate + " Cuerpo: " + cursor.getString(3));
            }
        }

    }

    @Override
    public void onCreate() {
        Log.i(TAG, "SMSService...");

        HandlerThread hiloEnSegundoPlano = new HandlerThread("Segundo Hilo", Process.THREAD_PRIORITY_BACKGROUND);
        hiloEnSegundoPlano.start();
        hilo2 = new segundoHilo(hiloEnSegundoPlano.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Log.i(TAG, "El id recibido es " + startId + ": " + intent);
        Log.i(TAG, "El hilo (Thread) actual es " + Thread.currentThread().getName());

        Message mensaje = hilo2.obtainMessage();
        mensaje.arg1 = startId;
        mensaje.obj = intent;
        hilo2.sendMessage(mensaje);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Detenido...");
    }
}

