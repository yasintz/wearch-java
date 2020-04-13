package com.example.wearch.youtube.video.Fetch;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wearch.lib.STATIC;
import com.example.wearch.lib.store.IEventHandler;
import com.example.wearch.lib.store.NState;
import com.example.wearch.lib.store.Store;
import com.example.wearch.youtube.caption.Caption;
import com.example.wearch.youtube.video.YouTubeVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Random;

public class YoutubeFetch {
  private final RequestQueue _queue;
  private ArrayList<String> _videoIds;
  private Store _store = Store.getInstance();
  private NState<Integer> fetchCount = new NState<>(0);
  private boolean isStartFirstFetch = false;

  public YoutubeFetch(Context ctx) {
    _videoIds = new ArrayList<>(STATIC.VIDEOS);
    _queue = Volley.newRequestQueue(ctx);
  }

  private void get(final String videoId) {
    String url = STATIC.CRUDE_URL + videoId;
    StringRequest _stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        JSONObject jsonObject;
        ArrayList<Caption> captions = new ArrayList<>();
        try {
          jsonObject = XML.toJSONObject(response);
          JSONArray captionArr = jsonObject.getJSONObject("transcript").getJSONArray("text");
          for (int i = 0; i < captionArr.length(); i++) {
            JSONObject captionObj = captionArr.getJSONObject(i);
            Double start = Double.parseDouble(captionObj.getString("start"));
            Double dur = Double.parseDouble(captionObj.getString("dur"));
            String content = STATIC.htmlUnascape(captionObj.getString("content"));
            Caption caption = new Caption(start, dur, content);
            captions.add(caption);
          }
        } catch (JSONException ignored) {
        }
        YouTubeVideo video = new YouTubeVideo(videoId, captions);
        _store.youTubeVideos.addState(video);
        fetchCount.setState(fetchCount.getState() + 1);
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {

      }
    });
    _queue.add(_stringRequest);
  }

  private void startFetch(final int size) {
    _store.isFetchStart.setState(true);
    fetchCount.setState(0);
    fetchCount.addEventListener(new IEventHandler<Integer>() {
      @Override
      public void callback(Integer val, Runnable end) {
        if (val == size) {
          _store.isFetchStart.setState(false);
          end.run();
        }
      }
    });
    ArrayList<String> sublist = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      if (_videoIds.size() > 0) {
        int randomIndex = new Random().nextInt(_videoIds.size());
        sublist.add(_videoIds.get(randomIndex));
        _videoIds.remove(randomIndex);
      }
    }
    for (String videoId : sublist) {
      get(videoId);
    }
  }

  public void startNextFetch() {
    if (!_videoIds.isEmpty()) {
      if (!isStartFirstFetch) {
        startFetch(14);
        isStartFirstFetch = true;
      } else {
        startFetch(9);
      }
    }
  }
}
