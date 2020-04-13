package com.example.wearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.example.wearch.lib.SetInterval;
import com.example.wearch.lib.eventbus.EventBus;
import com.example.wearch.lib.store.Store;
import com.example.wearch.lib.store.IEventHandler;
import com.example.wearch.lib.STATIC;
import com.example.wearch.youtube.caption.Caption;
import com.example.wearch.youtube.caption.search.Search;
import com.example.wearch.youtube.video.CorrectVideo;
import com.example.wearch.youtube.video.Fetch.YoutubeFetch;
import com.example.wearch.youtube.video.YouTubeVideo;
import com.example.wearch.youtube.video.YoutubeConfig;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import com.example.wearch.youtube.video.YouTubeController;

import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

  private final int MIN_KEYBOARD_HEIGHT_PX = 150;

  private Button nextButton, prevButton, restartButton;
  private TextView correctVideoSizeText, correctVideoCurrentIndexText, youtubeCaption, youtubeVideoSizeText;
  private YouTubePlayerSupportFragment youTubePlayerFragment;
  private MenuItem searchButton;
  private SearchView searchView;
  private ConstraintLayout wrapperLayout;
  private ProgressBar progressBar;
  private AlertDialog.Builder builder;
  private Toolbar toolbar;
  private View decorView;

  private YouTubeController youTubeController;
  private Store store;
  private Search search;
  private YoutubeFetch fetch;
  private SetInterval setInterval;
  private EventBus eventBus;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getViewFromXml();
    createInstance();
    setViewSettings();
    if (!isNetworkAvailable()) {
      builder.show();
    } else {
      setEventBus();
      setUIListeners();
      setStoreListener();
      setInterval();
      fetch.startNextFetch();
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    store.reset();
    setInterval.stopIfNotStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_menu, menu);
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    searchButton = menu.findItem(R.id.action_search);
    searchView = (SearchView) searchButton.getActionView();
    searchView.findViewById(android.support.v7.appcompat.R.id.search_plate).setBackgroundColor(Color.TRANSPARENT);
    ImageView searchViewIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
    ViewGroup linearLayoutSearchView = (ViewGroup) searchViewIcon.getParent();
    linearLayoutSearchView.removeView(searchViewIcon);
    assert searchManager != null;
    searchView.setSearchableInfo(searchManager
            .getSearchableInfo(getComponentName()));
    searchView.setIconifiedByDefault(false);

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(final String s) {
        if (s.length() <= STATIC.MAX_TEXT_SIZE) {
          store.youTubeStore.setInitialValue();
          store.enteredText.setInitialValue();
          store.playingVideoIndex.setInitialValue();
          searchButton.collapseActionView();
          fetch.startNextFetch();
          store.isFetchStart.addEventListener(new IEventHandler<Boolean>() {
            @Override
            public void callback(Boolean val, Runnable end) {
              if (!val) {
                store.correctVideos.setInitialValue();
                store.searchedText.setState(s);
                startToNextVideoInCorrect(1);
                end.run();
              }
            }
          });
        } else {
          Toast.makeText(MainActivity.this, STATIC.MAX_TEXT_WARNING_MESSAGES, Toast.LENGTH_SHORT).show();
        }
        return true;
      }

      @Override
      public boolean onQueryTextChange(String s) {
        return true;
      }
    };
    // init keyboard
    decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      private final Rect windowVisibleDisplayFrame = new Rect();
      private int lastVisibleDecorViewHeight;

      @Override
      public void onGlobalLayout() {
        decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
        final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

        if (lastVisibleDecorViewHeight != 0) {
          if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
            // Show Keyboard
            searchView.setQuery(store.enteredText.getState(), false);
          } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
            // Hide Keyboard
            store.enteredText.setState(searchView.getQuery().toString());
            searchButton.collapseActionView();
          }
        }
        lastVisibleDecorViewHeight = visibleDecorViewHeight;
      }
    });
    searchView.setOnQueryTextListener(queryTextListener);
    store.isMenuCreated.setState(true);
    return true;
  }

  private void getViewFromXml() {
    decorView = getWindow().getDecorView();
    wrapperLayout = findViewById(R.id.wrapper_layout);
    nextButton = findViewById(R.id.next);
    prevButton = findViewById(R.id.prev);
    restartButton = findViewById(R.id.restart);
    correctVideoSizeText = findViewById(R.id.correct_video_count);
    youtubeVideoSizeText = findViewById(R.id.youtube_video_count);
    correctVideoCurrentIndexText = findViewById(R.id.correct_video_current_index);
    youtubeCaption = findViewById(R.id.video_caption);
    toolbar = findViewById(R.id.toolbar);
    progressBar = findViewById(R.id.progressBar);
    youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
            .findFragmentById(R.id.youtube_fragment);
    builder = new AlertDialog.Builder(MainActivity.this);
  }


  private void createInstance() {

    youTubeController = new YouTubeController();
    store = Store.getInstance();
    search = new Search();
    fetch = new YoutubeFetch(this);
    youTubePlayerFragment.initialize(YoutubeConfig.API_KEY, youTubeController);
    eventBus = EventBus.getInstance();
  }

  private void setViewSettings() {
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setTitle(null);
    builder.setMessage(STATIC.BUILDER_MESSAGE);
    builder.setTitle(STATIC.BUILDER_TITTLE);
    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        System.exit(0);
      }
    });
    progressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
  }

  private void setUIListeners() {
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fetch.startNextFetch();
        startToNextVideoInCorrect(1);
      }
    });
    prevButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fetch.startNextFetch();
        startToNextVideoInCorrect(-1);
      }
    });
    restartButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fetch.startNextFetch();
        startToNextVideoInCorrect(0);
      }
    });

  }

  private void setStoreListener() {
    store.correctVideos.addEventListener(new IEventHandler<ArrayList<CorrectVideo>>() {
      @Override
      public void callback(ArrayList<CorrectVideo> val, Runnable end) {
        correctVideoSizeText.setText(Integer.toString(val.size()));
      }
    });
    store.isLoadingShown.addEventListener(new IEventHandler<Boolean>() {
      @Override
      public void callback(final Boolean val, Runnable end) {
        //region -- is loading shown content --
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            final float alpha;
            final Drawable icon;
            if (val) {
              alpha = 0.3f;
              icon = getResources().getDrawable(R.drawable.ic_search_gray);
              progressBar.setVisibility(View.VISIBLE);
            } else {
              alpha = 1f;
              icon = getResources().getDrawable(R.drawable.ic_search_blue);
              progressBar.setVisibility(View.INVISIBLE);
            }


            for (int i = 0; i < wrapperLayout.getChildCount(); i++) {
              View child = wrapperLayout.getChildAt(i);
              if (child.getId() != R.id.progressBar) {
                child.setEnabled(!val);
                child.setAlpha(alpha);
              }
            }
            if (store.isMenuCreated.getState()) {
              searchButton.setEnabled(!val);
              searchButton.setIcon(icon);
            }
          }
        });
        //endregion
      }
    });
    store.isFetchStart.addEventListener(new IEventHandler<Boolean>() {
      @Override
      public void callback(Boolean val, Runnable end) {
        if (val) {
          store.isLoadingShown.setState(true);
        } else {
          store.isLoadingShown.setState(false);
        }
      }
    });
    store.youTubeStore.currentMillis.addEventListener(new IEventHandler<Integer>() {
      @Override
      public void callback(final Integer val, Runnable end) {
        int playingVideoIndex = store.playingVideoIndex.getState();
        final int valCustom = val + 220;
        if (playingVideoIndex >= 0) {
          final CorrectVideo playingVideoInfo = store.correctVideos.getState().get(playingVideoIndex);
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Caption captionRef = null;
              for (Caption caption : playingVideoInfo.captions) {
                if (valCustom >= caption.start && valCustom <= caption.end) {
                  captionRef = caption;
                }
              }
              if (captionRef != null) {
                youtubeCaption.setText(captionRef.text);
              }
            }
          });
        }
      }
    });
    store.playingVideoIndex.addEventListener(new IEventHandler<Integer>() {
      @Override
      public void callback(Integer val, Runnable end) {
        correctVideoCurrentIndexText.setText(Integer.toString(val + 1));
      }
    });
    store.searchedText.addEventListener(new IEventHandler<String>() {
      @Override
      public void callback(String val, Runnable end) {
        if (!val.isEmpty() && store.youTubeVideos.getState().size() > 0) {
          search.againSearch();
        }
      }
    });
    store.youTubeVideos.addEventListener(new IEventHandler<ArrayList<YouTubeVideo>>() {
      @Override
      public void callback(ArrayList<YouTubeVideo> val, Runnable end) {
        youtubeVideoSizeText.setText(String.format(Locale.US, "%03d", val.size()));
      }
    });
    store.isFetchStart.addEventListener(new IEventHandler<Boolean>() {
      @Override
      public void callback(Boolean val, Runnable end) {
        if (store.youTubeVideos.getState().size() > 0 && !store.searchedText.getState().isEmpty()) {
          if (!val) {
            search.againSearch();
          }
        }
      }
    });
  }

  private void setEventBus() {
    eventBus.on("youtube-player-initialization-success", new Runnable() {
      @Override
      public void run() {
        store.youTubeStore.playingVideoId.addEventListener(new IEventHandler<String>() {
          @Override
          public void callback(String val, Runnable end) {
            if (!val.isEmpty()) {
              youTubeController.loadVideo();
            }
          }
        });
      }
    });
  }

  private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager
            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connectivityManager != null;
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  private void setInterval() {
    setInterval = new SetInterval(new Runnable() {
      @Override
      public void run() {
        if (youTubePlayerFragment.isAdded() && youTubePlayerFragment.isInLayout() && youTubePlayerFragment.isVisible() && youTubePlayerFragment.isResumed()) {
          if(store.youTubeStore.isReady.getState()){
            store.youTubeStore.currentMillis.setState(youTubeController.getCurrentTimeMillis());
          }
        }
      }
    }, 0, 300);
    setInterval.startIfNotStart();

  }

  private void startToNextVideoInCorrect(int index) {
    ArrayList<CorrectVideo> val = store.correctVideos.getState();
    int newIndex = store.playingVideoIndex.getState() + index;
    if (val.size() > 0 && newIndex < val.size() && newIndex >= 0) {
      final CorrectVideo item = val.get(newIndex);
      final ArrayList<Caption> captions = val.get(newIndex).captions;
      store.youTubeStore.playingVideoId.setState(item.videoId);
      store.playingVideoIndex.setState(newIndex);
      store.youTubeStore.isReady.addEventListener(new IEventHandler<Boolean>() {
        @Override
        public void callback(Boolean val, Runnable end) {
          if (val) {
            Double ms = captions.get(item.sentencePos).start - 600;
            youTubeController.seekToMillis(ms.intValue());
            youTubeController.play();
            end.run();
          }
        }
      });
    } else {
      if (store.youTubeStore.isPlaying.getState()) {
        youTubeController.pause();
      }
    }
  }
}
