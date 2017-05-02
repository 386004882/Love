import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by jiji on 2017/4/14.
 */

public class MyActivity extends Activity {
    public static Context context;

    public MyActivity(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
