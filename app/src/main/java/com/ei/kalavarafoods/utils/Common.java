package com.ei.kalavarafoods.utils;

import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.database.ProductEntity;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static List<ProductEntity> convertProductToProductEntity(List<Product> productList) {
        List<ProductEntity> productEntityList = new ArrayList<>();

        for (Product product : productList){
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductId(product.getProductId());
            productEntity.setProductName(product.getProductName());
            productEntity.setProductSku(product.getProductSku());
            productEntity.setProductCategory(product.getProductCategory());
            productEntity.setProductBrand(product.getProductBrand());
            productEntity.setProductSize(product.getProductSize());
            productEntity.setProductUnit(product.getProductUnit());
            productEntity.setProductUnitprice(product.getProductUnitprice());
            productEntity.setProductImage(product.getProductImage());
            productEntity.setProductDescription(product.getProductDescription());
            productEntity.setIngredients(product.getIngredients());
            productEntity.setHowToUse(product.getHowToUse());
            productEntity.setProductCreatedat(product.getProductCreatedat());
            productEntity.setProductOfferprice(product.getProductOfferprice());
            productEntity.setProductTaxid(product.getProductTaxid());
            productEntity.setProductWish(product.getProductWish());
            productEntityList.add(productEntity);
        }
        return productEntityList;
    }
}
