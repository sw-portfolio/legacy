package comuiappcenter.facebook.m.legacy.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import comuiappcenter.facebook.m.legacy.R;

/**
 * Created by Administrator on 2017-01-01.
 */
public class defaultButton extends RelativeLayout
{

    public defaultButton(Context context)
    {
        super(context);
        initialize();
    }

    public defaultButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public void initialize()
    {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        View v = inflater.inflate(R.layout.default_button_layout, this, false);
        addView(v);
    }

}
