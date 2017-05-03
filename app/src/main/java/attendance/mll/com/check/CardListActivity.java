package attendance.mll.com.check;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import attendance.mll.com.check.http.Net;
import attendance.mll.com.check.utils.AudioPlayer;
import attendance.mll.com.check.utils.T;

/**
 * Created by Administrator on 2017/5/2.
 */
public class CardListActivity extends Activity {

    public static final int REFRESH_INTERVAL = 3000;

    TextView tvTitle;

    XRecyclerView lv;
    Adapter adapter;

    Handler handler;
    CardTimes cardTimes;
    ProgressBar pb;

    List<String> soundEffects = new ArrayList<>();
    List<Date> playedList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        lv = (XRecyclerView) findViewById(R.id.lv);
        pb = (ProgressBar) findViewById(R.id.pb);

        SoundListActivity.initSoundEffectList(this, soundEffects);

        adapter = new Adapter();

        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setAdapter(adapter);
        lv.setPullRefreshEnabled(true);
        lv.setLoadingMoreEnabled(false);
        lv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                handler.post(refreshRunnable);
            }

            @Override
            public void onLoadMore() {

            }
        });
        handler = new Handler();
        handler.post(refreshRunnable);

        T.show(String.format("每 %d 种自动刷新一次,用完请返回退出", REFRESH_INTERVAL));

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    void playVoice(Date date) {
        if (playedList.contains(date)) return;
        playedList.add(date);

        AudioPlayer player = new AudioPlayer() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        };

        String sound = getSharedPreferences(Constants.PREF_, MODE_PRIVATE).getString(Constants.PREF_SOUND, "");
        if (!soundEffects.contains(sound)) {
            sound = soundEffects.get(1);
        }

        // player.playAsset(this, sound);
        SoundListActivity.playVoice(CardListActivity.this, player, pb, sound);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        refreshRunnable = null;
        T.show("已停止自动刷新");
        UserContext.get().logout();
    }

    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (refreshRunnable != null) handler.removeCallbacks(refreshRunnable);

            pb.setVisibility(View.VISIBLE);
            Net.cardTimes(new Net.CardTimesHandler() {
                @Override
                public void onCardTimeGet(CardTimes cardTimes) {
                    lv.refreshComplete();
                    pb.setVisibility(View.GONE);
                    CardListActivity.this.cardTimes = cardTimes;
                    if (cardTimes != null && cardTimes.getRows() != null && !cardTimes.getRows().isEmpty()) {
                        tvTitle.setText(UserContext.get().getWelcome());
                    }
                    adapter.notifyDataSetChanged();
                    if (refreshRunnable != null)
                        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
                }

                @Override
                public void onFailed(String description) {
                    lv.refreshComplete();
                    pb.setVisibility(View.GONE);
                    if (refreshRunnable != null)
                        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
                }
            });
        }
    };


    // SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private String[] weeks = {
            "星期日",
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六",
    };

    class Adapter extends RecyclerView.Adapter<ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(getBaseContext(), R.layout.adapter_card, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData(getItem(position));
        }

        @Override
        public int getItemCount() {
            return cardTimes == null || cardTimes.getRows() == null ? 0 : cardTimes.getRows().size();
        }

        public CardTimes.Row getItem(int position) {
            return cardTimes.getRows().get(getItemCount() - 1 - position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv; // = (TextView) v.findViewById(R.id.tv);
        TextView tvWeek; // = (TextView) v.findViewById(R.id.tv_week);

        private CardTimes.Row row;

        public ViewHolder(View v) {
            super(v);
            tv = (TextView) v.findViewById(R.id.tv);
            tvWeek = (TextView) v.findViewById(R.id.tv_week);
            v.setOnClickListener(this);
        }

        void bindData(CardTimes.Row row) {
            this.row = row;
            // CardTimes.Row row = getItem(position);

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (int i = row.getCardTimes().size() - 1; i >= 0; i--) {
                Date date = row.getCardTimes().get(i);
                int len = builder.length();

                builder.append(format2.format(date)).append("\n");
                calendar.setTime(date);

                if (System.currentTimeMillis() - date.getTime() < 5 * 60000) { // 60 * 24 * 4
                    builder.setSpan(new ForegroundColorSpan(0xffff0000), len, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    playVoice(date);
                }
            }
            builder.delete(builder.length() - 1, builder.length() - 1);


            tv.setText(builder);

            // 星期几
            int week = calendar.get(Calendar.DAY_OF_WEEK);

            tvWeek.setText(weeks[week - 1]);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
