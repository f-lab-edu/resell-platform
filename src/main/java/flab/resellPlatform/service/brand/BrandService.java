package flab.resellPlatform.service.brand;

import flab.resellPlatform.domain.brand.BrandDTO;
import flab.resellPlatform.domain.brand.BrandEntity;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    BrandDTO addBrand(BrandDTO brandDTO);
    Optional<List<BrandEntity>> viewAllBrands();
    boolean updateBrand(Long id, BrandDTO brandDTO);
    boolean deleteBrand(Long id);

}
