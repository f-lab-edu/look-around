package kky.flab.lookaround.feature.record

import android.graphics.ImageDecoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.core.ui.util.millsToTimeFormat
import kky.flab.lookaround.feature.record.databinding.ItemRecordBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordListAdapter(
    private val listener: ButtonListener
) : ListAdapter<Record, RecordListAdapter.RecordViewHolder>(RecordDiffUtil()) {

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
        fun onBind(item: Record) {
            val date = Date(item.startTimeStamp)
            val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            binding.tvDate.text = sdf.format(date)
            binding.tvTime.text = (item.endTimeStamp - item.startTimeStamp).millsToTimeFormat()
            binding.tvDistance.text = "${item.distance}m"
            binding.tvMemo.text = item.memo

            val hasImage = item.imageUri.isNotEmpty()
            binding.ivPhoto.isVisible = hasImage
            if (hasImage) {
                val bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        binding.root.context.contentResolver,
                        Uri.parse(item.imageUri)
                    )
                )
                binding.ivPhoto.setImageBitmap(bitmap)
            }

            binding.ivModify.setOnClickListener {
                listener.onModify(item)
            }

            binding.ivDelete.setOnClickListener {
                listener.onDelete(item)
            }
        }
    }

    private class RecordDiffUtil : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }
    }

    interface ButtonListener {
        fun onModify(record: Record)

        fun onDelete(record: Record)
    }
}
