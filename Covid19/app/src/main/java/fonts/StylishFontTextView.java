package fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class StylishFontTextView extends AppCompatTextView {

	private static Typeface LeagueGothicTextView;

	public StylishFontTextView(Context context) {
		super(context);
		if (isInEditMode()) return; //Won't work in Eclipse graphical layout
		setTypeface();
	}

	public StylishFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) return;
		setTypeface();
	}

	public StylishFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) return;
		setTypeface();
	}
	
	private void setTypeface() {
		if (LeagueGothicTextView == null) {
			LeagueGothicTextView = Typeface.createFromAsset(getContext().getAssets(), "fonts/font.ttf");
		}
		setTypeface(LeagueGothicTextView);
	}
}
