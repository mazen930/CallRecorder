package com.example.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallReceiver extends BroadcastReceiver {
    MediaRecorder recorder;
    File audioFile;
    private boolean recordStarted = false;
    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    Bundle bundle;
    String state;
    String inCall, outCall;
    public boolean wasRinging = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_IN)) {
            if ((bundle = intent.getExtras()) != null) {
                state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    wasRinging = true;
                    Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (wasRinging == true) {
                        Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();
                        String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/TestRecordingDasa1"+out);
                        if (!sampleDir.exists()) {
                            sampleDir.mkdirs();
                        }
                        String file_name = "Record";
                        try {
                            audioFile = File.createTempFile(file_name, ".mp3", sampleDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(audioFile.getAbsolutePath());
                        try {
                            recorder.prepare();
                        } catch (IllegalStateException | IOException e) {
                            e.printStackTrace();
                        }
                        recorder.start();
                        recordStarted = true;
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    wasRinging = false;
                    Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
                    if (recordStarted) {
                        recorder.stop();
                        recordStarted = false;
                    }
                }
            }
        } else if (intent.getAction().equals(ACTION_OUT)) {
            if ((bundle = intent.getExtras()) != null) {
                outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
                state = bundle.getString(TelephonyManager.EXTRA_STATE);

                String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                File sampleDir = new File(Environment.getExternalStorageDirectory(), "/TestRecordingDasa1"+out);
                if (!sampleDir.exists()) {
                    sampleDir.mkdirs();
                }
                String file_name = "Record";
                try {
                    audioFile = File.createTempFile(file_name, ".mp3", sampleDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(audioFile.getAbsolutePath());
                try {
                    recorder.prepare();
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
                recorder.start();
                recordStarted = true;
                if ((bundle = intent.getExtras()) != null) {
                    state = bundle.getString(TelephonyManager.EXTRA_STATE);
                    if (state != null) {
                        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                            wasRinging = false;
                            Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
                            if (recordStarted) {
                                recorder.stop();
                                recordStarted = false;
                            }
                        }
                    }
                }
            }
        }
    }

}
