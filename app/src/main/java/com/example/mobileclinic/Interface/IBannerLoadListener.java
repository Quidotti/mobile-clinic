package com.example.mobileclinic.Interface;

import com.example.mobileclinic.Model.Banner;

import java.util.List;


public interface IBannerLoadListener {

    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
