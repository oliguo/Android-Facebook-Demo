package oliguo.example.loginmanager.customizedbutton;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    List<String> permissionNeeds= Arrays.asList("user_friends", "email", "user_birthday");
    private Button loginButton;
    private TextView connectedStateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        loginButton=(Button)findViewById(R.id.login_button);
        float fbIconScale = 1.45F;
        Drawable drawable = getResources().getDrawable(
                com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        loginButton.setCompoundDrawables(drawable, null, null, null);
        loginButton.setCompoundDrawablePadding(getResources().
                getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        loginButton.setPadding(
                getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_lr),
                getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_top),
                0,
                getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_bottom));
        connectedStateLabel=(TextView)findViewById(R.id.profile);
        callbackManager = CallbackManager.Factory.create();

        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(connectedStateLabel.getText()==getResources().getString(R.string.hello_world)
                        ||connectedStateLabel.getText()=="facebook login canceled"
                        ||connectedStateLabel.getText()=="facebook login failed error") {
                    LoginManager.getInstance().logInWithReadPermissions(
                            MainActivity.this, permissionNeeds);
                }else{
                    LoginManager.getInstance().logOut();
                    connectedStateLabel.setText(R.string.hello_world);
                    loginButton.setText(R.string.login);
                }
            }
        });

//                LoginManager.getInstance().logInWithReadPermissions(
//                        this ,permissionNeeds);

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResults) {

                                GraphRequest request = GraphRequest.newMeRequest(
                                        loginResults.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(
                                                    JSONObject object,
                                                    GraphResponse response) {
                                                // Application code
                                                loginButton.setText(R.string.logout);
                                                connectedStateLabel.setText(response.toString());
                                                Log.d("dd", response.toString());
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,gender, birthday");
                                request.setParameters(parameters);
                                request.executeAsync();

                            }

                            @Override
                            public void onCancel() {
                                connectedStateLabel.setText("facebook login canceled");
                                        Log.d("dd", "facebook login canceled");

                            }


                            @Override
                            public void onError(FacebookException e) {

                                connectedStateLabel.setText("facebook login failed error");
                                Log.d("dd", "facebook login failed error");

                            }
                        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
