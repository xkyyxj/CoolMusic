package com.run.coolmusic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.run.Adapter.LocalMusicListAdapter;
import com.run.Bean.LocalMusic;
import com.run.Services.LocalMusicGetter;
import com.run.Services.PlayService;

import java.io.IOException;
import java.util.List;

public class LocalMusicActivity extends Activity {

    public static final String LOCAL_MUSIC_ACTIVITY_TAG = "LocalMusicActivity:";

    private PlayService playService = null;
    private List<LocalMusic> localMusicList = null;

    private ListView musicList;
    private TextView title,music_name;
    private Button return_button;
    private ImageView music_icon;
    private ImageButton playing,next;
    private SeekBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_local_music_list);
        init();
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

    /*
    * 查找加载View组件
    * 由Android Studio插件完成
    * */
    private void assignViews() {
        musicList = (ListView) findViewById(R.id.music_list);
        title = (TextView) findViewById(R.id.header_title);
        return_button = (Button) findViewById(R.id.header_return_button);
        playing = (ImageButton) findViewById(R.id.playing);
        next = (ImageButton) findViewById(R.id.next_music);
        music_name = (TextView) findViewById(R.id.music_name);
        progress = (SeekBar) findViewById(R.id.playing_progress);
        music_icon = (ImageView) findViewById(R.id.music_icon);
    }

    /*
    * 初始化工作
    * 初始化播放器服务PlayService
    * 初始化本地音乐列表localMusicList
    * 待完成工作：加载本地音乐时需要一个加载提示
    * */
    private void init()
    {
        //初始化音乐播放服务
        playService = new PlayService();
        //Application保存了一份本地音乐列表，从中取出
        CoolMusicApplication application = (CoolMusicApplication)getApplication();
        LocalMusicGetter localMusicGetter = application.getLocalMusicList();
        //若Application没有保存本地音乐列表，则向Application中注入一份，全局保存
        if(localMusicGetter == null) {
            localMusicGetter = new LocalMusicGetter(this);
            application.setLocalMusicList(localMusicGetter);
        }
        localMusicList = localMusicGetter.getMusicInfo();
        Log.e(CoolMusicApplication.COOL_MUSIC_TAG, "" + localMusicList.size());
        initView();
    }

    //初始化视图组件
    private void initView()
    {
        assignViews();
        //PlayBar 播放按钮设置背景图片
        playing.setImageResource(R.mipmap.play_button);
        //设置标题栏
        title.setText(R.string.local_music_title);
        //为各个组件设置监听
        //滑动条监听
        progress.setOnSeekBarChangeListener(new MySeekBarListener());
        //列表监听
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickToPlay(position);
            }
        });
        //各个按钮监听
        LocalMusicButtonListener buttonListener = new LocalMusicButtonListener();
        return_button.setOnClickListener(buttonListener);
        playing.setOnClickListener(buttonListener);
        next.setOnClickListener(buttonListener);
        //为代表本地音乐列表的ListView设置Adapter
        musicList.setAdapter(new LocalMusicListAdapter(this ,localMusicList));
    }

    //用户点击列表某一项然后播放对应音乐
    private void clickToPlay(int position)
    {
        //获取对应音乐Bean
        LocalMusic temp = localMusicList.get(position);
        //设置playBar的相关UI信息
        music_name.setText(temp.getMusicNameString());
        //TODO 设置音乐的图片信息，也就是musicIcon
        //试图播放音乐
        try {
            playService.play(temp.getMusicPathString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //PlayBar 播放按钮背景图片切换 R.mipmap.play_button -> R.mipmap.pause
        playing.setImageResource(R.mipmap.pause);
    }

    //音乐播放滑动条监听器
    class MySeekBarListener implements SeekBar.OnSeekBarChangeListener
    {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    //LocalMusicActivity按钮监听器（公用）
    class LocalMusicButtonListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch(id)
            {
                //标题栏返回按钮
                case R.id.header_return_button:
                    finish();
                    break;
                //播放栏播放\暂停按钮
                case R.id.playing:
                    boolean tempState = playService.isPlaying();
                    if(tempState) {
                        playService.pause();
                        playing.setImageResource(R.mipmap.play_button);
                    }
                    else {
                        playService.reStart();
                        playing.setImageResource(R.mipmap.pause);
                    }
                    break;
                //播放栏下一首音乐按钮
                case R.id.next_music:
                    //TODO 下一首音乐的逻辑实现：列表循环、随机播放等
                    break;
            }
        }
    }

}
