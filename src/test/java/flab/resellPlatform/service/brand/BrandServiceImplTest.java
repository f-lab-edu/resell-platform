package flab.resellPlatform.service.brand;

import flab.resellPlatform.domain.brand.BrandDTO;
import flab.resellPlatform.domain.brand.BrandEntity;
import flab.resellPlatform.repository.brand.BrandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.testcontainers.shaded.org.yaml.snakeyaml.constructor.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class BrandServiceImplTest {

    @Mock
    BrandRepository brandRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    BrandServiceImpl brandService;

    @DisplayName("브랜드 정보 저장 성공")
    @Test
    void addBrand() {
        BrandDTO brandDTO = BrandDTO.builder()
                .name("Nike")
                .build();

        BrandEntity brandEntity = BrandEntity.builder()
                .id(1L)
                .name("Nike")
                .build();

        when(modelMapper.map(brandDTO, BrandEntity.class)).thenReturn(brandEntity);
        when(brandRepository.save(brandEntity)).thenReturn(brandEntity);

        BrandDTO savedBrandDTO = brandService.addBrand(brandDTO);

        assertThat(savedBrandDTO).isEqualTo(brandDTO);
    }

    @DisplayName("브랜드 정보 저장 실패 - 중복된 브랜드명")
    @Test
    void addBrandFailure() {
        BrandDTO brandDTO = BrandDTO.builder()
                .name("Nike")
                .build();

        BrandEntity brandEntity = BrandEntity.builder()
                .id(1L)
                .name("Nike")
                .build();

        when(modelMapper.map(brandDTO, BrandEntity.class)).thenReturn(brandEntity);
        when(brandRepository.save(brandEntity)).thenThrow(DuplicateKeyException.class);

        assertThatThrownBy(() -> brandService.addBrand(brandDTO)).isInstanceOf(DuplicateKeyException.class);
    }
}