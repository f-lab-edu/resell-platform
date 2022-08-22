package flab.resellPlatform.controller.brand;

import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.brand.BrandDTO;
import flab.resellPlatform.domain.user.Role;
import flab.resellPlatform.service.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
public class BrandController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final BrandService brandService;

    @GetMapping
    public StandardResponse viewAllBrands() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message("brand.view.all")
                .data(Map.of("brands", brandService.viewAllBrands().orElseThrow()))
                .build();

        return standardResponse;
    }

    @PostMapping
    @PreAuthorize(Role.ADMIN)
    public StandardResponse addBrand(@Valid @ModelAttribute BrandDTO brandDTO) {
        brandService.addBrand(brandDTO);

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("brand.add.success"))
                .build();

        return standardResponse;
    }

    @PutMapping("/{brandId}")
    @PreAuthorize(Role.ADMIN)
    public StandardResponse updateBrand(@PathVariable Long brandId, BrandDTO brandDTO) {
        if (brandService.updateBrand(brandId, brandDTO)) {
            return StandardResponse.builder()
                    .message(messageSourceAccessor.getMessage("brand.update.success"))
                    .build();
        } else {
            return StandardResponse.builder()
                    .message(messageSourceAccessor.getMessage("brand.update.failure"))
                    .build();
        }
    }

    @DeleteMapping("/{brandId}")
    @PreAuthorize(Role.ADMIN)
    public StandardResponse deleteBrand(@PathVariable Long brandId) {
        if (brandService.deleteBrand(brandId)) {
            return StandardResponse.builder()
                    .message(messageSourceAccessor.getMessage("brand.delete.success"))
                    .build();
        } else {
            return StandardResponse.builder()
                    .message(messageSourceAccessor.getMessage("brand.delete.failure"))
                    .build();
        }
    }


}
