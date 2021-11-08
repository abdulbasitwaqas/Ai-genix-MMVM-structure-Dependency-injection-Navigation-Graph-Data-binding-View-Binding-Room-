package com.jsbl.genix.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.model.FeedBackQuestionsModel

class FeedBackQuestionAdapter(
    feedBackQuestionsModelList: List<FeedBackQuestionsModel>,
    context: Context,
    val click : feedbackInterface
) :
    RecyclerView.Adapter<FeedBackQuestionAdapter.MyViewHolder>() {
    private var feedBackQuestionsModelList: List<FeedBackQuestionsModel>
    private val context: Context

    private val questionID: Int = -1
    private val rating: Int = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.feedback_questions, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.rateYourExperienceTV.setText(feedBackQuestionsModelList[position].question)
        if (feedBackQuestionsModelList[position].answerStars.toString()
                .equals("") || feedBackQuestionsModelList[position].answerStars ==0
        ) {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))

        } else if (feedBackQuestionsModelList[position].answerStars==1) {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 1
            click.feedback(feedBackQuestionsModelList[position].iD, 1)
        } else if (feedBackQuestionsModelList[position].answerStars==2) {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 2
            click.feedback(feedBackQuestionsModelList[position].iD, 2)
        } else if (feedBackQuestionsModelList[position].answerStars==3) {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 3
            click.feedback(feedBackQuestionsModelList[position].iD, 3)
        } else if (feedBackQuestionsModelList[position].answerStars==4) {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 4
            click.feedback(feedBackQuestionsModelList[position].iD, 4)
        } else if (feedBackQuestionsModelList[position].answerStars==5) {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            rating == 5
            click.feedback(feedBackQuestionsModelList[position].iD, 5)
        }

        holder.oneStarIV.setOnClickListener {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 1
            click.feedback(feedBackQuestionsModelList[position].iD, 1)
        }

        holder.twoStarIV.setOnClickListener {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 2
            click.feedback(feedBackQuestionsModelList[position].iD, 2)
        }

        holder.threeStarIV.setOnClickListener {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 3
            click.feedback(feedBackQuestionsModelList[position].iD, 3)
        }

        holder.fourStarIV.setOnClickListener {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_unselected))
            rating == 4
            click.feedback(feedBackQuestionsModelList[position].iD, 4)
        }

        holder.fiveStarIV.setOnClickListener {
            holder.oneStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.twoStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.threeStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fourStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            holder.fiveStarIV.setImageDrawable(context.resources.getDrawable(R.drawable.ic_star_selected))
            rating == 5
            click.feedback(feedBackQuestionsModelList[position].iD, 5)
        }
    }

    fun updateFeedBackQuestionsList(feedBackQuestionsModelList: List<FeedBackQuestionsModel>) {
        this.feedBackQuestionsModelList = feedBackQuestionsModelList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return feedBackQuestionsModelList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rateYourExperienceTV: TextView
        var oneStarIV: ImageView
        var twoStarIV: ImageView
        var threeStarIV: ImageView
        var fourStarIV: ImageView
        var fiveStarIV: ImageView

        init {
            oneStarIV = itemView.findViewById(R.id.oneStarIV)
            twoStarIV = itemView.findViewById(R.id.twoStarIV)
            threeStarIV = itemView.findViewById(R.id.threeStarIV)
            fourStarIV = itemView.findViewById(R.id.fourStarIV)
            fiveStarIV = itemView.findViewById(R.id.fiveStarIV)
            rateYourExperienceTV = itemView.findViewById(R.id.rateYourExperienceTV)
        }
    }

    interface feedbackInterface {
        fun feedback(id: Int, rating: Int)
    }

    init {
        this.feedBackQuestionsModelList = feedBackQuestionsModelList
        this.context = context
    }
}