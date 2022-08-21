package flab.resellPlatform.repository.brand;

import flab.resellPlatform.domain.brand.BrandEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyBatisBrandRepositoryTest {

    @Mock
    BrandMapper brandMapper;

    @InjectMocks
    MyBatisBrandRepository myBatisBrandRepository;

    @DisplayName("브랜드 정보 저장")
    @Test
    void save() throws SQLException {
        BrandEntity brandEntity = BrandEntity.builder()
                .id(1L)
                .name("Nike")
                .build();

        when(brandMapper.save(brandEntity)).thenReturn(brandEntity);
        BrandEntity savedBrandEntity = myBatisBrandRepository.save(brandEntity);

        assertThat(savedBrandEntity).isEqualTo(brandEntity);
    }

    @DisplayName("전체 브랜드 정보 조회")
    @Test
    void findAll() {
        BrandEntity brandEntity1 = BrandEntity.builder()
                .id(1L)
                .name("Nike")
                .build();
        BrandEntity brandEntity2 = BrandEntity.builder()
                .id(2L)
                .name("Adidas")
                .build();

        when(brandMapper.findAll()).thenReturn(Arrays.asList(brandEntity1, brandEntity2));

        List<BrandEntity> allBrands = myBatisBrandRepository.findAll();

        verify(brandMapper).findAll();
        assertThat(allBrands).contains(brandEntity1, brandEntity2);
        assertThat(allBrands.size()).isEqualTo(2);
    }

    @DisplayName("브랜드 정보 수정")
    @Test
    void update() {
        BrandEntity originalBrandEntity = BrandEntity.builder()
                .id(1L)
                .name("Nike")
                .build();

        myBatisBrandRepository.save(originalBrandEntity);

        BrandEntity updatedBrandEntity = BrandEntity.builder()
                .id(1L)
                .name("Adidas")
                .build();

        when(brandMapper.findById(1L)).thenReturn(updatedBrandEntity);
        when(brandMapper.update(1L, updatedBrandEntity)).thenReturn(updatedBrandEntity);

        myBatisBrandRepository.update(1L, updatedBrandEntity);

        BrandEntity foundBrand = myBatisBrandRepository.findById(1L);
        assertThat(foundBrand.getName()).isEqualTo(updatedBrandEntity.getName());
    }

    @DisplayName("브랜드 정보 삭제")
    @Test
    void delete() {
    }
}