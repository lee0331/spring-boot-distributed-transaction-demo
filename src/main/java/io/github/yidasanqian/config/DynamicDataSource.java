package io.github.yidasanqian.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author Linyu Chen
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    // primary库
    public static final String DATA_SOURCE_PRIMARY = "primaryDataSource";
    // business库
    public static final String DATA_SOURCE_BUSINESS = "businessDataSource";

    private static final ThreadLocal<String> dataSourceHolder = new InheritableThreadLocal<String>();

    public static void setDataSourceKey(String dataSource) {
        dataSourceHolder.set(dataSource);
    }

    public static String getDataSourceKey() {
        return dataSourceHolder.get();
    }

    public static void clear() {
        dataSourceHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceKey();
    }
}
