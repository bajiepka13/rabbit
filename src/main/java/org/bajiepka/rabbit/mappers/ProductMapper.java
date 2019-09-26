package org.bajiepka.rabbit.mappers;

import org.apache.ibatis.annotations.*;
import org.bajiepka.rabbit.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM products WHERE name = #{name}")
    Optional<Product> selectByName(@Param("name") String name);

    @Select("SELECT * FROM products")
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "weight", column = "weight"),
            @Result(property = "cost", column = "cost")
    })
    List<Product> selectAll();

    @Insert("INSERT into products(name,description,weight,cost) VALUES(#{name}, #{description}, #{weight}, #{cost})")
    void save(Product product);

    @Delete("DELETE FROM products")
    void clear();
}
