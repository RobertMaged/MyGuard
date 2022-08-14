package com.rmsr.myguard.presentation.ui.schedules

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rmsr.myguard.databinding.ScheduleListItemBinding
import com.rmsr.myguard.domain.entity.Schedule
import javax.inject.Inject
import com.google.android.material.R as MaterialR

@Deprecated("Depend on Jetpack Compose.")
class _ScheduleAdapter @Inject constructor() :
    RecyclerView.Adapter<_ScheduleAdapter.ScheduleHolder>() {

    private var mItems: List<Schedule> = listOf()
    private lateinit var itemBinding: ScheduleListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        val inflater = LayoutInflater.from(parent.context)
        itemBinding = ScheduleListItemBinding.inflate(inflater, parent, false)

        return ScheduleHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount(): Int = mItems.size

    fun setList(newItems: List<Schedule>) {
        mItems = newItems
        notifyDataSetChanged()
    }


    inner class ScheduleHolder(private val mBinding: ScheduleListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(currItem: Schedule) {
            mBinding.scheduleItemQuery.text = currItem.searchQuery.query
//            mBinding.ScheduleTextView.setText(currItem.scheduledQuery, TextView.BufferType.NORMAL)
            //mBinding.ScheduleTextView.inputType = InputType.TYPE_NULL
        }
    }

    /**
     * onSwipe left or right delete [ItemTouchHelper] ready to be attached to [RecyclerView],
     *
     * Witch refresh list after deleted item and notifies [onSwipe] with it.
     */
    fun deleteItemOnSwipeListener(onSwipe: (deletedSchedule: Schedule, position: Int) -> Unit): ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val delete = mItems[viewHolder.adapterPosition]
                onSwipe(delete, viewHolder.adapterPosition)
                setList(mItems.minusElement(delete))
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return

                val item = viewHolder.itemView
                val rect = when {
                    //swiping right
                    dX > 0 -> Rect(item.left, item.top, item.right + dX.toInt(), item.bottom)
                    //swiping left
                    else -> Rect(item.right + dX.toInt(), item.top, item.right, item.bottom)

                }

                canvas.clipRect(rect)
                ColorDrawable(Color.YELLOW).apply {
                    bounds = rect
                    draw(canvas)
//                    canvas.drawR
                }

                ResourcesCompat.getDrawable(
                    recyclerView.context.resources,
                    MaterialR.drawable.abc_ic_clear_material,
                    recyclerView.context.theme
                )?.let { icon ->
                    val horizontalMarian = 30

                    val halfSize = icon.intrinsicHeight / 2
                    val topPosition = item.top + ((item.bottom - item.top) / 2 - halfSize)
                    val leftPos = item.right - horizontalMarian - halfSize * 2

                    val iconRect = when {
                        //swiping right
                        dX > 0 -> Rect(
                            item.left + horizontalMarian,
                            topPosition,
                            item.left + horizontalMarian + icon.intrinsicWidth,
                            topPosition + icon.intrinsicHeight
                        )
                        //swiping left
                        else -> Rect(
                            leftPos,
                            topPosition,
                            item.right - horizontalMarian,
                            topPosition + icon.intrinsicHeight
                        )

                    }

                    icon.bounds = iconRect
                    icon.draw(canvas)

                }

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        return ItemTouchHelper(callback)
    }
}