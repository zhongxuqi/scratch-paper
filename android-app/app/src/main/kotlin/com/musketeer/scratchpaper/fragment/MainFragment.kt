package com.musketeer.scratchpaper.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.muskeeter.base.fragment.BaseSupportFragment
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.activity.BrowsePaperActivity
import com.musketeer.scratchpaper.activity.EditPaperActivity
import com.musketeer.scratchpaper.adapter.MainAdapter
import com.musketeer.scratchpaper.config.Config
import com.musketeer.scratchpaper.fileutils.PaperFileUtils
import com.musketeer.scratchpaper.utils.LogUtils
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.shareboard.SnsPlatform
import com.umeng.socialize.utils.ShareBoardlistener
import java.io.File

/**
 * Created by zhongxuqi on 08/07/2017.
 */
class MainFragment: BaseSupportFragment() {
    companion object {
        val TAG = "MainFragment"
    }

    val mPaperListView: RecyclerView by lazy {
        findViewById(R.id.main_paper_list) as RecyclerView
    }
    val mAdapter: MainAdapter by lazy {
        MainAdapter(activity!!)
    }

    val addNewScratchPaper: ImageView by lazy {
        findViewById(R.id.add_new_scratch_paper) as ImageView
    }

    val layoutManger: LinearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    var mDialog: AlertDialog? = null

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            dismissLoadingDialog()
            mAdapter.notifyDataSetChanged()
            showCustomToast(resources.getString(R.string.rename_success))
        }
    }

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) {
        if (BaseView == null) BaseView = inflater?.inflate(R.layout.fragment_main, null)
    }

    override fun initView() {
        mPaperListView.layoutManager = layoutManger
    }

    override fun initEvent() {
        addNewScratchPaper.setOnClickListener(this)
        mAdapter.onItemClickListener = object: View.OnClickListener{
            override fun onClick(v: View?) {
                val fileName = v?.getTag() as String
                val bundle = Bundle()
                bundle.putString("paper_name", fileName)
                startActivityForResult(BrowsePaperActivity::class.java, bundle, Config.ACTION_EDIT_FILE)

                if (mDialog != null) {
                    mDialog!!.dismiss()
                }

            }
        }
        mAdapter.onItemLongClickListener = object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                val fileName = v?.getTag() as String
                mDialog?.dismiss()
                val builder = AlertDialog.Builder(activity)
                val contentView = LayoutInflater.from(activity).inflate(R.layout.include_saved_paper_action, null)
                builder.setView(contentView)
                mDialog = builder.create()
                //编辑内容
                val editButton = contentView.findViewById<View>(R.id.edit)
                editButton.setOnClickListener {
                    // TODO Auto-generated method stub
                    val bundle = Bundle()
                    bundle.putString("paper_name", fileName)
                    startActivityForResult(EditPaperActivity::class.java, bundle, Config.ACTION_EDIT_FILE)
                    mDialog!!.dismiss()
                }
                //删除内容
                val deleteButton = contentView.findViewById<View>(R.id.delete)
                deleteButton.setOnClickListener {
                    // TODO Auto-generated method stub
                    mDialog?.dismiss()
                    val deleteDialogBuilder = AlertDialog.Builder(activity)
                    deleteDialogBuilder.setMessage(resources.getString(R.string.affirm_delete))
                    deleteDialogBuilder.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mDialog!!.dismiss() }
                    deleteDialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                        PaperFileUtils.deleteImage(fileName)
                        mAdapter.removeItem(fileName)
                        mDialog?.dismiss()
                    }
                    mDialog = deleteDialogBuilder.create()
                    mDialog?.show()
                }
                //分享
                val shareButton = contentView.findViewById<View>(R.id.share)
                shareButton.setOnClickListener {
                    val filePath = PaperFileUtils.getImagePath(fileName)
                    val image = UMImage(activity, File(filePath))
                    image.setThumb(UMImage(activity, File(filePath)))
                    image.compressStyle = UMImage.CompressStyle.SCALE
                    ShareAction(activity).withText(fileName).withMedia(image)
                            .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                            .setShareboardclickCallback(object: ShareBoardlistener {
                                override fun onclick(p0: SnsPlatform?, p1: SHARE_MEDIA?) {
                                    ShareAction(activity).setPlatform(p1).withText(fileName).withMedia(image)
                                            .setCallback(object: UMShareListener {
                                                override fun onResult(p0: SHARE_MEDIA?) {
                                                    LogUtils.d(TAG, "onResult SHARE_MEDIA: $p0")
                                                }

                                                override fun onCancel(p0: SHARE_MEDIA?) {
                                                    LogUtils.d(TAG, "onCancel SHARE_MEDIA: $p0")
                                                }

                                                override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                                                    p1?.printStackTrace()
                                                    LogUtils.d(TAG, "onError SHARE_MEDIA: $p0")
                                                }

                                                override fun onStart(p0: SHARE_MEDIA?) {
                                                    LogUtils.d(TAG, "onStart SHARE_MEDIA: $p0")
                                                }
                                            }).share()
                                }
                            }).open()
                    mDialog?.dismiss()
                }
                mDialog?.show()
                return true
            }
        }
    }

    override fun initData() {
        mAdapter.imageGroupList = PaperFileUtils.readBitmapListGroup(false)
        mPaperListView.adapter = mAdapter
    }

    fun refreshViews() {
        mAdapter.imageGroupList = PaperFileUtils.readBitmapListGroup(true)
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.add_new_scratch_paper -> {
                val myAnimation = AnimationUtils.loadAnimation(activity, R.anim.view_scale_larger)
                addNewScratchPaper.startAnimation(myAnimation)
                myAnimation.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {
                        // TODO Auto-generated method stub

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        // TODO Auto-generated method stub
                        startActivityForResult(EditPaperActivity::class.java, Config.ACTION_ADD_FILE)
                    }

                    override fun onAnimationRepeat(animation: Animation) {
                        // TODO Auto-generated method stub

                    }

                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            Config.ACTION_ADD_FILE -> refreshViews()
            Config.ACTION_EDIT_FILE -> refreshViews()
            Config.ACTION_CHANGE_SETTINGS -> initData()
        }
    }
}