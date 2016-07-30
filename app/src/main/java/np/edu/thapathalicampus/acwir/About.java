package np.edu.thapathalicampus.acwir;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class About extends Activity {

    TextView sus,sub,sag,dev,col;
    ImageView iv;
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        /*sus = (TextView) findViewById(R.id.iSushil);
        sub = (TextView) findViewById(R.id.iSubash);
        sag = (TextView) findViewById(R.id.iSagar);
        dev = (TextView) findViewById(R.id.idevelopers);
        col = (TextView) findViewById(R.id.iCollege);
        iv= (ImageView)findViewById(R.id.imageView);

        back = (Button)findViewById(R.id.ibutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(About.this, MainActivity.class));
            }
        });*/

    }


}
