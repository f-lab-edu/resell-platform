package flab.resellPlatform.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomJWT {
    final private String username;
    final private String expiredTime;
}
