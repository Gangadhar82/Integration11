package com.mmadapps.integration11;

/**
 * Created by gangadhar.g on 10/8/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    Context c;

    private CallbackManager callbackManager;
    private TextView textView, textView1, textView2,textView3;
    ImageLoader imageLoader;
    DisplayImageOptions options;


    private ImageView imageView;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            Log.d("jsonResult", loginResult + "");
            final GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    JSONObject json = graphResponse.getJSONObject();

                    try {
                        if (json != null) {
                            String text = json.getString("email");
                            String token = loginResult.getAccessToken().getToken();
                            Log.d("email", text);
                            textView2.setText("Email:" + text);
                            textView3.setText("Access Token::"+token);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email,picture");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException exception) {

        }
    };



    public MainFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
        try {
            options = new DisplayImageOptions.Builder()
                    .showStubImage(R.mipmap.ic_launcher)
                    .showImageForEmptyUri(R.mipmap.ic_launcher)
                    .showImageOnFail(R.mipmap.ic_launcher).cacheInMemory()
                    .cacheOnDisc().bitmapConfig(Bitmap.Config.ARGB_8888)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        textView = (TextView) view.findViewById(R.id.name);
       textView1=(TextView)view.findViewById(R.id.id);
        textView2=(TextView)view.findViewById(R.id.Emailid);
        textView3=(TextView)view.findViewById(R.id.Accesstoken);

        imageView=(ImageView)view.findViewById(R.id.imageurl);

        loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions("email");

        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);


        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void displayMessage(Profile profile){
        if(profile != null){
            textView.setText("Name:"+profile.getName());
           textView1.setText("User Id:"+ profile.getId());
            //user.getProperty("email");
            String url = ""+profile.getProfilePictureUri(200, 200);
            imageLoader.displayImage(url, imageView, options);

           Log.e("imageview", url);
            //Log.i(TAG, "Access Token" + session.getAccessToken());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Session session = Session.getActiveSession();
//        if (session.isOpened()) {
//            Toast.makeText(getActivity(), session.getAccessToken(), Toast.LENGTH_LONG).show();
//        }
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);

    }


}

