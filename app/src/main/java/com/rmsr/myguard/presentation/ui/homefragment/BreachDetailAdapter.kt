package com.rmsr.myguard.presentation.ui.homefragment

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rmsr.myguard.R
import com.rmsr.myguard.databinding.BreachListItemBinding
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@Deprecated("")
@FragmentScoped
class BreachDetailAdapter @Inject constructor(@ActivityContext val mContext: Context) :
    RecyclerView.Adapter<BreachDetailAdapter.DetailBreachHolder>() {

    private var mBreaches: List<BreachDetailView> = listOf()
    lateinit var itemBinding: BreachListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailBreachHolder {

        val inflater = LayoutInflater.from(parent.context)
//        itemBinding = DataBindingUtil.inflate(inflater, R.layout.breach_list_item, parent, false)
        itemBinding = BreachListItemBinding.inflate(inflater)


        return DetailBreachHolder(itemBinding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: DetailBreachHolder, position: Int) {
        holder.bind(mBreaches[position])


        holder.itemView.setOnClickListener {
            holder.changeVisibility(mBreaches[position])
//            TransitionManager.beginDelayedTransition(mRecyclerView)
        }
    }

    override fun getItemCount(): Int {
        return mBreaches.size
    }

    fun setList(newBreaches: List<BreachDetailView>?) {
        mBreaches = newBreaches ?: listOf()
        notifyDataSetChanged()
    }


    inner class DetailBreachHolder(private val mBinding: BreachListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(currBreach: BreachDetailView) {
            Glide.with(mContext)
                .load(currBreach.logoPath)
                .into(mBinding.listItemBreachLogo)

            mBinding.listItemBreachName.text =
                String.format(mContext.getString(R.string.domain_name), currBreach.name)
            mBinding.listItemBreachData.text =
                String.format(mContext.getString(R.string.breach_date), currBreach.breachDate)
            mBinding.listItemCompromisedData.text =
                String.format(mContext.getString(R.string.compromised_data), currBreach.dataClasses)
            mBinding.listItemBreachDescription.text = Html.fromHtml(
                String.format(mContext.getString(R.string.description), currBreach.description),
                Html.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE
            )
            mBinding.listItemBreachDescription.movementMethod = LinkMovementMethod.getInstance()

            mBinding.expandedInfo.visibility =
                if (currBreach.isInfoExpanded) View.VISIBLE else View.GONE
        }

        fun changeVisibility(currBreach: BreachDetailView) {
            currBreach.isInfoExpanded = mBinding.expandedInfo.toggleVisibility()
        }

    }


    private fun LinearLayout.toggleVisibility(): Boolean {
        return if (visibility == View.GONE) {
            visibility = View.VISIBLE
            true
        } else {
            visibility = View.GONE
            false
        }
        //isActivated = visibility == View.VISIBLE
    }


}