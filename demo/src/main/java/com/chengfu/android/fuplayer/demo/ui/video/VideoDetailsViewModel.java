package com.chengfu.android.fuplayer.demo.ui.video;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;

import com.chengfu.android.fuplayer.demo.bean.Resource;
import com.chengfu.android.fuplayer.demo.bean.Video;
import com.chengfu.android.fuplayer.demo.util.VideoUtil;

import android.os.Handler;

public class VideoDetailsViewModel extends ViewModel {

    private Handler handler;
    private final MediatorLiveData<Resource<Video>> videoResource;
    private MutableLiveData<Video> videoSource;

    private String id;

    public VideoDetailsViewModel() {
        handler = new Handler(Looper.getMainLooper());

        videoResource = new MediatorLiveData<>();
        videoResource.setValue(Resource.loading(null, null));
    }

    public void setParams(String id) {
        this.id = id;
        refreshVideo();
    }

    @VisibleForTesting
    public LiveData<Resource<Video>> getVideoResource() {
        return videoResource;
    }

    public void refreshVideo() {
        videoResource.setValue(Resource.loading(null, null));
        handler.postDelayed(() -> {
            Video video = VideoUtil.getVideo(id);
            if (video == null) {
                videoResource.setValue(Resource.error(null, null));
            } else {
                videoResource.setValue(Resource.success(video, null));
            }

        }, 3000);
    }
}
