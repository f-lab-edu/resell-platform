package flab.resellPlatform.repository.brand;

import flab.resellPlatform.domain.brand.BrandEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisBrandRepository implements BrandRepository {

    BrandMapper brandMapper;

    @Override
    public BrandEntity save(BrandEntity brandEntity) {
        return brandMapper.save(brandEntity);
    }

    @Override
    public List<BrandEntity> findAll() {
        return brandMapper.findAll();
    }

    @Override
    public BrandEntity update(Long id, BrandEntity brandEntity) {
        brandMapper.update(id, brandEntity);
        return brandEntity;
    }

    @Override
    public void delete(Long id) {
        brandMapper.delete(id);
    }

    @Override
    public BrandEntity findById(Long id) {
        return brandMapper.findById(id);
    }
}
