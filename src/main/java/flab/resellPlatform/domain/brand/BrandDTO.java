package flab.resellPlatform.domain.brand;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class BrandDTO {

    @NotBlank
    private String name;

}
