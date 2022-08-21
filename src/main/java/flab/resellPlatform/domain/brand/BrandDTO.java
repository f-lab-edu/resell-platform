package flab.resellPlatform.domain.brand;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Builder
public class BrandDTO {

    @NotBlank
    private String name;

}
