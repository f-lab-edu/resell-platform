package flab.resellPlatform.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.ThreadLocalStandardResponseBucketHolder;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.response.StandardResponseBucket;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StandardResponseConvertFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        chain.doFilter(request, response);

        // get data to be used in response
        StandardResponseBucket standardResponseBucket = ThreadLocalStandardResponseBucketHolder.getResponse();
        HttpStatus httpStatus = standardResponseBucket.getHttpStatus();
        StandardResponse standardResponse = standardResponseBucket.getStandardResponse();

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;


        System.out.println(standardResponse.getMessage());
        System.out.println(standardResponse.getData());

        // write the data in a response
        httpServletResponse.setStatus(httpStatus.value());
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        standardResponse));
        response.getWriter().flush();

        // clear thread context
        ThreadLocalStandardResponseBucketHolder.clearContext();
    }
}
