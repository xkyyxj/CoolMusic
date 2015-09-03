package com.run.Services;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Albert on 2015/8/27.
 */
public class PlayService {

    private boolean isPlaying = false;
    MediaPlayer player = null;

    public PlayService()
    {
        player = new MediaPlayer();
    }

    public void play(String filePathString) throws IOException {
        if(player == null) {
            player = new MediaPlayer();
        }
        if(isPlaying)
        {
            //正在播放中的时候，需要将MediaPlayer重置，否则播放新音乐的时候回出现错误
            player.stop();
            player.reset();
        }
        //重新设置数据源
        player.setDataSource(filePathString);
        player.prepare();
        player.start();
        //设置播放状态
        isPlaying = true;
    }

    public void stop() {
        player.stop();
        isPlaying = false;
    }

    public void pause()
    {
        player.pause();
    }

    public void reStart(){
        if(player != null)
            player.start();
//        else
            //player被释放之后的处置措施
            //重建，并且应当保存上一次播放的歌曲信息以及播放时长
        isPlaying = true;
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }

}
