package attendance.mll.com.check;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import attendance.mll.com.check.utils.AudioPlayer;
import attendance.mll.com.check.utils.T;

/**
 * Created by Administrator on 2017/5/2.
 */
public class SoundListActivity extends Activity {

    XRecyclerView lv;
    Adapter adapter;

    ProgressBar pb;

    List<String> soundEffects = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        lv = (XRecyclerView) findViewById(R.id.lv);
        pb = (ProgressBar) findViewById(R.id.pb);

        pb.setVisibility(View.GONE);

        initSoundEffectList(this, soundEffects);

        adapter = new Adapter();

        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setAdapter(adapter);
        lv.setPullRefreshEnabled(false);
        lv.setLoadingMoreEnabled(false);

        findViewById(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static void initSoundEffectList(Context context, List<String> musicList) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] musics = assetManager.list("sound");
            musicList.addAll(Arrays.asList(musics));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void playVoice(Context context, AudioPlayer player, View prog, String name) {
        player.stop();
        prog.setVisibility(View.VISIBLE);
        player.playAsset(context, "sound/" + name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    AudioPlayer player = new AudioPlayer() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            pb.setVisibility(View.GONE);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            pb.setVisibility(View.GONE);
            return false;
        }
    };

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(getBaseContext(), R.layout.adapter_music, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData(getItem(position));
        }

        @Override
        public int getItemCount() {
            return soundEffects.size();
        }

        public String getItem(int position) {
            return soundEffects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv; // = (TextView) v.findViewById(R.id.tv);
        String name;

        public ViewHolder(View v) {
            super(v);
            tv = (TextView) v.findViewById(R.id.tv_name);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSharedPreferences(Constants.PREF_, MODE_PRIVATE)
                            .edit()
                            .putString(Constants.PREF_SOUND, name)
                            .apply();
                    T.show("音效已设置为:" + name);
                    playVoice(SoundListActivity.this, player, pb, name);
                }
            });
        }

        void bindData(String name) {
            this.name = name;
            tv.setText(name);
        }

    }
}
