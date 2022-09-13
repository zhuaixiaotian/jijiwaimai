package com.gigie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gigie.domain.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
