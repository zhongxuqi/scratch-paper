package com.musketeer.scratchpaper.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.muskeeter.base.acitivity.BaseFragmentActivity
import com.muskeeter.base.fragment.BaseSupportFragment
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.settings.SettingsActivity
import com.musketeer.scratchpaper.bean.UserInfo
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.utils.LogUtils
import com.squareup.picasso.Picasso
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.shareboard.SnsPlatform
import com.umeng.socialize.utils.ShareBoardlistener

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class MyFragment: BaseSupportFragment() {
    companion object {
        val TAG = "MyFragment"
    }

    var userInfo: UserInfo? = null

    val headImage: ImageView by lazy {
        findViewById(R.id.head_image) as ImageView
    }

    val userName: TextView by lazy {
        findViewById(R.id.user_name) as TextView
    }

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) {
        BaseView = inflater?.inflate(R.layout.fragment_my, null)
    }

    override fun initView() {

    }

    override fun initEvent() {
        findViewById(R.id.goto_settings)?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent()
                intent.setClass(activity, SettingsActivity::class.java)
                activity.startActivityForResult(intent, Config.ACTION_CHANGE_SETTINGS)
            }
        })
        findViewById(R.id.goto_login)?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                ShareAction(activity).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                        .setShareboardclickCallback(object: ShareBoardlistener {
                            override fun onclick(p0: SnsPlatform?, shareMedia: SHARE_MEDIA?) {
                                UMShareAPI.get(activity).getPlatformInfo(activity, shareMedia, object: UMAuthListener{
                                    override fun onComplete(p0: SHARE_MEDIA?, p1: Int, p2: MutableMap<String, String>?) {
                                        if (p2 != null) {
                                            for (entry in p2.entries) {
                                                LogUtils.d(TAG, entry.key + "=" + entry.value)
                                            }
                                            when(shareMedia) {
                                                SHARE_MEDIA.WEIXIN -> {
                                                    userInfo = UserInfo(p2.get("unionid"), p2.get("screen_name"), p2.get("profile_image_url"))
                                                }
                                                SHARE_MEDIA.QQ -> {
                                                    userInfo = UserInfo(p2.get("openid"), p2.get("screen_name"), p2.get("profile_image_url"))
                                                }
                                                SHARE_MEDIA.SINA -> {
                                                    userInfo = UserInfo(p2.get("id"), p2.get("screen_name"), p2.get("profile_image_url"))
                                                }
                                            }
                                        }
                                    }

                                    override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {

                                    }

                                    override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
                                        p2?.printStackTrace()
                                    }

                                    override fun onStart(p0: SHARE_MEDIA?) {

                                    }
                                })
                            }
                        }).open()
            }
        })
    }

    override fun initData() {

    }

    fun showUserInfo() {
        if (userInfo == null) return
        Picasso.with(context).load(userInfo?.imageUrl).into(headImage)
        userName.setText(userInfo?.name)
    }
}