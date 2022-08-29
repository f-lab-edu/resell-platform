package flab.resellPlatform.repository.brand;

import flab.resellPlatform.domain.brand.BrandEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisBrandRepository implements BrandRepository {

    private final BrandMapper brandMapper;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public BrandEntity save(BrandEntity brandEntity) {
        try {
            return brandMapper.save(brandEntity);
        } catch (SQLException e) {
            throw new DuplicateKeyException(messageSourceAccessor.getMessage("brand.name.duplicated"));
        }
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
