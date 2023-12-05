package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemArticleBinding
import java.text.DecimalFormat

class ProductAdapter(val onItemClicked: (ArticleModel) -> Unit) : ListAdapter<ArticleModel, ProductAdapter.ViewHolder>(diffUtil){

    private var items: List<ArticleModel> = listOf()

    fun updateData(newItems: List<ArticleModel>) {
        //제품 업데이트 확인
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(articleModel: ArticleModel){
            //ArticleModel 정보들을 TextView로 보여줌
            val priceFormat = DecimalFormat("#,###")
            binding.titleTextView.text = articleModel.title
            binding.emailTextView.text = articleModel.email
            binding.priceTextView.text = "${priceFormat.format(articleModel.price.toInt())}원"
            binding.informationTextView.text = articleModel.information
            binding.filterTextView.text = "판매여부" + articleModel.filter

            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object  : DiffUtil.ItemCallback<ArticleModel>(){
            override fun areItemsTheSame(Item: ArticleModel, newItem: ArticleModel): Boolean {
                //새로 들어온 제품의 createdAt로 변경
                return Item.createdAt == newItem.createdAt
            }
            override fun areContentsTheSame(Item: ArticleModel, newItem: ArticleModel): Boolean {
                return Item == newItem
            }
        }
    }
}
