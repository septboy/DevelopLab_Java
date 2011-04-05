package kr.arawn.springframework.data.sqlmap.ibatis.repository.factory;

import java.io.Serializable;

import kr.arawn.springframework.data.sqlmap.repository.SqlmapRepository;

import org.springframework.data.repository.support.RepositoryFactorySupport;
import org.springframework.data.repository.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.util.Assert;

import com.ibatis.sqlmap.client.SqlMapClient;


public class SqlMapClientRepositoryFactoryBean<T extends SqlmapRepository<S, ID>, S, ID extends Serializable>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {
    
    private SqlMapClient sqlmapExecutor;
    private SqlMapClientTemplate sqlMapClientTemplate;
    
    public void setSqlmapExecutor(SqlMapClient sqlmapExecutor) {
        this.sqlmapExecutor = sqlmapExecutor;
    }
    
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new SqlmapClientRepositoryFactory(sqlMapClientTemplate);
    }
    
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(sqlmapExecutor, "SqlMapClient[sqlmapExecutor] must not be null!");
        sqlMapClientTemplate = new SqlMapClientTemplate(sqlmapExecutor);
        super.afterPropertiesSet();
    }    

}
