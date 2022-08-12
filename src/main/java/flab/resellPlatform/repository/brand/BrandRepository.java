package flab.resellPlatform.repository.brand;

import flab.resellPlatform.domain.brand.BrandEntity;

import java.util.List;

public interface BrandRepository {

    BrandEntity save(BrandEntity brandEntity);
    List<BrandEntity> findAll();
    BrandEntity update(Long id, BrandEntity brandEntity);
    void delete(Long id);

}
