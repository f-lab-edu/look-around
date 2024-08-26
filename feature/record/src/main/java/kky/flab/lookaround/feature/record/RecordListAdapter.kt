package kky.flab.lookaround.feature.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kky.flab.lookaround.core.ui.util.ImageLoader.loadUri
import kky.flab.lookaround.feature.record.databinding.ItemRecordBinding
import kky.flab.lookaround.feature.record.model.RecordUiModel

class RecordListAdapter(
    private val listener: ButtonListener
) : ListAdapter<RecordUiModel, RecordListAdapter.RecordViewHolder>(RecordDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )

        return RecordViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class RecordViewHolder(
        private val binding: ItemRecordBinding,
        private val listener: ButtonListener,
    ) : ViewHolder(binding.root) {
        fun onBind(item: RecordUiModel) = with(binding) {
            tvDate.text = item.date
            tvTime.text = item.runTime
            tvDistance.text = item.distance
            tvMemo.text = item.memo

            val hasImage = item.imageUri != null
            ivPhoto.isVisible = hasImage

            if (item.imageUri != null) {
                ivPhoto.loadUri(item.imageUri)
            }

            ivModify.setOnClickListener {
                listener.onModify(item)
            }

            ivDelete.setOnClickListener {
                listener.onDelete(item)
            }
        }

    }

    private class RecordDiffUtil : DiffUtil.ItemCallback<RecordUiModel>() {
        override fun areItemsTheSame(oldItem: RecordUiModel, newItem: RecordUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecordUiModel, newItem: RecordUiModel): Boolean {
            return oldItem == newItem
        }
    }

    interface ButtonListener {
        fun onModify(record: RecordUiModel)

        fun onDelete(record: RecordUiModel)
    }
}
