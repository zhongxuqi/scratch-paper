package com.musketeer.scratchpaper.bean

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class UserInfo(uid: String?, name: String?, imageUrl: String?) {
    val uid: String?
    val name: String?
    val imageUrl: String?

    init {
        this.uid = uid
        this.name = name
        this.imageUrl = imageUrl
    }
}