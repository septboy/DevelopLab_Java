package kr.arawn.springframework.data.sqlmap.repository.factory;

import java.io.Serializable;

import kr.arawn.springframework.data.sqlmap.repository.SqlmapRepository;

import org.springframework.data.repository.support.EntityInformation;
import org.springframework.data.repository.support.RepositoryFactorySupport;


public abstract class SqlmapRepositoryFactory extends RepositoryFactorySupport {

    @Override
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(
            Class<T> domainClass) {
        return new SqlmapEntityInformation<T, ID>(domainClass);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(Class<?> repositoryInterface) {
        return SqlmapRepository.class;
    }

}
