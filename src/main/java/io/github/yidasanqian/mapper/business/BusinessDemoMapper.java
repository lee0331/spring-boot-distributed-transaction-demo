package io.github.yidasanqian.mapper.business;

import io.github.yidasanqian.domain.Demo;
import org.springframework.stereotype.Repository;


@Repository
public interface BusinessDemoMapper {
    int save(Demo dsDemo);
}
