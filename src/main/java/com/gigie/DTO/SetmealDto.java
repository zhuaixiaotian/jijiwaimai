package com.gigie.DTO;


import com.gigie.domain.Setmeal;
import com.gigie.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
