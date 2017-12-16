package io.github.yidasanqian.mapper.master;

import io.github.yidasanqian.domain.Demo;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterDemoMapper {
    int save(Demo dsDemo);
}
