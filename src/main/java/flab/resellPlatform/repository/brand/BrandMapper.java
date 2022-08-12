package flab.resellPlatform.repository.brand;

import flab.resellPlatform.domain.brand.BrandEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BrandMapper {

    @Insert("INSERT INTO brand(name) VALUES(#{name})")
    BrandEntity save(BrandEntity brandEntity);

    @Select("SELECT * FROM brand")
    List<BrandEntity> findAll();

    @Update("UPDATE brand SET name = #{brandEntity.name} WHERE id = #{id}")
    BrandEntity update(Long id, BrandEntity brandEntity);

    @Delete("DELETE FROM brand WHERE id = #[id}")
    void delete(Long id);

}
