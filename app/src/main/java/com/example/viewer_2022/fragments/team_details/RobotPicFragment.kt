package com.example.viewer_2022.fragments.team_details

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.viewer_2022.R
import com.example.viewer_2022.constants.Constants
import com.example.viewer_2022.getTeamDataValue
import kotlinx.android.synthetic.main.robot_pic.view.*
import kotlinx.android.synthetic.main.team_details.view.tv_team_name
import kotlinx.android.synthetic.main.team_details.view.tv_team_number
import java.io.File


/**
 * Page for showing robot pictures
 */
class RobotPicFragment : Fragment() {
    private var teamNumber: String? = null
    private var teamName: String? = null
    private var picFile1: File? = null
    private var picFile2: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.robot_pic, container, false)
        populateTeamInfo(root)
        getPicFiles()
        showPictures(root)
        return root
    }

    private fun populateTeamInfo(root: View) {
        teamNumber = arguments?.getString(Constants.TEAM_NUMBER, Constants.NULL_CHARACTER)
        teamName = getTeamDataValue(teamNumber!!, "team_name")
        root.tv_team_number.text = teamNumber.toString()
        root.tv_team_name.text = teamName.toString()
    }

    private fun getPicFiles() {
        picFile1 = File(
            "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/",
            "${teamNumber}_full_robot_1.jpg"
        )
        picFile2 = File(
            "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/",
            "${teamNumber}_full_robot_2.jpg"
        )
    }

    private fun showPictures(root: View) {
        val bitmapsToDisplay = mutableListOf<Bitmap>()
        if (picFile1!!.exists()) {
            bitmapsToDisplay.add(BitmapFactory.decodeFile(picFile1!!.absolutePath).rotated())
        }
        if (picFile2!!.exists()) {
            bitmapsToDisplay.add(BitmapFactory.decodeFile(picFile2!!.absolutePath).rotated())
        }
        for (bitmap in bitmapsToDisplay) {
            root.ll_robot_pics.addView(ImageView(context).also {
                it.scaleType = ImageView.ScaleType.FIT_START
                it.adjustViewBounds = true
                it.setImageBitmap(bitmap)
            })
        }
    }

    private fun Bitmap.rotated() = Bitmap.createBitmap(
        this, 0, 0, width, height, Matrix().also { it.postRotate(90F) }, true
    )
}
