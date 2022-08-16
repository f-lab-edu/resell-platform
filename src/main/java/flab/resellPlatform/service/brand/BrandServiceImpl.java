package flab.resellPlatform.service.brand;

import flab.resellPlatform.domain.brand.BrandDTO;
import flab.resellPlatform.domain.brand.BrandEntity;
import flab.resellPlatform.repository.brand.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public BrandDTO addBrand(BrandDTO brandDTO) {
        BrandEntity brandEntity = modelMapper.map(brandDTO, BrandEntity.class);
        brandRepository.save(brandEntity);
        return null;
    }

    @Override
    public Optional<List<BrandEntity>> viewAllBrands() {
        return Optional.of(brandRepository.findAll());
    }

    @Override
    public boolean updateBrand(Long id, BrandDTO brandDTO) {
        BrandEntity brandEntity = modelMapper.map(brandDTO, BrandEntity.class);
        return brandRepository.update(id, brandEntity) != null;
    }

    @Override
    public boolean deleteBrand(Long id) {
        brandRepository.delete(id);
        return brandRepository.findById(id) == null;
    }

}
