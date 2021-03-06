package com.madxstudio.co8.zxing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseActivity;
import com.madxstudio.co8.ui.FamilyProfileActivity;
import com.madxstudio.co8.ui.FamilyViewProfileActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.MeActivity;
import com.madxstudio.co8.ui.more.ViewQRCodeActivity;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.zxing.camera.CameraManager;
import com.madxstudio.co8.zxing.decoding.CaptureActivityHandler;
import com.madxstudio.co8.zxing.decoding.InactivityTimer;
import com.madxstudio.co8.zxing.decoding.RGBLuminanceSource;
import com.madxstudio.co8.zxing.view.ViewfinderView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Initial the camera
 * 
 */
public class CaptureActivity extends BaseActivity implements Callback {

    private final String TAG = "CaptureActivity";
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button cancelScanButton;
    private View progressDialog;
    private UserEntity userEntity;
    private RelativeLayout rlScanQR;

	int ifOpenLight = 0; // 判断是否开启闪光灯
    public static String SCAN_RESULT = "bwm_id";


    @Override
    public int getLayout() {
        return R.layout.activity_capture;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.text_scan_qr);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        // ViewUtil.addTopView(getApplicationContext(), this,
        // R.string.scan_card);
        CameraManager.init(getApplication());
        progressDialog = getViewById(R.id.rl_progress);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cancelScanButton = (Button) this.findViewById(R.id.btn_my_qr);
        rlScanQR = getViewById(R.id.rl_scan_qr);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

		// quit the scan view
		cancelScanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentQRCode = new Intent(CaptureActivity.this, ViewQRCodeActivity.class);
                intentQRCode.putExtra("from_scan", "view_my_qr");
                startActivity(intentQRCode);
            }
        });
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * 
	 * @param result
	 * @param barcode
	 *            获取结果
	 */

    String strPrefixId = "=";
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
        LogUtil.d("CaptureActivity", "handleDecode()====resultString===" + resultString);
        if (resultString.contains("bid=")){
            resultString=resultString.substring(resultString.indexOf(strPrefixId) + 1,resultString.length());
            LogUtil.d("CaptureActivity", "resultString===" + resultString);
            checkId(resultString);
        }else {
            Toast.makeText(CaptureActivity.this, getString(R.string.text_invalid_qr), Toast.LENGTH_SHORT)
                    .show();
            CaptureActivity.this.finish();
        }

//		CaptureActivity.this.finish();
	}

    private void checkId(final String bwmId) {
        UserEntity me  = MainActivity.getUser();
        LogUtil.d(TAG,"checkId()======bwmId:"+bwmId);
        LogUtil.d(TAG,"checkId()======Dis_Bondwithme_id():"+me.getDis_bondwithme_id());
        if (me.getDis_bondwithme_id().equals(bwmId)) {
            LogUtil.d(TAG,"checkId()"+"bemIIIIIIIIIId:"+me.getDis_bondwithme_id());
            Intent resultIntent = new Intent(CaptureActivity.this, MeActivity.class);
            startActivity(resultIntent);
            return;
        } else {
            HashMap<String, String> jsonParams = new HashMap<>();
            jsonParams.put("user_id", me.getUser_id());
//        if(memberId != null){
//            jsonParams.put("member_id", memberId);
//        }else {
            jsonParams.put("bwm_id", bwmId);
//        }

            String jsonParamsString = UrlUtil.mapToJsonstring(jsonParams);
            HashMap<String, String> params = new HashMap<>();
            params.put("condition", jsonParamsString);
//            vProgress.setVisibility(View.VISIBLE);
            String url = UrlUtil.generateUrl(Constant.API_MEMBER_PROFILE_DETAIL, params);

            new HttpTools(this).get(url, params, TAG, new HttpCallback() {
                @Override
                public void onStart() {
                    progressDialog.setVisibility(View.VISIBLE);
//                    rlScanQR.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFinish() {
                    progressDialog.setVisibility(View.GONE);
                }

                @Override
                public void onResult(String response) {
                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();
                    List<UserEntity> data = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                    }.getType());
                    if ((data != null) && (data.size() > 0)) {
                        userEntity = data.get(0);
//                    Message.obtain(handler, GET_USER_ENTITY, userEntity).sendToTarget();
                        if (progressDialog.getVisibility() == View.VISIBLE) {
                            progressDialog.setVisibility(View.GONE);
                        }
                        String memberFlag = userEntity.getMember_flag();
                        LogUtil.d(TAG,"============member_flag:"+memberFlag+"========bwmId: "+bwmId);
                        if ("0".equals(memberFlag)){
                            Intent resultIntent = new Intent(CaptureActivity.this, FamilyViewProfileActivity.class);
                        resultIntent.putExtra("userEntity", userEntity);
                            resultIntent.putExtra(SCAN_RESULT,bwmId);
                            startActivity(resultIntent);
                        }else if("1".equals(memberFlag)){
                            Intent resultIntent = new Intent(CaptureActivity.this, FamilyProfileActivity.class);
                            resultIntent.putExtra("userEntity", userEntity);
                            resultIntent.putExtra(SCAN_RESULT,bwmId);
                            startActivity(resultIntent);
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
					MessageUtil.getInstance().showShortToast(getResources().getString(R.string.text_error));
                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });

        }
    }

    /*
     * 获取带二维码的相片进行扫描
     */
	public void pickPictureFromAblum(View v) {
		// 打开手机中的相册
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
		innerIntent.setType("image/*");
		Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
		this.startActivityForResult(wrapperIntent, 1);
	}

	String photo_path;
	ProgressDialog mProgress;
	Bitmap scanBitmap;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent) 对相册获取的结果进行分析
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				// 获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(),
						null, null, null, null);
				if (cursor.moveToFirst()) {
					photo_path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.i("路径", photo_path);
				}
				cursor.close();

				mProgress = new ProgressDialog(CaptureActivity.this);
				mProgress.setMessage("正在扫描...");
				mProgress.setCancelable(false);
				mProgress.show();

				new Thread(new Runnable() {
					@Override
					public void run() {
						Result result = scanningImage(photo_path);
						if (result != null) {
							Message m = mHandler.obtainMessage();
							m.what = 1;
							m.obj = result.getText();
							mHandler.sendMessage(m);
						} else {
							Message m = mHandler.obtainMessage();
							m.what = 2;
							m.obj = "Scan failed!";
							mHandler.sendMessage(m);
						}

					}
				}).start();
				break;

			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				mProgress.dismiss();
				String resultString = msg.obj.toString();
				if (resultString.equals("")) {
					Toast.makeText(CaptureActivity.this, "扫描失败!",
							Toast.LENGTH_SHORT).show();
				} else {
					// System.out.println("Result:"+resultString);
					Intent resultIntent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("result", resultString);
					resultIntent.putExtras(bundle);
					CaptureActivity.this.setResult(RESULT_OK, resultIntent);
				}
				CaptureActivity.this.finish();
				break;

			case 2:
				mProgress.dismiss();
				Toast.makeText(CaptureActivity.this, "解析错误！", Toast.LENGTH_LONG)
						.show();

				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	@Override
	protected void onStop() {
		CameraManager.get().stopPreview();
		CameraManager.get().closeLight();
		CameraManager.get().closeDriver();
		super.onStop();
	}

	/**
	 * 扫描二维码图片的方法
	 * 
	 * 目前识别度不高，有待改进
	 * 
	 * @param path
	 * @return
	 */
	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 100);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 是否开启闪光灯
	public void IfOpenLight(View v) {
		ifOpenLight++;

		switch (ifOpenLight % 2) {
		case 0:
			// 关闭
			CameraManager.get().closeLight();
			break;

		case 1:
			// 打开
			CameraManager.get().openLight(); // 开闪光灯
			break;
		default:
			break;
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};



    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}