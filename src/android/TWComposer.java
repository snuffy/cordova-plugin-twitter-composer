package jp.snuffy;

import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.*;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.core.internal.TwitterApi;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import android.content.BroadcastReceiver;

import org.apache.cordova.LOG;
import org.jdeferred2.Deferred;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

import com.google.gson.Gson;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import org.jdeferred2.*;

import android.util.Log;

public class TWComposer extends CordovaPlugin {
  private String action;
  private CallbackContext callbackContext;
  private static final String LOG_TAG = "Twitter Connect";

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {

    TwitterConfig config = new TwitterConfig.Builder(cordova.getActivity().getApplicationContext())
        .twitterAuthConfig(new TwitterAuthConfig(getTwitterKey(), getTwitterSecret()))
        .build();
    Twitter.initialize(config);

    // set braodcast receiver;
    BroadcastReceiver br = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

        if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
          callbackContext.success("tweet!");
        } else if (TweetUploadService.UPLOAD_FAILURE.equals(intent.getAction())) {
          // failure
          callbackContext.error("may be same tweet");
        } else if (TweetUploadService.TWEET_COMPOSE_CANCEL.equals(intent.getAction())) {
          // cancel
          callbackContext.success("cancel");
        }
      }
    };

    IntentFilter filter = new IntentFilter("com.twitter.sdk.android.tweetcomposer.UPLOAD_SUCCESS");
    filter.addAction("com.twitter.sdk.android.tweetcomposer.UPLOAD_FAILURE");
    filter.addAction("com.twitter.sdk.android.tweetcomposer.TWEET_COMPOSE_CANCEL");

    this.cordova.getActivity().registerReceiver(br, filter);
  }

  public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    this.action = action;
    final Activity activity = this.cordova.getActivity();
    final Context context = activity.getApplicationContext();
    this.callbackContext = callbackContext;

    if (action.equals("compose")) {

      String text = args.get(0).toString();;
      String url = args.get(1).toString();

      cordova.setActivityResultCallback(this);

      if (url == "null") {
        Uri imageUri = null;
        composeTweet(text, imageUri);
      }
      else {
        // download from URL; convert to cache uri;
        downloadImage(url).done(new DoneCallback() {
          @Override
          public void onDone(Object result) {
            Uri imageUri = (Uri)result;
            composeTweet(text, imageUri);
          }
        }).fail(new FailCallback() {
          @Override
          public void onFail(Object rejection) {
              Uri imageUri = null;
              composeTweet(text, imageUri);
          }
        });
      }

      return true;
    }
    else if (action.equals("logout")) {
      logoutWithTwitter(activity, callbackContext);
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    if (action.equals("compose")) {
      handleTwitterLoginResult(requestCode, resultCode, intent);
    }
  }

  private String getTwitterKey() {
    return preferences.getString("TwitterConsumerKey", "");
  }

  private String getTwitterSecret() {
    return preferences.getString("TwitterConsumerSecret", "");
  }

  private Promise downloadImage(String imageURL) {

    Deferred deferred = new DeferredObject();
    Promise promise = deferred.promise();

    Bitmap bmp = null;

    HttpURLConnection urlConnection = null;

    try {
      URL url = new URL(imageURL);
      
      // set URL
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setReadTimeout(10000);
      urlConnection.setConnectTimeout(20000);

      // request
      urlConnection.setRequestMethod("GET");
      urlConnection.setInstanceFollowRedirects(false);
      
      // connect
      urlConnection.connect();
      
      // case
      int res = urlConnection.getResponseCode();

      switch (res) {
        case HttpURLConnection.HTTP_OK:
          InputStream in = null;
          try {

            // get bitmap
            in = new java.net.URL(imageURL).openStream();
            bmp = BitmapFactory.decodeStream(in);

            // get URI
            File tempDir = cordova.getContext().getCacheDir();
            tempDir=new File(tempDir.getAbsolutePath() + "/.temp");
            tempDir.mkdir();

            try {
              File tempFile = File.createTempFile("TempTweetImage", ".jpg", tempDir);
              ByteArrayOutputStream bytes = new ByteArrayOutputStream();
              bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
              byte[] bitmapData = bytes.toByteArray();

              //write the bytes in file
              FileOutputStream fos = new FileOutputStream(tempFile);
              fos.write(bitmapData);
              fos.flush();
              fos.close();
              Uri imageUri = Uri.fromFile(tempFile);

              deferred.resolve(imageUri);
            }
            catch (IOException e) {
              deferred.reject(e);
            }

          }
          catch (IOException e) {
            e.printStackTrace();
            deferred.reject(e);
          }
          finally {
            if (in != null) {
              in.close();
            }
          }
          break;
        case HttpURLConnection.HTTP_UNAUTHORIZED:
          break;
        default:
          break;
      }
    } 
    catch (Exception e) {
      Log.d("debug", "downloadImage error");
      e.printStackTrace();
      deferred.reject(e);
    }
    finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }

    }
    return promise;
  }

  private void composeTweet(String text, Uri imageUri) {

    Activity activity = cordova.getActivity();
    final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

    // if login ?
    if (session != null) {
      final Intent composerIntent = new ComposerActivity.Builder(activity).session(session).text(text).image(imageUri).createIntent();
      cordova.getActivity().startActivity(composerIntent);
    }

    // if not login, login and compose;
    else {
      loginWithTwitter(activity, callbackContext).then(new DoneCallback() {
        @Override
        public void onDone(Object result) {

          final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
          final Intent composerIntent = new ComposerActivity.Builder(activity).session(session).text(text).image(imageUri).createIntent();
          cordova.getActivity().startActivity(composerIntent);
        }
      }).fail(new FailCallback() {
        @Override
        public void onFail(Object result) {
          callbackContext.error("Failed login session.");
        }
      });
    }

  }


  private Promise loginWithTwitter(final Activity activity, final CallbackContext callbackContext) {

      Deferred deferred = new DeferredObject();
      Promise promise = deferred.promise();

      TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
      twitterAuthClient.authorize(activity, new Callback<TwitterSession>() {

        @Override
        public void success(final Result<TwitterSession> loginResult) {

          Result hoge = loginResult;
          Log.v(LOG_TAG, "Failed login session.");
          callbackContext.error("Failed login session.");

          deferred.resolve(hoge);

        }

        @Override
        public void failure(final TwitterException e) {
          Log.v(LOG_TAG, "Failed login session.");
          deferred.reject(e);
        }
      });

      return promise;
  }


  private void logoutWithTwitter(final Activity activity, final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        TwitterCore twitterCore = TwitterCore.getInstance();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        callbackContext.success("logout");
      }
    });
  }


  private void handleTwitterLoginResult(int requestCode, int resultCode, Intent intent) {
    TwitterLoginButton twitterLoginButton = new TwitterLoginButton(cordova.getActivity());
    twitterLoginButton.onActivityResult(requestCode, resultCode, intent);
  }

}


