package com.api.repository;

import com.api.entity.Authority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryAuthority extends CrudRepository<Authority,Long> {
    Authority findByAuthority(String authority);
}
