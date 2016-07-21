package com.shapewriter.android.softkeyboard;




import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

public class SWI_CommandPage extends TabActivity {
	
	public final static int LARGE_FONT_SIZE = 20;
	public final static int NORMAL_FONT_SIZE = 16;
	public final static int SMALL_FONT_SIZE = 14;
	
	private static String mainTitleText = "Command Stroke";
	private static String tab2TitleText = "Command List";
	private int screenWidth;
	
	OnClickListener listener0 = null;
    Button button0;
    OnClickListener listener1 = null;
    Button button1;
    OnClickListener listener2 = null;
    Button button2;
    OnClickListener listener3 = null;
    Button button3;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.command_page,
				tabHost.getTabContentView(), true);
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    	screenWidth = wm.getDefaultDisplay().getWidth();
    	
		ScrollView tv1 = (ScrollView)findViewById(R.id.view1);
		ScrollView tv2 = (ScrollView)findViewById(R.id.view2);
    	
		LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.BLACK);
        
        TextView mainTitle = new TextView(this);
        mainTitle.setTextColor(Color.WHITE);
        mainTitle.setTextSize(LARGE_FONT_SIZE);
        mainTitle.setBackgroundColor(Color.BLACK);
        mainTitle.setText(convertToCenter(mainTitleText, (int)mainTitle.getTextSize()));
        
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.command_page);
        SimpleImageView keyborad = new SimpleImageView(this, image);
        
        Bitmap marginBitmap = BitmapFactory.decodeResource(getResources(), 
        		R.raw.port_qwerty_margin);
        SWI_MargingroundView marginView1 = new SWI_MargingroundView(this, image.getWidth(),
        		image.getHeight(), marginBitmap);
        SWI_MargingroundView marginView2 = new SWI_MargingroundView(this, image.getWidth(),
        		image.getHeight(), marginBitmap);
        
        LinearLayout imageLayout = new LinearLayout(this);
        imageLayout.setOrientation(LinearLayout.HORIZONTAL);
        imageLayout.addView(marginView1);
        imageLayout.addView(keyborad);
        imageLayout.addView(marginView2);
        
        TextView body = new TextView(this);
        body.setTextColor(Color.WHITE);
        body.setTextSize(NORMAL_FONT_SIZE);
        body.setBackgroundColor(Color.BLACK);
        body.setText("Application commands such as cut, copy or paste can be invoked " +
        		"using ShapeWriter. To shape write a command, start your stroke gesture " +
        		"at the \"ShapeWriter\" key. For example, the cut command can be invoked " +
        		"by tracing from the \"ShapeWriter\" key to c-u-t.\n");    
        
        layout.addView(mainTitle);
        layout.addView(body);
        layout.addView(imageLayout);
        layout.addView(new TextView(this));
        
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        Button lauchVideo = new Button(this);
        lauchVideo.setWidth(screenWidth / 3);
        lauchVideo.setText("Video");
        lauchVideo.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse("http://www.youtube.com/watch?v=MeTb7nPYlOA"));
				startActivity(intent);
			}
        });        
        buttonLayout.addView(lauchVideo);
        
        Button lauchHelp = new Button(this);
        lauchHelp.setWidth(screenWidth / 3);
        lauchHelp.setText("Help");
        lauchHelp.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse("http://www.shapewriter.com/download/help/android_help/and_kb_help.html"));
				startActivity(intent);
			}
        });        
        buttonLayout.addView(lauchHelp);
        
        Button lauchSetting = new Button(this);
        lauchSetting.setWidth(screenWidth / 3);
        lauchSetting.setText("Settings");
        lauchSetting.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SWI_CommandPage.this, SWI_IMESettings.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
        });
        buttonLayout.addView(lauchSetting);
        
        layout.addView(buttonLayout);
		
		tv1.addView(layout);
		
		// tab2		
		LinearLayout tab2layout = new LinearLayout(this);
		tab2layout.setOrientation(LinearLayout.VERTICAL);
//		tab2layout.setBackgroundColor(Color.BLACK);
		
		TextView tab2Title = new TextView(this);
		tab2Title.setTextColor(Color.WHITE);
		tab2Title.setTextSize(LARGE_FONT_SIZE);
//		tab2Title.setBackgroundColor(Color.BLACK);
		tab2Title.setText(convertToCenter(tab2TitleText, (int)mainTitle.getTextSize()));
		
    	TextView newTab = new TextView(this);
	//	newTab.setBackgroundColor(Color.BLACK);
		newTab.setTextColor(Color.WHITE);
		newTab.setTextSize(NORMAL_FONT_SIZE);
		newTab.setText("\t cut - to cut selected text. \n" + 
				"\t copy - to copy selected text. \n" + 
				"\t paste - to paste text that was previously copied or pasted. \n" +
				"\t all/select all - to select all the text in a note. \n" +
				"\t replay - to replay your most recent stroke gesture. \n" +
				"\t literal - Turn on/off the editing feature. \n" +
				"\t settings - to the setting page. \n" +
/*				"\t qwerty - change to the qwerty keyboard. \n" +
				"\t atomic - change to the atomic keyboard. \n" +*/
				"\t hide/close - hide keyboard. \n" +
				"\t game - to the shapewriter game page. \n" +
				"\t help - to the help page. \n" +
				"\t video - to open the demo video of ShapeWriter in YouTube \n");
		
		tab2layout.addView(tab2Title); 
		tab2layout.addView(newTab);
		tv2.addView(tab2layout);
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Command Stroke")
				.setContent(R.id.view1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Command List")
				.setContent(R.id.view2));
	}
	
	private String convertToCenter(String text, int fontSize){
		String blank = new String("                                                      ");
		int textLength = getTextWidth(text, fontSize);
		int blankLength = Math.max(0, (screenWidth - textLength) / 2);
		int blankNum = getPositionInWidth(blank, fontSize, blankLength);
		return blank.substring(0, blankNum) + text;
	}
	
	private static int getTextWidth(String text, int fontSize) {

		float[] width = new float[text.length()];
		Paint pt = new Paint();
		pt.setTextSize(fontSize);
		pt.getTextWidths(text, width);
		int selectionWidth = 0;
		for (int i = 0; i < text.length(); ++i) {
			selectionWidth += (int) width[i];
		}
		return selectionWidth;
	}
	
	protected static int getPositionInWidth(String text, int fontSize, int maxLength){
		float[] width = new float[text.length()];
		Paint pt = new Paint();
		pt.setTextSize(fontSize);
		pt.getTextWidths(text, width);
		int selectionWidth = 0;
		for (int i = 0; i < text.length(); ++i) {
			selectionWidth += (int) width[i];
			if(selectionWidth > maxLength)
				return i - 1;
		}
		return text.length() - 1;
	}
	
	class SimpleImageView extends View{

		private Bitmap bitmap;
		public SimpleImageView(Context context, Bitmap bitmap) {
			super(context);
			this.bitmap = bitmap;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(bitmap.getWidth(), bitmap.getHeight());
		}
	}

}
