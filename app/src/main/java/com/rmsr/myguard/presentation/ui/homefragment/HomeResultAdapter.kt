package com.rmsr.myguard.presentation.ui.homefragment

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rmsr.myguard.R
import com.rmsr.myguard.databinding.BreachListItemBinding
import com.rmsr.myguard.databinding.HomeResultRecyclerHeaderBinding
import com.rmsr.myguard.di.GlideApp
import com.rmsr.myguard.presentation.util.getHTMLFormatText
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.random.Random

@FragmentScoped
class HomeResultAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var breachItems: List<BreachItemUiState> = emptyList()

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private var expandedPos = -1
    private var preExpandedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        logoImageSize = parent.resources.getDimensionPixelSize(R.dimen._60sdp)

        return if (viewType == TYPE_HEADER) {
            HeaderMessageHolder.from(parent)
        } else {
            ResultHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderMessageHolder -> holder.bind(breachItems.size)

            is ResultHolder -> {
                val isExpanded = expandedPos == position
                if (isExpanded)
                    preExpandedPos = position
                holder.bind(breachItems[position], isExpanded)
                holder.itemBinding.root.setOnClickListener {
                    expandedPos = if (isExpanded) -1 else position
                    notifyItemChanged(expandedPos)
                    notifyItemChanged(preExpandedPos)
                }
            }

        }
    }


    override fun getItemCount(): Int {
        return breachItems.size
    }

    //to differ Header Item from Normal List Item
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }


    private inner class MyDiffUtil(
        private val oldList: List<BreachItemUiState>,
        private val newList: List<BreachItemUiState>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
    }

    fun setList(newBreaches: List<BreachItemUiState>) {
        expandedPos = -1
        preExpandedPos = -1
        //Add empty Breach at first index to replace it with Header
        val uniqueIdForDiffUtil =
            if (newBreaches.isNotEmpty())
                -newBreaches.take(3).map { it.id }.average().toLong()
            else Random.nextLong(-100_000, -2)

        val headerPlaceHolder = BreachItemUiState(
            id = uniqueIdForDiffUtil,
            title = "",
            logoUrl = "",
            discoveredDate = "",
            compromisedData = "",
            description = ""
        )

        val editedList = listOf(headerPlaceHolder) + newBreaches
        val diffResult = DiffUtil.calculateDiff(MyDiffUtil(breachItems, editedList))
        breachItems = editedList
        diffResult.dispatchUpdatesTo(this)
    }


    //    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    private class ResultHolder(val itemBinding: BreachListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        companion object {
            fun from(parent: ViewGroup): ResultHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemBinding: BreachListItemBinding =
                    BreachListItemBinding.inflate(inflater, parent, false)
                //DataBindingUtil.inflate(inflater, R.layout.breach_list_item, parent, false)
                return ResultHolder(itemBinding)
            }
        }


        fun bind(itemUiState: BreachItemUiState, expandInfo: Boolean) {
            val context = itemBinding.root.context


            with(itemBinding) {

                GlideApp.with(listItemBreachLogo)
                    .load(itemUiState.logoUrl)
                    .timeout(5000)
                    .into(listItemBreachLogo)


                listItemBreachName.text =
                    String.format(context.getString(R.string.domain_name), itemUiState.title)
                listItemBreachData.text =
                    String.format(
                        context.getString(R.string.breach_date),
                        itemUiState.discoveredDate
                    )
                listItemCompromisedData.text =
                    String.format(
                        context.getString(R.string.compromised_data),
                        itemUiState.compromisedData
                    )

                listItemBreachDescription.text =
                    getHTMLFormatText(
                        itemUiState.description
//                        String.format(
//                            context.getString(R.string.description),
//                            itemUiState.description
//                        )
                    )
                listItemBreachDescription.movementMethod = LinkMovementMethod.getInstance()

                if (expandInfo) {
                    expandedInfo.visibility = View.VISIBLE
                    expandIcon.rotation = 180f
                } else {
                    expandedInfo.visibility = View.GONE
                    expandIcon.rotation = 0f
                }
            }
        }


    }

    /**
     * A header to display Total leaks in the first item in recycler.
     */
    private class HeaderMessageHolder(
        private val headerBinding: HomeResultRecyclerHeaderBinding
    ) : RecyclerView.ViewHolder(headerBinding.root) {

        companion object {
            fun from(parent: ViewGroup): HeaderMessageHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =
                    HomeResultRecyclerHeaderBinding.inflate(inflater, parent, false)
                return HeaderMessageHolder(binding)
            }
        }

        fun bind(breachesSize: Int) {

            with(headerBinding.resultHeaderTextView) {

                if (breachesSize > 1) {
                    text = String.format(
                        resources.getString(R.string.breach_leak_massage),
                        breachesSize - 1
                    )
                    background =
                        ResourcesCompat.getColor(
                            resources,
                            R.color.error_red_color,
                            context.theme
                        ).toDrawable()

                } else {
                    text = String.format(resources.getString(R.string.breach_safe_massage))
                    background = ResourcesCompat.getColor(
                        resources,
                        R.color.success_green_color,
                        context.theme
                    ).toDrawable()
                }
            }
        }
    }
}


private fun toggleArrow(view: View, isExpanded: Boolean): Boolean {

    return if (isExpanded) {
        view.animate().setDuration(200).rotation(180f)
        true
    } else {
        view.animate().setDuration(200).rotation(0f)
        false
    }
}


@Suppress("Safe to delete - Unused")
private fun toggleLayout(isExpanded: Boolean, v: View, layoutExpand: LinearLayout): Boolean {
    toggleArrow(v, isExpanded)
    if (isExpanded) {
        Animations.expand(layoutExpand)
    } else {
        Animations.collapse(layoutExpand)
    }
    return isExpanded
}

@Suppress("Safe to delete - Unused")
object Animations {

    fun expand(view: View) {
        val animation = expandAction(view)
        view.startAnimation(animation)
    }

    private fun expandAction(view: View): Animation {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val actualheight = view.measuredHeight
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (actualheight * interpolatedTime).toInt()
                view.requestLayout()
            }
        }
        animation.duration = (actualheight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)
        return animation
    }

    fun collapse(view: View) {
        val actualHeight = view.measuredHeight
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height =
                        actualHeight - (actualHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }
        }
        animation.duration = (actualHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)
    }
}