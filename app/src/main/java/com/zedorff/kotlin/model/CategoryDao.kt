package com.zedorff.kotlin.model

import com.activeandroid.query.Select

object CategoryDao {

    fun createCategory(name : String) : Category {
        var category = Category(name)
        category.save()
        return category
    }

    fun loadAllCategories(): List<Category> = Select().from(Category::class.java).execute<Category>()
    fun loadCategory(id: Long): Category = Select().from(Category::class.java).where("id = ?", id).executeSingle<Category>()
}