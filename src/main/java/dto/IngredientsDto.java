package dto;

import java.util.List;

public class IngredientsDto {
    private List<String> ingredients;


    public IngredientsDto(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
