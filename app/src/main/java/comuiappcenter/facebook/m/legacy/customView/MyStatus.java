package comuiappcenter.facebook.m.legacy.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import comuiappcenter.facebook.m.legacy.R;

/**
 * Created by Administrator on 2/23/2017.
 */

public class MyStatus extends RelativeLayout
{
    public ImageView mImage;
    public TextView point;
    public TextView statistic1;

    public MyStatus(Context context)
    {
        super(context);
        setDefault();
    }

    public MyStatus(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setDefault();
    }

    private void setDefault()
    {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        View v = inflater.inflate(R.layout.my_status, this, false);
        addView(v);
        mImage = (ImageView) findViewById(R.id.imageView_my_status);
        point = (TextView) findViewById(R.id.textView_my_status);

    }
}
