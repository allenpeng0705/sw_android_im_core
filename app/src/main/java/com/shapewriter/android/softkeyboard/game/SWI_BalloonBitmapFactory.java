package com.shapewriter.android.softkeyboard.game;

import com.shapewriter.android.softkeyboard.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;

public class SWI_BalloonBitmapFactory {
	private Context context;
	private static SWI_BalloonBitmapFactory instance = null;

	private Bitmap balloons[];
	private Bitmap pop_balloons[];

	private int balloons_res[] = new int[] { R.drawable.blue, R.drawable.green,
			R.drawable.orange, R.drawable.pink, R.drawable.purple,
			R.drawable.red, R.drawable.white, R.drawable.yellow };

	private int pop_balloons_res[] = new int[] { R.drawable.pop,
			R.drawable.pop2, R.drawable.pop3 };

	private SWI_BalloonBitmapFactory(Context context) {
		this.context = context;

		balloons = new Bitmap[balloons_res.length];
		pop_balloons = new Bitmap[pop_balloons_res.length];

		InputStream is;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = false;

		for (int i = 0; i < balloons.length; i++) {
			is = context.getResources().openRawResource(balloons_res[i]);
			balloons[i] = BitmapFactory.decodeStream(is, null, opts);
		}

		for (int i = 0; i < pop_balloons.length; i++) {
			is = context.getResources().openRawResource(pop_balloons_res[i]);
			pop_balloons[i] = BitmapFactory.decodeStream(is, null, opts);
		}
	}

	public static synchronized SWI_BalloonBitmapFactory getInstance(Context context) {
		if (instance == null)
			instance = new SWI_BalloonBitmapFactory(context);
		return instance;
	}
	
	public Bitmap[] getBitmaps(){
		return balloons;
	}
	
	public Bitmap[] getPopBitmaps(){
		return pop_balloons;
	}
}
