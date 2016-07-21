package com.shapewriter.android.softkeyboard;





import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.os.Bundle;
import android.widget.TextView;

public class SWI_StartPage extends TabActivity{
	
	public final static int LARGE_FONT_SIZE = 18;
	public final static int NORMAL_FONT_SIZE = 16;
	public final static int SMALL_FONT_SIZE = 14;
	
	private static String mainTitleText = "Welcome to ShapeWriter Keyboard";
	private static String subTitleText = "Version : 3.0.8"; 
	
	private int screenWidth;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setTitle("TabDemoActivity");
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.start_page,
				tabHost.getTabContentView(), true);
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    	screenWidth = wm.getDefaultDisplay().getWidth();
		
				
		ScrollView tv1 = (ScrollView)findViewById(R.id.view1);
		TextView tv2 = (TextView)findViewById(R.id.view2);
		ScrollView tv3 = (ScrollView)findViewById(R.id.view3);
		
		LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.BLACK);
        
        TextView mainTitle = new TextView(this);
        mainTitle.setTextColor(Color.WHITE);
        mainTitle.setTextSize(LARGE_FONT_SIZE);
        mainTitle.setBackgroundColor(Color.BLACK);
        mainTitle.setText(convertToCenter(mainTitleText, (int)mainTitle.getTextSize()));
        
        
        
        TextView subTitle = new TextView(this);
        subTitle.setTextColor(Color.WHITE);
        subTitle.setTextSize(SMALL_FONT_SIZE);
        subTitle.setBackgroundColor(Color.BLACK);
        subTitle.setText(convertToCenter(subTitleText, (int)subTitle.getTextSize()));
        
        TextView body = new TextView(this);
        body.setTextColor(Color.WHITE);
        body.setTextSize(NORMAL_FONT_SIZE);
        body.setBackgroundColor(Color.BLACK);
        body.setText("\nShapeWriter Keyboard has been" +
				" installed successfully. Now you only need to\n" +
				"1. Enable ShapeWriter -- " +
				"Click the button below to select \"ShapeWriter Keyboard\".\n" +
				"2. In any text field, such as the search box, press and " +
				"hold for a moment: an \"Edit text\" menu will pop up. " +
				"Tap \"Input Method\" then select \"ShapeWriter Keyboard\".\n");
        
        
        
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.start_show);
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
        
        TextView blank = new TextView(this);
        blank.setTextSize(SMALL_FONT_SIZE);
        blank.setText(" ");
        
        layout.addView(mainTitle);
        layout.addView(subTitle);
        layout.addView(body);
        layout.addView(imageLayout);
		layout.addView(blank);

        
        Button lauchEnableIM = new Button(this);
        lauchEnableIM.setText("Enable ShapeWriter Keyboard");
        lauchEnableIM.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName("com.android.settings", 
							"com.android.settings.LanguageSettings");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
        });
        layout.addView(lauchEnableIM);
		
		tv1.addView(layout);
		
		
		tv2.setBackgroundColor(Color.BLACK);
		tv2.setTextColor(Color.WHITE);
		tv2.setTextSize(NORMAL_FONT_SIZE);
		tv2.setText("See a short list of ShapeWriter input method instructions:\n\n" + 
					"http://www.shapewriter.com/download/help/android_help/and_kb_help.html\n\n" + 
					"Watch a demo video of ShapeWriter on YouTube:\n\n" +
					"http://www.youtube.com/watch?v=MeTb7nPYlOA\n\n" +
					"Developer's home page:\n\n" +
					"http://www.shapewriter.com\n");

		LinearLayout layoutNew = new LinearLayout(this);
		layoutNew.setOrientation(LinearLayout.VERTICAL);
			
		TextView newTab = new TextView(this);
		newTab.setTextColor(Color.WHITE);
		newTab.setTextSize(NORMAL_FONT_SIZE);
		newTab.setText("New Features in this version:\n\n" + 
				"Improved recognition performance. \n\n" +
				"Support Multi-language support:\n\n" +
				"This vertion supports seven languages, namely English, Spanish, French, FrenchQwerty, German, " +
				"Italian and Swedish. By default, English has been installed in your phone and you can" +
				" use it now. \nTo use other languages, you also to download a dictionary from " +
				"Android Market. \nThere is a link in \"ShapeWriter keyboard settings\" that will " +
				"help you to find these dictionaries in Android Market quickly. ( The button " +
				"below can enter \"ShapeWriter keyboard settings\" directly. )\n\n" + 
				"Globe Key\n\n" +
				"There is a new \"Globe Key\" on the keyboard, you can press it to switch " +
				"between two languages. To decide which two languages to switch, you can set them in " +
				"\"ShapeWriter keyboard settings\". Only two languages should be enabled at the same time.\n\n" +
				"User Dictionary Manager\n\n" +
				"One new tool that can help you to manage your personal user dictionary. You can add, " +
				"delete, or import words from your phone's user dictionary and contacts. Press the \"Menu\" key in \"ShapeWriter keyboard settings\" to access this feature.\nWhen you " +
				"first run ShapeWriter keyboard, your phone's " +
				"user dictionary and contacts are automatically imported into ShapeWriter's user " +
				"dictionary. \n\n" + 
				"Double Click\n\n" +
				"Double click the \"Space\" key, you will send one period and space.\n" +
				"Double click the \"Backspace\" key, you will delete a whole word around the cursor.\n");
		
		Button lauchSettings = new Button(this);
		lauchSettings.setText("ShapeWriter Keyboard Settings");
		lauchSettings.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SWI_StartPage.this, SWI_IMESettings.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
        });
	        
		layoutNew.addView(newTab);
		layoutNew.addView(lauchSettings);
		tv3.addView(layoutNew);
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Welcome")
				.setContent(R.id.view1));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Links")
				.setContent(R.id.view2));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Change Log")
				.setContent(R.id.view3));
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
