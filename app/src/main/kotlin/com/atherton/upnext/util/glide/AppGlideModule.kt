package com.atherton.upnext.util.glide

import com.atherton.upnext.R
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class UpNextAppGlideModule : AppGlideModule() {

    companion object {
        val searchModelGridRequestOptions: RequestOptions by lazy {
            RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(20))
                .error(R.drawable.ic_broken_image_white_24dp)
        }

        val searchModelPosterRequestOptions by lazy {
            RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(20))
                .error(R.drawable.ic_broken_image_white_24dp)
        }

        val modelDetailBackdropRequestOptions by lazy {
            RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_broken_image_white_24dp)
        }

        val modelDetailVideoRequestOptions by lazy {
            RequestOptions()
                .transforms(CenterInside(), RoundedCorners(20))
                .error(R.drawable.ic_broken_image_white_24dp)
        }
    }
}
