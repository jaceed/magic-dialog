package com.github.jaceed.magicdialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.jaceed.magicdialog.databinding.FragmentDialogCheckOptionBinding
import com.github.jaceed.magicdialog.databinding.ItemDialogCheckOptionBinding
import com.github.jaceed.extender.view.setOnProtectedClickListener
import com.github.jaceed.extender.view.visible

/**
 * Created by Jacee.
 * Date: 2021.07.15
 */
class CheckOptionDialog : BaseOptionDialog() {

    private lateinit var options: Array<String>
    private var checkedIndex: Int = -1
    private var interaction: CheckOptionInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        options = arguments?.getStringArray(ARG_OPTIONS) ?: emptyArray()
        checkedIndex = arguments?.getInt(ARG_CHECKED, -1) ?: -1
    }

    override fun onCreateOptionView(inflater: LayoutInflater): View? {

        return FragmentDialogCheckOptionBinding.inflate(inflater).apply {
            optionList.adapter = OptionAdapter()
        }
            .root
    }

    fun setInteraction(interaction: CheckOptionInteraction) {
        this.interaction = interaction
    }


    private inner class OptionHolder(binding: ItemDialogCheckOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.option
        val checked: ImageView = binding.checked

        init {
            binding.root.setOnProtectedClickListener {
                interaction?.onCheckOption(adapterPosition)
                dismiss()
            }
        }
    }


    @SuppressLint("ResourceType")
    private inner class OptionAdapter : RecyclerView.Adapter<OptionHolder>() {

        private val themeColor by lazy {
            val a = requireActivity().themeBy(R.attr.magicOptionStyle, R.styleable.MagicOptionChecked) ?: return@lazy null
            Pair(a.getColor(R.styleable.MagicOptionChecked_magicOptionItemColor, 0), a.getColor(R.styleable.MagicOptionChecked_magicOptionItemCheckColor, 0)).also {
                a.recycle()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder {
            return OptionHolder(ItemDialogCheckOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
                themeColor?.let {
                    if (it.first != 0) {
                        name.setTextColor(it.first)
                    }
                    if (it.second != 0) {
                        checked.imageTintList = ColorStateList.valueOf(it.second)
                    }
                }
            }

        }

        override fun onBindViewHolder(holder: OptionHolder, position: Int) {
            holder.name.text = options[position]
            holder.checked.visible = checkedIndex == position
        }

        override fun getItemCount(): Int {
            return options.size
        }

    }


    class Builder(context: Context) : BaseOptionDialog.Builder<CheckOptionDialog>(context) {

        fun options(list: Array<String>, checked: Int): Builder {
            require(checked in list.indices)
            arguments.putStringArray(ARG_OPTIONS, list)
            arguments.putInt(ARG_CHECKED, checked)
            return this
        }

        override fun create() = CheckOptionDialog().apply {
            this.arguments = this@Builder.arguments
        }

    }


    fun interface CheckOptionInteraction {
        fun onCheckOption(index: Int)
    }

    companion object {
        private const val ARG_OPTIONS = "check_options"
        private const val ARG_CHECKED = "check_index"
    }

}